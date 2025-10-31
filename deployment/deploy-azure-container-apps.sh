#!/bin/bash

# Deploy to Azure Container Apps (Modern serverless containers with scaling)
# Ideal for: Production workloads, auto-scaling, microservices

set -e

PROJECT_NAME="franquicias-nequi"
AZURE_REGION="${AZURE_REGION:-eastus}"
RESOURCE_GROUP="rg-${PROJECT_NAME}-dev"
ACR_NAME="acr${PROJECT_NAME//-/}dev"
KEY_VAULT_NAME="kv-${PROJECT_NAME}-dev"
CONTAINER_APP_ENV="${PROJECT_NAME}-env"
CONTAINER_APP_NAME="${PROJECT_NAME}-api"

echo "========================================="
echo "  Deploy to Azure Container Apps"
echo "========================================="

# Get ACR credentials
ACR_LOGIN_SERVER=$(az acr show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query loginServer --output tsv)
ACR_USERNAME=$(az acr credential show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query username --output tsv)
ACR_PASSWORD=$(az acr credential show --name ${ACR_NAME} --resource-group ${RESOURCE_GROUP} --query passwords[0].value --output tsv)

# Get MongoDB connection string
MONGODB_URI=$(az keyvault secret show \
  --vault-name ${KEY_VAULT_NAME} \
  --name mongodb-connection-string \
  --query value \
  --output tsv)

# Create Container Apps Environment (if not exists)
echo "Creating Container Apps Environment..."
az containerapp env create \
  --name ${CONTAINER_APP_ENV} \
  --resource-group ${RESOURCE_GROUP} \
  --location ${AZURE_REGION} \
  --only-show-errors || true

# Create/Update Container App
echo "Deploying Container App..."
az containerapp create \
  --name ${CONTAINER_APP_NAME} \
  --resource-group ${RESOURCE_GROUP} \
  --environment ${CONTAINER_APP_ENV} \
  --image ${ACR_LOGIN_SERVER}/${PROJECT_NAME}-api:latest \
  --registry-server ${ACR_LOGIN_SERVER} \
  --registry-username ${ACR_USERNAME} \
  --registry-password ${ACR_PASSWORD} \
  --target-port 8080 \
  --ingress external \
  --cpu 1.0 \
  --memory 2.0Gi \
  --min-replicas 1 \
  --max-replicas 5 \
  --env-vars \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATA_MONGODB_URI="${MONGODB_URI}" \
    SPRING_DATA_MONGODB_DATABASE=franquicias \
  --revision-suffix v1

# Get App URL
APP_URL=$(az containerapp show \
  --name ${CONTAINER_APP_NAME} \
  --resource-group ${RESOURCE_GROUP} \
  --query properties.configuration.ingress.fqdn \
  --output tsv)

echo ""
echo "========================================="
echo "  Deployment Complete!"
echo "========================================="
echo ""
echo "API URL: https://${APP_URL}"
echo "Health Check: https://${APP_URL}/actuator/health"
echo ""
echo "View logs:"
echo "  az containerapp logs show --name ${CONTAINER_APP_NAME} --resource-group ${RESOURCE_GROUP} --follow"
echo ""
echo "Scale manually:"
echo "  az containerapp update --name ${CONTAINER_APP_NAME} --resource-group ${RESOURCE_GROUP} --min-replicas 2 --max-replicas 10"
echo ""
echo "Delete app:"
echo "  az containerapp delete --name ${CONTAINER_APP_NAME} --resource-group ${RESOURCE_GROUP} --yes"
echo "========================================="

