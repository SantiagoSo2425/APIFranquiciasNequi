#!/bin/bash

# Deployment script for Azure Container Instances
# Prerequisites: Azure CLI, Docker configured

set -e

# Configuration
PROJECT_NAME="franquicias-nequi"
AZURE_REGION="${AZURE_REGION:-eastus}"
RESOURCE_GROUP="rg-${PROJECT_NAME}-dev"
ACR_NAME="acr${PROJECT_NAME//-/}dev"
IMAGE_TAG="${IMAGE_TAG:-latest}"
IMAGE_NAME="${PROJECT_NAME}-api"

echo "========================================="
echo "  FranquiciasAPI - Azure Deployment"
echo "========================================="
echo "Region: $AZURE_REGION"
echo "Resource Group: $RESOURCE_GROUP"
echo "ACR: $ACR_NAME"
echo "Image Tag: $IMAGE_TAG"
echo "========================================="

# Step 1: Login to Azure
echo ""
echo "[1/7] Logging into Azure..."
az login

# Step 2: Set subscription (if you have multiple)
# az account set --subscription "YOUR_SUBSCRIPTION_NAME"

# Step 3: Get ACR credentials
echo ""
echo "[2/7] Getting ACR credentials..."
ACR_LOGIN_SERVER=$(az acr show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query loginServer --output tsv)
ACR_USERNAME=$(az acr credential show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query username --output tsv)
ACR_PASSWORD=$(az acr credential show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query passwords[0].value --output tsv)

echo "ACR Login Server: ${ACR_LOGIN_SERVER}"

# Step 4: Login to ACR
echo ""
echo "[3/7] Logging into ACR..."
echo ${ACR_PASSWORD} | docker login ${ACR_LOGIN_SERVER} --username ${ACR_USERNAME} --password-stdin

# Step 5: Build Docker Image
echo ""
echo "[4/7] Building Docker image..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f deployment/Dockerfile .

# Step 6: Tag Image
echo ""
echo "[5/7] Tagging image..."
docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${IMAGE_TAG}
docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ACR_LOGIN_SERVER}/${IMAGE_NAME}:latest

# Step 7: Push to ACR
echo ""
echo "[6/7] Pushing image to ACR..."
docker push ${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${IMAGE_TAG}
docker push ${ACR_LOGIN_SERVER}/${IMAGE_NAME}:latest

# Step 8: Get MongoDB URI from Key Vault
echo ""
echo "[7/7] Retrieving MongoDB connection string from Key Vault..."
KEY_VAULT_NAME="kv-${PROJECT_NAME}-dev"
MONGODB_URI=$(az keyvault secret show \
  --vault-name ${KEY_VAULT_NAME} \
  --name mongodb-connection-string \
  --query value \
  --output tsv)

echo ""
echo "========================================="
echo "  Deployment Completed Successfully!"
echo "========================================="
echo ""
echo "Container Image: ${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${IMAGE_TAG}"
echo ""
echo "Next steps:"
echo ""
echo "1. Deploy to Azure Container Instances:"
echo "   ./deployment/deploy-azure-aci.sh"
echo ""
echo "2. Or deploy to Azure Container Apps:"
echo "   ./deployment/deploy-azure-container-apps.sh"
echo ""
echo "3. Or deploy to Azure Kubernetes Service:"
echo "   ./deployment/deploy-azure-aks.sh"
echo ""
echo "MongoDB connection string stored in Key Vault:"
echo "  Vault: ${KEY_VAULT_NAME}"
echo "  Secret: mongodb-connection-string"
echo "========================================="

