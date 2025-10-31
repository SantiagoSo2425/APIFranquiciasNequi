# Guía de Despliegue en Azure para FranquiciasAPI

Guía completa para desplegar la FranquiciasAPI en la nube de Microsoft Azure utilizando Container Apps y MongoDB Atlas.

mongodb_atlas_public_key  = "TU_PUBLIC_KEY_AQUI"          # ej: tdglkxub
mongodb_atlas_private_key = "TU_PRIVATE_KEY_AQUI"         # ej: d23396dd-1b38-4497-b312-1089751d7172
mongodb_atlas_org_id      = "TU_ORGANIZATION_ID_AQUI"     # ej: 682773aac80f84577f517fba (NO el Project ID)
   - **Docker Desktop**: https://www.docker.com/products/docker-desktop/
# Project Configuration
project_name = "franquicias-nequi"
environment  = "dev"

# Azure Configuration
azure_region = "eastus"  # Puedes cambiar a: westus, westeurope, etc.
```

### Paso 2: Compilar y Subir Imagen Docker a ACR

Ahora vamos a compilar la aplicación Java y crear una imagen Docker.

#### 2.1 Compilar la Aplicación

Vuelve al directorio raíz del proyecto:
```bash
cd ..  # Salir de la carpeta terraform
```

Compila el proyecto (sin ejecutar tests):
```bash
# Windows
gradle clean build -x test

# Linux/Mac
./gradlew clean build -x test
```

⏱️ **Tiempo estimado: 1-2 minutos**

**Verificar que el JAR se creó:**
```bash
# Windows (PowerShell)
Get-ChildItem -Path "applications\app-service\build\libs" -Filter "*.jar"

# Linux/Mac
ls -lh applications/app-service/build/libs/*.jar
```

Deberías ver: `FranquiciasAPI.jar` (~50-60 MB)

#### 2.2 Iniciar Sesión en Azure Container Registry

⚠️ **IMPORTANTE**: Asegúrate de usar el **Organization ID**, NO el Project ID.
az acr login --name acrfranquiciasnequidev
```

**Salida esperada:**
```
Login Succeeded
```
   - **Terraform**: https://developer.hashicorp.com/terraform/install
#### 2.3 Compilar la Imagen Docker
- Se abrirá tu navegador
```bash
docker build -t franquicias-api:v1.0.0 -f deployment/Dockerfile .
- Presiona Enter para usar la suscripción por defecto

⏱️ **Tiempo estimado: 30-60 segundos** (la primera vez puede tardar más)
     - Verifica: `terraform --version` (debe ser >= 1.0)
**Progreso que verás:**
```
[+] Building 5.2s (10/10) FINISHED
 => [internal] load build definition from Dockerfile
 => [internal] load .dockerignore
 => [internal] load metadata for docker.io/library/eclipse-temurin:21-jre-alpine
 => [1/5] FROM docker.io/library/eclipse-temurin:21-jre-alpine
 => [2/5] WORKDIR /app
 => [3/5] RUN addgroup -S appgroup && adduser -S appuser -G appgroup
 => [4/5] COPY applications/app-service/build/libs/*.jar FranquiciasAPI.jar
 => [5/5] RUN chown -R appuser:appgroup /app
 => exporting to image
 => => naming to docker.io/library/franquicias-api:v1.0.0
```

#### 2.4 Etiquetar la Imagen para ACR

```bash
# Etiquetar con versión específica
docker tag franquicias-api:v1.0.0 acrfranquiciasnequidev.azurecr.io/franquicias-api:v1.0.0

# Etiquetar con 'latest'
docker tag franquicias-api:v1.0.0 acrfranquiciasnequidev.azurecr.io/franquicias-api:latest
```

#### 2.5 Subir la Imagen a ACR

```bash
# Subir versión específica
docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:v1.0.0

# Subir latest
docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:latest
```

⏱️ **Tiempo estimado: 1-3 minutos** (depende de tu conexión a internet)

**Progreso que verás:**
```
The push refers to repository [acrfranquiciasnequidev.azurecr.io/franquicias-api]
80f92e557a53: Pushed
34074eb54496: Pushed
361a9ad12837: Pushed
...
v1.0.0: digest: sha256:52c15358a12d2902a4053... size: 856
```

✅ **¡Imagen Docker subida exitosamente!**

**Verificar que la imagen está en ACR:**
```bash
az acr repository show-tags \
  --name acrfranquiciasnequidev \
  --repository franquicias-api \
  --output table
```

Deberías ver:
```
Result
--------
### Paso 3: Desplegar en Azure Container Apps

Ahora vamos a desplegar la aplicación en Azure Container Apps (opción recomendada con auto-escalado).

#### 3.1 Obtener la Cadena de Conexión de MongoDB

```bash
az keyvault secret show \
  --vault-name kv-franquicias-nequi-dev \
  --name mongodb-connection-string \
  --query value \
  -o tsv
```

**Salida esperada:**
```
mongodb+srv://franquicias_app:{contraseña}@franquicias-cluster-dev.8rguath.mongodb.net
```

💾 **Guarda esta cadena** (la necesitarás en el siguiente paso)

#### 3.2 Crear el Entorno de Container Apps

```bash
az containerapp env create \
  --name franquicias-nequi-env \
  --resource-group rg-franquicias-nequi-dev \
  --location eastus
```

⏱️ **Tiempo estimado: 2-3 minutos**

**Salida esperada:**
```
Container Apps environment created. To deploy a container app, use: az containerapp create --help
```

**¿Qué se creó?**
- ✅ Entorno de Container Apps
- ✅ Log Analytics Workspace (para logs)
- ✅ Dominio por defecto: `*.blueplant-xxxxx.eastus.azurecontainerapps.io`
v1.0.0
#### 3.3 Obtener Credenciales de ACR

```bash
az acr credential show \
  --name acrfranquiciasnequidev \
  --resource-group rg-franquicias-nequi-dev
```

**Salida esperada:**
```json
{
  "passwords": [
    {
      "name": "password",
      "value": "YOUR_ACR_PASSWORD_HERE_EXAMPLE_ONLY"
    },
    {
      "name": "password2",
      "value": "YOUR_ACR_PASSWORD2_HERE_EXAMPLE_ONLY"
    }
  ],
  "username": "acrfranquiciasnequidev"
}
```

💾 **Guarda el `username` y una de las `passwords`**

#### 3.4 Crear el Container App

**⚠️ IMPORTANTE**: Reemplaza `[MONGODB_CONNECTION_STRING]` y `[ACR_PASSWORD]` con los valores reales que obtuviste en los pasos anteriores.

```bash
az containerapp create \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --environment franquicias-nequi-env \
  --image acrfranquiciasnequidev.azurecr.io/franquicias-api:latest \
  --registry-server acrfranquiciasnequidev.azurecr.io \
  --registry-username acrfranquiciasnequidev \
  --registry-password "[TU_PASSWORD_DE_ACR]" \
  --target-port 8080 \
  --ingress external \
  --cpu 1.0 \
  --memory 2.0Gi \
  --min-replicas 1 \
  --max-replicas 5 \
  --env-vars \
    "SPRING_PROFILES_ACTIVE=prod" \
    "SPRING_DATA_MONGODB_URI=[MONGODB_CONNECTION_STRING]" \
    "SPRING_DATA_MONGODB_DATABASE=franquicias"
```

**Nota**: NO copies el ejemplo de abajo literalmente. Los valores entre corchetes `[...]` deben ser reemplazados con tus credenciales reales obtenidas en los pasos 3.1 y 3.3.
    "SPRING_DATA_MONGODB_URI=mongodb+srv://franquicias_app:{password}@franquicias-cluster-dev.8rguath.mongodb.net" \
    "SPRING_DATA_MONGODB_DATABASE=franquicias"
```

⏱️ **Tiempo estimado: 2-3 minutos**

**Progreso que verás:**
```
Adding registry password as a secret...
Container app created. Access your app at https://franquicias-nequi-api.blueplant-xxxxx.eastus.azurecontainerapps.io/
```

**Salida final:**
```json
{
  "properties": {
    "provisioningState": "Succeeded",
    "runningStatus": "Running",
    "configuration": {
      "ingress": {
        "fqdn": "franquicias-nequi-api.blueplant-xxxxx.eastus.azurecontainerapps.io",
        "external": true,
        "targetPort": 8080
      }
    }
  }
}
```

✅ **¡Aplicación desplegada exitosamente!**

💾 **Guarda la URL (FQDN)** que aparece en `"fqdn"`:
```
https://franquicias-nequi-api.blueplant-xxxxx.eastus.azurecontainerapps.io
```

### Paso 4: Probar tu Despliegue

### Paso 3: Desplegar en Azure Container Apps

```bash
   - **Gradle**: Ya incluido en el proyecto
```

**Salida esperada:**
```
Initializing the backend...
Initializing provider plugins...
- Installing mongodb/mongodbatlas v1.41.1...
- Installing hashicorp/azurerm v3.117.1...
- Installing hashicorp/random v3.7.2...
   
Terraform has been successfully initialized!
```

#### 1.4 Revisar el Plan de Terraform

```bash
   ```bash
```

**¿Qué verás?**
Terraform te mostrará **12 recursos** que se crearán:
- 1 Resource Group de Azure
- 1 Azure Key Vault
- 1 Azure Container Registry
- 4 Secretos en Key Vault
- 1 Proyecto MongoDB Atlas
- 1 Cluster MongoDB M0 (GRATIS)
- 1 Usuario de base de datos
- 1 IP Whitelist
- 1 Password aleatorio
   git clone <tu-repositorio>
**Salida esperada:**
```
Plan: 12 to add, 0 to change, 0 to destroy.
```

#### 1.5 Aplicar la Infraestructura

```bash
   ```

| Método | Endpoint | Body (JSON) |
**Te preguntará:**
```
Do you want to perform these actions?
  Enter a value:
```

**Escribe `yes` y presiona Enter**

⏱️ **Tiempo estimado: 5-10 minutos**

**Progreso que verás:**
```
random_password.db_password: Creating...
mongodbatlas_project.franquicias_project: Creating...
azurerm_resource_group.franquicias_rg: Creating...
azurerm_key_vault.franquicias_kv: Creating...
mongodbatlas_cluster.franquicias_cluster: Creating... (este tarda más)
...
Apply complete! Resources: 12 added, 0 changed, 0 destroyed.
```

**Al finalizar, verás los outputs:**
```
Outputs:

container_registry_login_server = "acrfranquiciasnequidev.azurecr.io"
container_registry_name = "acrfranquiciasnequidev"
key_vault_name = "kv-franquicias-nequi-dev"
key_vault_uri = "https://kv-franquicias-nequi-dev.vault.azure.net/"
mongodb_cluster_name = "franquicias-cluster-dev"
mongodb_connection_secret_id = "https://kv-franquicias-nequi-dev.vault.azure.net/secrets/..."
resource_group_name = "rg-franquicias-nequi-dev"
```

✅ **¡Infraestructura creada exitosamente!**

---

**Lo que se creó:**
- ✅ Resource Group: `rg-franquicias-nequi-dev`
- ✅ Key Vault: `kv-franquicias-nequi-dev` (con credenciales de MongoDB)
- ✅ Container Registry: `acrfranquiciasnequidev.azurecr.io`
- ✅ MongoDB Atlas Cluster M0 (GRATIS): `franquicias-cluster-dev`
- ✅ Usuario MongoDB: `franquicias_app`
- ✅ Base de datos: `franquicias`
| `GET` | `/api/franchises/{id}/highest-stock-products` | (sin body) |

### 📊 **Monitoreo en Tiempo Real**

Ver logs de la aplicación:
```bash
az containerapp logs show \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --follow
```

---

## 🚀 **Guía Completa de Despliegue (Paso a Paso)**

Si quieres desplegar tu propia instancia, sigue estos pasos exactos:

## 🏗️ Descripción de la Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                   Microsoft Azure                        │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │          Azure Front Door (Opcional)               │ │
│  │          CDN + WAF + SSL/TLS                       │ │
│  └─────────────────────┬──────────────────────────────┘ │
│                        │                                 │
│  ┌─────────────────────▼──────────────────────────────┐ │
│  │    Entorno de Azure Container Apps                │ │
│  │  ┌──────────────┐  ┌──────────────┐               │ │
│  │  │ Contenedor   │  │ Contenedor   │               │ │
│  │  │ Instancia 1  │  │ Instancia 2  │  Auto-escala │ │
│  │  │ Franquicias  │  │ Franquicias  │  (1-10)      │ │
│  │  │     API      │  │     API      │               │ │
│  │  └──────────────┘  └──────────────┘               │ │
│  └────────────────────────────────────────────────────┘ │
│                        │                                 │
│  ┌─────────────────────▼──────────────────────────────┐ │
│  │            Azure Key Vault                         │ │
│  │  - Cadena de Conexión MongoDB                      │ │
│  │  - Credenciales de Base de Datos                   │ │
│  │  - Secretos de Aplicación                          │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │   Azure Container Registry (ACR)                   │ │
│  │      (Almacenamiento de Imágenes Docker)           │ │
│  └────────────────────────────────────────────────────┘ │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │      Azure Monitor + Log Analytics                 │ │
│  │   (Logs, Métricas, Application Insights)           │ │
│  └────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
                        │
                        │ (Internet)
                        │
┌────────────────────────▼──────────────────────────────┐
│              MongoDB Atlas                            │
│         (Cluster MongoDB Administrado)                │
│              Región: AZURE_EASTUS2                    │
└───────────────────────────────────────────────────────┘
```

## 📋 Requisitos Previos

1. **Cuenta de Azure**
   - Azure CLI instalado (`az --version`)
   - Suscripción activa
   - Permisos suficientes

2. **Cuenta de MongoDB Atlas**
   - Organización creada
   - Claves API generadas

3. **Herramientas**
   - Docker Desktop
   - Terraform >= 1.0
   - Azure CLI >= 2.50
   - jq (para parsear JSON) - opcional

## 🚀 Pasos de Despliegue

### Paso 1: Provisionar Infraestructura con Terraform

```bash
cd terraform

# Configurar credenciales
cp terraform-azure.tfvars.example terraform-azure.tfvars

# Editar con tus credenciales:
# - Claves API de MongoDB Atlas
# - Región de Azure preferida
nano terraform-azure.tfvars

# Iniciar sesión en Azure
az login

# Inicializar Terraform
terraform init

# Revisar el plan
terraform plan -var-file="terraform-azure.tfvars"

# Aplicar infraestructura
terraform apply -var-file="terraform-azure.tfvars"
```

**Lo que esto crea:**
- **Grupo de Recursos**: `rg-franquicias-nequi-dev`
- **Cluster MongoDB Atlas M0** (Tier GRATUITO) en Azure
- **Azure Key Vault**: Almacena credenciales de MongoDB
- **Azure Container Registry**: Para imágenes Docker
- **Identidades Administradas**: Para acceso seguro

### Paso 2: Compilar y Subir Imagen Docker a ACR

```bash
# Hacer los scripts ejecutables
chmod +x deployment/deploy-azure.sh
chmod +x deployment/deploy-azure-aci.sh
chmod +x deployment/deploy-azure-container-apps.sh

# Configurar entorno (opcional)
export AZURE_REGION=eastus
export IMAGE_TAG=v1.0.0

# Ejecutar script de despliegue
./deployment/deploy-azure.sh
```

**Lo que esto hace:**
- Inicia sesión en Azure
- Obtiene credenciales de ACR
- Compila imagen Docker localmente
- Sube imagen a Azure Container Registry
- Recupera cadena de conexión de MongoDB desde Key Vault

### Paso 3: Desplegar en Azure

Tienes **3 opciones de despliegue**:

#### Opción A: Azure Container Instances (ACI) - La Más Simple

**Ideal para**: Desarrollo, pruebas, cargas de trabajo simples

```bash
./deployment/deploy-azure-aci.sh
```

**Características:**
- ✅ Despliegue más rápido
- ✅ Pago por segundo
- ✅ No requiere orquestación
- ✅ IP pública + DNS
- ❌ Sin auto-escalado
- ❌ Sin balanceo de carga

**Costo**: ~$30-40/mes

#### Opción B: Azure Container Apps - Recomendado ⭐

**Ideal para**: Producción, microservicios, auto-escalado

```bash
./deployment/deploy-azure-container-apps.sh
```

**Características:**
- ✅ Auto-escalado (1-10 réplicas)
- ✅ Balanceo de carga integrado
- ✅ HTTPS/SSL automático
- ✅ Escalado de cero a N
- ✅ Entorno administrado
- ✅ Monitoreo integrado

**Costo**: ~$15-50/mes (escala con el uso)

#### Opción C: Azure Kubernetes Service (AKS) - Avanzado

**Ideal para**: Microservicios complejos, control total

```bash
# Crear cluster AKS
az aks create \
  --resource-group rg-franquicias-nequi-dev \
  --name franquicias-aks \
  --node-count 2 \
  --enable-managed-identity \
  --attach-acr acrfranquiciasnequidev

# Desplegar con kubectl
kubectl apply -f deployment/kubernetes/
```

**Costo**: ~$75+/mes

### Paso 4: Probar tu Despliegue

```bash
# Para Container Instances
FQDN=$(az container show \
  --resource-group rg-franquicias-nequi-dev \
  --name franquicias-nequi-api \
  --query ipAddress.fqdn \
  --output tsv)

curl http://${FQDN}:8080/actuator/health

# Para Container Apps
APP_URL=$(az containerapp show \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --query properties.configuration.ingress.fqdn \
  --output tsv)

curl https://${APP_URL}/actuator/health

# Probar API
curl -X POST https://${APP_URL}/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Franquicia de Prueba Azure"}'
```

## 🔒 Configuración de Seguridad

### Identidad Administrada (Recomendado)

Habilitar Identidad Administrada para acceso seguro a Key Vault:

```bash
# Para Container Apps
az containerapp identity assign \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --system-assigned

# Obtener ID principal de identidad
PRINCIPAL_ID=$(az containerapp identity show \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --query principalId \
  --output tsv)

# Otorgar acceso a Key Vault
az keyvault set-policy \
  --name kv-franquicias-nequi-dev \
  --object-id $PRINCIPAL_ID \
  --secret-permissions get list
```

### Seguridad de Red

```bash
# Restringir acceso de Key Vault a VNet específica (opcional)
az keyvault network-rule add \
  --name kv-franquicias-nequi-dev \
  --subnet /subscriptions/.../subnets/container-apps-subnet

# Habilitar firewall
az keyvault update \
  --name kv-franquicias-nequi-dev \
  --default-action Deny
```

### Lista Blanca de IPs de MongoDB Atlas

Actualizar para usar rangos de IP de Azure:

```hcl
# En Terraform
resource "mongodbatlas_project_ip_access_list" "azure_containers" {
  project_id = mongodbatlas_project.franquicias_project.id
  cidr_block = "40.74.0.0/16"  # Región Azure East US
  comment    = "Azure Container Apps"
}
```

## 📊 Monitoreo y Registro

### Azure Monitor

```bash
# Crear Log Analytics Workspace
az monitor log-analytics workspace create \
  --resource-group rg-franquicias-nequi-dev \
  --workspace-name franquicias-logs

# Habilitar diagnósticos de Container Apps
az containerapp logs show \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --follow
```

### Application Insights

```bash
# Crear Application Insights
az monitor app-insights component create \
  --app franquicias-api-insights \
  --location eastus \
  --resource-group rg-franquicias-nequi-dev \
  --application-type web

# Obtener clave de instrumentación
INSTRUMENTATION_KEY=$(az monitor app-insights component show \
  --app franquicias-api-insights \
  --resource-group rg-franquicias-nequi-dev \
  --query instrumentationKey \
  --output tsv)

# Agregar al entorno del contenedor
az containerapp update \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --set-env-vars APPLICATIONINSIGHTS_CONNECTION_STRING="InstrumentationKey=${INSTRUMENTATION_KEY}"
```

### Métricas a Monitorear

- **Tasa de Solicitudes**: Solicitudes por segundo
- **Tiempo de Respuesta**: P50, P95, P99
- **Tasa de Errores**: Errores 4xx y 5xx
- **CPU/Memoria**: Utilización de recursos
- **Conteo de Réplicas**: Comportamiento de auto-escalado

## 💰 Estimación de Costos

### Tier Gratuito / Costo Mínimo

| Servicio | Costo |
|---------|------|
| MongoDB Atlas M0 | **GRATIS** |
| Azure Container Apps | ~$15-25/mes |
| Azure Container Registry (Básico) | ~$5/mes |
| Azure Key Vault | ~$0.03/mes |
| Log Analytics (5GB) | **GRATIS** |
| **Total** | **~$20-30/mes** |

### Escala de Producción

| Servicio | Costo |
|---------|------|
| MongoDB Atlas M10 | ~$57/mes |
| Azure Container Apps (escalado) | ~$100-200/mes |
| Azure Container Registry (Estándar) | ~$20/mes |
| Application Insights | ~$10-30/mes |
| Azure Front Door | ~$35/mes |
| **Total** | **~$222-342/mes** |

## 🔄 Pipeline CI/CD

### GitHub Actions

```yaml
name: Desplegar en Azure

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Iniciar sesión en Azure
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
      
      - name: Iniciar sesión en ACR
        run: |
          az acr login --name acrfranquiciasnequidev
      
      - name: Compilar y Subir
        run: |
          docker build -t acrfranquiciasnequidev.azurecr.io/franquicias-api:${{ github.sha }} .
          docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:${{ github.sha }}
      
      - name: Desplegar en Container Apps
        run: |
          az containerapp update \
            --name franquicias-nequi-api \
            --resource-group rg-franquicias-nequi-dev \
            --image acrfranquiciasnequidev.azurecr.io/franquicias-api:${{ github.sha }}
```

### Azure DevOps Pipeline

```yaml
trigger:
  - main

pool:
  vmImage: 'ubuntu-latest'

variables:
  acrName: 'acrfranquiciasnequidev'
  imageName: 'franquicias-api'
  resourceGroup: 'rg-franquicias-nequi-dev'
  containerApp: 'franquicias-nequi-api'

steps:
  - task: Docker@2
    inputs:
      containerRegistry: '$(acrName)'
      repository: '$(imageName)'
      command: 'buildAndPush'
      Dockerfile: 'deployment/Dockerfile'
      tags: |
        $(Build.BuildId)
        latest

  - task: AzureCLI@2
    inputs:
      azureSubscription: 'AzureServiceConnection'
      scriptType: 'bash'
      scriptLocation: 'inlineScript'
      inlineScript: |
        az containerapp update \
          --name $(containerApp) \
          --resource-group $(resourceGroup) \
          --image $(acrName).azurecr.io/$(imageName):$(Build.BuildId)
```

## 🧪 Funciones Avanzadas

### Reglas de Auto-Escalado

```bash
# Escalado basado en CPU
az containerapp update \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --min-replicas 2 \
  --max-replicas 10 \
  --scale-rule-name cpu-scale \
  --scale-rule-type cpu \
  --scale-rule-metadata "type=Utilization" "value=70"

# Escalado basado en solicitudes HTTP
az containerapp update \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --scale-rule-name http-scale \
  --scale-rule-type http \
  --scale-rule-http-concurrency 100
```

### Despliegue Blue-Green

```bash
# Desplegar nueva versión con 0% de tráfico
az containerapp revision copy \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --revision-suffix v2 \
  --image acrfranquiciasnequidev.azurecr.io/franquicias-api:v2

# Cambiar tráfico gradualmente
az containerapp ingress traffic set \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --revision-weight v1=80 v2=20

# Cambio completo
az containerapp ingress traffic set \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --revision-weight v2=100
```

## 🗑️ Limpieza

```bash
# Opción 1: Eliminar recursos individuales
az containerapp delete \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --yes

# Opción 2: Eliminar grupo de recursos completo
az group delete \
  --name rg-franquicias-nequi-dev \
  --yes

# Opción 3: Destruir con Terraform
cd terraform
terraform destroy -var-file="terraform-azure.tfvars"
```

## 📝 Mejores Prácticas

1. ✅ Usar **Identidades Administradas** en lugar de contraseñas
2. ✅ Almacenar secretos en **Azure Key Vault**
3. ✅ Habilitar **Application Insights** para monitoreo
4. ✅ Usar **Azure Front Door** para distribución global
5. ✅ Implementar **health checks** en todos los contenedores
6. ✅ Configurar **auto-escalado** basado en métricas
7. ✅ Usar **Azure Policy** para gobernanza
8. ✅ Habilitar **logs de diagnóstico** para todos los recursos

## 🆘 Solución de Problemas

### El contenedor no inicia

```bash
# Verificar logs
az containerapp logs show \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --tail 100

# Verificar revisiones
az containerapp revision list \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --output table
```

### No se puede descargar imagen de ACR

```bash
# Verificar acceso a ACR
az acr login --name acrfranquiciasnequidev

# Verificar que la imagen existe
az acr repository show-tags \
  --name acrfranquiciasnequidev \
  --repository franquicias-api
```

### Problemas de conexión con MongoDB

```bash
# Verificar secreto
az keyvault secret show \
  --vault-name kv-franquicias-nequi-dev \
  --name mongodb-connection-string

# Verificar que la lista blanca de MongoDB Atlas incluye IPs de Azure
```

---

## 🎉 ¡Éxito!

Tu **FranquiciasAPI** ahora está ejecutándose en **Microsoft Azure** con:

- ✅ Contenedores serverless con auto-escalado
- ✅ MongoDB Atlas en la nube
- ✅ Gestión segura de credenciales
- ✅ Monitoreo de nivel de producción
- ✅ Listo para CI/CD
- ✅ Costos optimizados (~$20-30/mes)

**Endpoint de la API**: ¡Verifica la salida del despliegue para obtener tu URL!

---

**¿Necesitas ayuda?** Consulta la documentación de Azure o ejecuta `az --help`

