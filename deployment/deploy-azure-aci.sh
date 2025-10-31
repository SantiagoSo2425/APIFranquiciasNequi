#!/bin/bash

# Deploy to Azure Container Instances (Simple, serverless containers)
# Ideal for: Development, testing, simple workloads

set -e

PROJECT_NAME="franquicias-nequi"
AZURE_REGION="${AZURE_REGION:-eastus}"
RESOURCE_GROUP="rg-${PROJECT_NAME}-dev"
ACR_NAME="acr${PROJECT_NAME//-/}dev"
KEY_VAULT_NAME="kv-${PROJECT_NAME}-dev"
CONTAINER_NAME="${PROJECT_NAME}-api"
DNS_NAME_LABEL="${PROJECT_NAME}-api-${RANDOM}"

echo "========================================="
echo "  Deploy to Azure Container Instances"
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

# Create Container Instance
echo "Creating Azure Container Instance..."
az container create \
  --resource-group ${RESOURCE_GROUP} \
  --name ${CONTAINER_NAME} \
  --image ${ACR_LOGIN_SERVER}/${PROJECT_NAME}-api:latest \
  --registry-login-server ${ACR_LOGIN_SERVER} \
  --registry-username ${ACR_USERNAME} \
  --registry-password ${ACR_PASSWORD} \
  --dns-name-label ${DNS_NAME_LABEL} \
  --ports 8080 \
  --cpu 1 \
  --memory 2 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    SPRING_DATA_MONGODB_URI="${MONGODB_URI}" \
    SPRING_DATA_MONGODB_DATABASE=franquicias \
  --restart-policy Always

# Get FQDN
FQDN=$(az container show \
  --resource-group ${RESOURCE_GROUP} \
  --name ${CONTAINER_NAME} \
  --query ipAddress.fqdn \
  --output tsv)

echo ""
echo "========================================="
echo "  Deployment Complete!"
echo "========================================="
echo ""
echo "API URL: http://${FQDN}:8080"
echo "Health Check: http://${FQDN}:8080/actuator/health"
echo ""
echo "View logs:"
echo "  az container logs --resource-group ${RESOURCE_GROUP} --name ${CONTAINER_NAME} --follow"
echo ""
echo "Stop container:"
echo "  az container stop --resource-group ${RESOURCE_GROUP} --name ${CONTAINER_NAME}"
echo ""
echo "Delete container:"
echo "  az container delete --resource-group ${RESOURCE_GROUP} --name ${CONTAINER_NAME} --yes"
echo "========================================="

