# 🚀 Guía Rápida de Despliegue en Azure

## ⚡ Resumen de 5 Minutos

Esta guía te llevará paso a paso para desplegar FranquiciasAPI en Azure en aproximadamente **30-40 minutos**.

---

## 📋 Pre-requisitos (15 minutos)

### 1. Cuentas Necesarias

#### Azure (5 min)
- Regístrate en: https://azure.microsoft.com/es-es/free/
- **$200 USD gratis** por 30 días
- Requiere tarjeta (solo verificación, no se cobra)

#### MongoDB Atlas (5 min)
- Regístrate en: https://www.mongodb.com/cloud/atlas/register
- **100% GRATIS** (tier M0 permanente)
- No requiere tarjeta

#### Obtener Credenciales MongoDB (5 min)
1. Ve a tu organización: https://cloud.mongodb.com
2. **Organization Settings** → **Access Manager** → **API Keys**
3. **Create API Key**:
   - Description: `Terraform FranquiciasAPI`
   - Permissions: `Organization Owner`
4. **Guarda**:
   - Public Key: `________`
   - Private Key: `________`
5. **Organization Settings** → **Settings**:
   - Organization ID: `________` ⚠️ (NO es el Project ID)

### 2. Herramientas (si no las tienes)

```bash
# Verificar instalaciones
az --version      # Azure CLI >= 2.50
docker --version  # Docker Desktop
terraform --version  # Terraform >= 1.0
```

**¿No las tienes?**
- Azure CLI: https://aka.ms/installazurecliwindows
- Docker: https://www.docker.com/products/docker-desktop/
- Terraform: https://developer.hashicorp.com/terraform/install

---

## 🎯 Despliegue (25 minutos)

### Paso 1: Configurar Terraform (2 min)

```bash
cd terraform
copy terraform-azure.tfvars.example terraform-azure.tfvars
notepad terraform-azure.tfvars
```

**Edita** y reemplaza:
```hcl
mongodb_atlas_public_key  = "TU_PUBLIC_KEY"
mongodb_atlas_private_key = "TU_PRIVATE_KEY"
mongodb_atlas_org_id      = "TU_ORG_ID"
```

### Paso 2: Terraform Apply (8 min)

```bash
az login
terraform init
terraform apply -var-file="terraform-azure.tfvars"
```

Cuando pregunte, escribe: `yes`

⏱️ Espera 5-8 minutos...

### Paso 3: Compilar Aplicación (2 min)

```bash
cd ..
gradle clean build -x test
```

### Paso 4: Docker Push (3 min)

```bash
az acr login --name acrfranquiciasnequidev
docker build -t franquicias-api:v1.0.0 -f deployment/Dockerfile .
docker tag franquicias-api:v1.0.0 acrfranquiciasnequidev.azurecr.io/franquicias-api:latest
docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:latest
```

### Paso 5: Obtener Datos (1 min)

```bash
# MongoDB Connection String
az keyvault secret show --vault-name kv-franquicias-nequi-dev --name mongodb-connection-string --query value -o tsv

# ACR Credentials
az acr credential show --name acrfranquiciasnequidev --resource-group rg-franquicias-nequi-dev
```

**Guarda**:
- MongoDB URI: `mongodb+srv://...`
- ACR Username: `acrfranquiciasnequidev`
- ACR Password: `________`

### Paso 6: Container Apps (7 min)

```bash
# Crear entorno (2 min)
az containerapp env create --name franquicias-nequi-env --resource-group rg-franquicias-nequi-dev --location eastus

# Crear app (5 min) - REEMPLAZA [MONGODB_URI] y [ACR_PASSWORD]
az containerapp create \
  --name franquicias-nequi-api \
  --resource-group rg-franquicias-nequi-dev \
  --environment franquicias-nequi-env \
  --image acrfranquiciasnequidev.azurecr.io/franquicias-api:latest \
  --registry-server acrfranquiciasnequidev.azurecr.io \
  --registry-username acrfranquiciasnequidev \
  --registry-password "[ACR_PASSWORD]" \
  --target-port 8080 \
  --ingress external \
  --cpu 1.0 \
  --memory 2.0Gi \
  --min-replicas 1 \
  --max-replicas 5 \
  --env-vars "SPRING_PROFILES_ACTIVE=prod" "SPRING_DATA_MONGODB_URI=[MONGODB_URI]" "SPRING_DATA_MONGODB_DATABASE=franquicias"
```

### Paso 7: Probar (2 min)

```bash
# La URL aparece en el output anterior
curl https://franquicias-nequi-api.blueplant-xxxxx.eastus.azurecontainerapps.io/actuator/health
```

**Respuesta esperada**: `{"status":"UP"}`

---

## ✅ Checklist de Despliegue

- [ ] Azure CLI instalado y login exitoso
- [ ] MongoDB Atlas: Public Key, Private Key, Org ID
- [ ] `terraform apply` completado (12 recursos creados)
- [ ] `gradle build` exitoso (JAR creado)
- [ ] Imagen Docker subida a ACR
- [ ] Container App creado y corriendo
- [ ] Health check retorna `{"status":"UP"}`

---

## 🎉 ¡Listo!

Tu API está en:
```
https://franquicias-nequi-api.blueplant-xxxxx.eastus.azurecontainerapps.io
```

**Recursos creados:**
- ✅ MongoDB Atlas M0 (GRATIS)
- ✅ Azure Container Apps (auto-scaling)
- ✅ Azure Container Registry
- ✅ Azure Key Vault
- ✅ HTTPS automático
- ✅ Monitoreo integrado

**Costo estimado**: ~$20-30/mes

---

## 🆘 ¿Problemas?

### Error: "ORG_NOT_FOUND"
- ✅ Verifica que estés usando el **Organization ID** (NO el Project ID)
- ✅ Organization Settings → Settings → Organization ID

### Error: "NOT_ORG_GROUP_CREATOR"
- ✅ Tu API Key necesita permisos de `Organization Owner`
- ✅ Edita la API Key y cambia los permisos

### La imagen no se sube
- ✅ Verifica: `az acr login --name acrfranquiciasnequidev`
- ✅ Verifica que Docker Desktop esté corriendo

### Container App no inicia
- ✅ Verifica logs: `az containerapp logs show --name franquicias-nequi-api --resource-group rg-franquicias-nequi-dev --follow`
- ✅ Verifica que la MongoDB URI sea correcta

---

## 📚 Documentación Completa

Ver: **[AZURE-DEPLOYMENT.md](AZURE-DEPLOYMENT.md)** para la guía completa con explicaciones detalladas.

---

## 🗑️ Limpiar Todo (si quieres eliminar)

```bash
# Opción 1: Terraform destroy
cd terraform
terraform destroy -var-file="terraform-azure.tfvars"

# Opción 2: Eliminar Resource Group completo
az group delete --name rg-franquicias-nequi-dev --yes
```

---

**¡Éxito con tu despliegue!** 🚀

