# Terraform configuration for MongoDB Atlas and Azure infrastructure
terraform {
  required_version = ">= 1.0"
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 1.14"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
}

# Variables
variable "mongodb_atlas_public_key" {
  description = "MongoDB Atlas Public API Key"
  type        = string
  sensitive   = true
}

variable "mongodb_atlas_private_key" {
  description = "MongoDB Atlas Private API Key"
  type        = string
  sensitive   = true
}

variable "mongodb_atlas_org_id" {
  description = "MongoDB Atlas Organization ID"
  type        = string
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "franquicias-nequi"
}

variable "azure_region" {
  description = "Azure Region"
  type        = string
  default     = "eastus"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}

# MongoDB Atlas Provider
provider "mongodbatlas" {
  public_key  = var.mongodb_atlas_public_key
  private_key = var.mongodb_atlas_private_key
}

# Azure Provider
provider "azurerm" {
  features {
    key_vault {
      purge_soft_delete_on_destroy = true
    }
  }
}

# Current Azure Client
data "azurerm_client_config" "current" {}

# Resource Group
resource "azurerm_resource_group" "franquicias_rg" {
  name     = "rg-${var.project_name}-${var.environment}"
  location = var.azure_region

  tags = {
    Environment = var.environment
    Project     = var.project_name
    ManagedBy   = "Terraform"
  }
}

# MongoDB Atlas Project
resource "mongodbatlas_project" "franquicias_project" {
  name   = "${var.project_name}-${var.environment}"
  org_id = var.mongodb_atlas_org_id
}

# MongoDB Atlas Cluster (Free Tier M0)
resource "mongodbatlas_cluster" "franquicias_cluster" {
  project_id = mongodbatlas_project.franquicias_project.id
  name       = "franquicias-cluster-${var.environment}"

  # Free Tier Configuration
  provider_name               = "TENANT"
  backing_provider_name       = "AZURE"
  provider_region_name        = "US_EAST_2"
  provider_instance_size_name = "M0"

  # MongoDB Version
  mongo_db_major_version = "7.0"

  # Auto Scaling (disabled for free tier)
  auto_scaling_disk_gb_enabled = false
}

# Database User
resource "mongodbatlas_database_user" "franquicias_user" {
  username           = "franquicias_app"
  password           = random_password.db_password.result
  project_id         = mongodbatlas_project.franquicias_project.id
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = "franquicias"
  }
}

# Random password for database user
resource "random_password" "db_password" {
  length  = 32
  special = true
}

# IP Whitelist (Allow from anywhere for demo - restrict in production)
resource "mongodbatlas_project_ip_access_list" "franquicias_ip" {
  project_id = mongodbatlas_project.franquicias_project.id
  cidr_block = "0.0.0.0/0"
  comment    = "Allow from anywhere - DEMO ONLY"
}

# Azure Key Vault
resource "azurerm_key_vault" "franquicias_kv" {
  name                       = "kv-${var.project_name}-${var.environment}"
  location                   = azurerm_resource_group.franquicias_rg.location
  resource_group_name        = azurerm_resource_group.franquicias_rg.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "standard"
  soft_delete_retention_days = 7
  purge_protection_enabled   = false

  access_policy {
    tenant_id = data.azurerm_client_config.current.tenant_id
    object_id = data.azurerm_client_config.current.object_id

    secret_permissions = [
      "Get",
      "List",
      "Set",
      "Delete",
      "Purge",
      "Recover"
    ]
  }

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# Store MongoDB connection string in Key Vault
resource "azurerm_key_vault_secret" "mongodb_connection_string" {
  name         = "mongodb-connection-string"
  value        = replace(
    mongodbatlas_cluster.franquicias_cluster.connection_strings[0].standard_srv,
    "mongodb+srv://",
    "mongodb+srv://${mongodbatlas_database_user.franquicias_user.username}:${random_password.db_password.result}@"
  )
  key_vault_id = azurerm_key_vault.franquicias_kv.id

  depends_on = [
    azurerm_key_vault.franquicias_kv
  ]
}

# Store MongoDB username
resource "azurerm_key_vault_secret" "mongodb_username" {
  name         = "mongodb-username"
  value        = mongodbatlas_database_user.franquicias_user.username
  key_vault_id = azurerm_key_vault.franquicias_kv.id
}

# Store MongoDB password
resource "azurerm_key_vault_secret" "mongodb_password" {
  name         = "mongodb-password"
  value        = random_password.db_password.result
  key_vault_id = azurerm_key_vault.franquicias_kv.id
}

# Store MongoDB database name
resource "azurerm_key_vault_secret" "mongodb_database" {
  name         = "mongodb-database"
  value        = "franquicias"
  key_vault_id = azurerm_key_vault.franquicias_kv.id
}

# Azure Container Registry
resource "azurerm_container_registry" "franquicias_acr" {
  name                = "acr${replace(var.project_name, "-", "")}${var.environment}"
  resource_group_name = azurerm_resource_group.franquicias_rg.name
  location            = azurerm_resource_group.franquicias_rg.location
  sku                 = "Basic"
  admin_enabled       = true

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

# Outputs
output "resource_group_name" {
  description = "Resource Group name"
  value       = azurerm_resource_group.franquicias_rg.name
}

output "mongodb_cluster_name" {
  description = "MongoDB Atlas cluster name"
  value       = mongodbatlas_cluster.franquicias_cluster.name
}

output "key_vault_name" {
  description = "Azure Key Vault name"
  value       = azurerm_key_vault.franquicias_kv.name
}

output "key_vault_uri" {
  description = "Azure Key Vault URI"
  value       = azurerm_key_vault.franquicias_kv.vault_uri
}

output "container_registry_name" {
  description = "Azure Container Registry name"
  value       = azurerm_container_registry.franquicias_acr.name
}

output "container_registry_login_server" {
  description = "Azure Container Registry login server"
  value       = azurerm_container_registry.franquicias_acr.login_server
}

output "mongodb_connection_secret_id" {
  description = "Key Vault secret ID for MongoDB connection string"
  value       = azurerm_key_vault_secret.mongodb_connection_string.id
}
