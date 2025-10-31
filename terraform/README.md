# Terraform for FranquiciasAPI - MongoDB Atlas & Azure

This directory contains Terraform configuration to provision:
- MongoDB Atlas Cluster (Free Tier M0) on Azure region
- Database User with credentials
- IP Whitelist configuration
- Azure Key Vault to store MongoDB credentials
- Azure Container Registry for Docker images

## Prerequisites

1. **MongoDB Atlas Account**
   - Sign up at https://cloud.mongodb.com
   - Create an Organization
   - Generate API Keys (Organization Access Manager)

2. **Azure Account**
   - Azure CLI installed (`az --version`)
   - Active subscription
   - Login: `az login`

3. **Terraform**
   - Install Terraform >= 1.0

## Setup

1. **Configure Credentials**
   ```bash
   # Copy example file
   cp terraform-azure.tfvars.example terraform-azure.tfvars
   
   # Edit with your credentials
   nano terraform-azure.tfvars
   # - MongoDB Atlas Public/Private keys
   # - MongoDB Atlas Organization ID
   # - Azure region (default: eastus)
   ```

2. **Login to Azure**
   ```bash
   az login
   ```

3. **Initialize Terraform**
   ```bash
   terraform init
   ```

4. **Review Plan**
   ```bash
   terraform plan -var-file="terraform-azure.tfvars"
   ```

5. **Apply Infrastructure**
   ```bash
   terraform apply -var-file="terraform-azure.tfvars"
   ```

6. **Get MongoDB Connection String**
   ```bash
   # From Azure Key Vault
   az keyvault secret show \
     --vault-name kv-franquicias-nequi-dev \
     --name mongodb-connection-string \
     --query value \
     --output tsv
   ```

## What Gets Created

### MongoDB Atlas
- **Project**: `franquicias-nequi-dev`
- **Cluster**: `franquicias-cluster-dev` (M0 Free Tier on Azure)
- **Region**: `AZURE_EASTUS2`
- **Database User**: `franquicias_app` (auto-generated password)
- **Database**: `franquicias`
- **IP Whitelist**: `0.0.0.0/0` (allow all - for demo)

### Azure Resources
- **Resource Group**: `rg-franquicias-nequi-dev`
- **Key Vault**: `kv-franquicias-nequi-dev`
  - Secrets: connection-string, username, password, database
- **Container Registry**: `acrfranquiciasnequidev`
  - SKU: Basic
  - Admin enabled

## Using with Application

### Local Development (Docker)
Update `application.yaml` or use environment variables:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}  # From Azure Key Vault
      database: franquicias
```

### Azure Container Apps
The deployment scripts automatically retrieve the connection string from Key Vault.

```bash
# Get connection string
MONGODB_URI=$(az keyvault secret show \
  --vault-name kv-franquicias-nequi-dev \
  --name mongodb-connection-string \
  --query value \
  --output tsv)

# Use in container
az containerapp create \
  --name franquicias-api \
  --environment-variables SPRING_DATA_MONGODB_URI="${MONGODB_URI}"
```

## Costs

- **MongoDB Atlas M0**: **FREE** (512 MB storage)
- **Azure Key Vault**: ~$0.03/month (10,000 operations)
- **Azure Container Registry (Basic)**: ~$5/month

**Total**: ~$5/month

## Cleanup

```bash
terraform destroy -var-file="terraform-azure.tfvars"
```

## Production Recommendations

1. **IP Whitelist**: Restrict to your Azure VNet CIDR ranges
2. **Cluster Tier**: Upgrade from M0 for production workloads
3. **Backup**: Enable continuous backups in MongoDB Atlas
4. **Monitoring**: Configure alerts in MongoDB Atlas and Azure Monitor
5. **Secrets**: Use Managed Identities instead of passwords
6. **Multi-Region**: Deploy in multiple Azure regions for HA
7. **Key Vault**: Enable soft delete and purge protection

## Outputs

After `terraform apply`, you'll get:
- `resource_group_name`: Azure Resource Group name
- `mongodb_cluster_name`: Name of the MongoDB cluster
- `key_vault_name`: Azure Key Vault name
- `key_vault_uri`: Key Vault URI
- `container_registry_name`: ACR name
- `container_registry_login_server`: ACR login server URL

## Security Notes

- Never commit `terraform-azure.tfvars` to version control
- Store API keys in environment variables or CI/CD secrets
- Use Azure Managed Identities for application access to Key Vault
- Rotate MongoDB Atlas API keys regularly
- Enable Azure Policy for compliance

