| Endpoint agregar sucursal | ✅ | `POST /api/franchises/{id}/branches` |
| Endpoint agregar producto | ✅ | `POST /.../products` |
| Endpoint eliminar producto | ✅ | `DELETE /.../products/{id}` |
| Endpoint modificar stock | ✅ | `PATCH /.../stock` |
| Endpoint mayor stock | ✅ | `GET /.../highest-stock-products` |
| Persistencia en la nube | ✅ | MongoDB Atlas (Azure region) |
| **Puntos Extra** | | |
| Docker | ✅ | Docker + Docker Compose completo |
| Programación Reactiva | ✅ | 100% WebFlux + Project Reactor |
| Actualizar nombre franquicia | ✅ | `PATCH /.../name` |
| Actualizar nombre sucursal | ✅ | `PATCH /.../name` |
| Actualizar nombre producto | ✅ | `PATCH /.../name` |
| Infraestructura como Código | ✅ | Terraform (MongoDB Atlas + Azure) |
| Despliegue en la nube | ✅ | Azure Container Apps + MongoDB Atlas |

### 🎯 Puntaje Total: **14/14 (100%)**
# FranquiciasAPI - Sistema de Gestión de Franquicias

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![WebFlux](https://img.shields.io/badge/WebFlux-Reactive-blue.svg)](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

API REST reactiva para la gestión integral de franquicias, sucursales y productos, desarrollada con **Spring Boot WebFlux** siguiendo los principios de **Clean Architecture** utilizando el **Scaffold de Bancolombia**.

---

## 🎯 Sobre este Proyecto

Este proyecto fue desarrollado como parte de mi proceso de aplicación a **Nequi**, una de las empresas fintech más innovadoras de Colombia. Nequi representa la transformación digital del sector financiero en Latinoamérica, democratizando el acceso a servicios bancarios a través de tecnología de punta y una experiencia de usuario excepcional.

### ¿Por qué quiero trabajar en Nequi?

Como desarrollador backend apasionado por la arquitectura de software y las tecnologías reactivas, Nequi representa el lugar ideal para crecer profesionalmente por varias razones:

1. **Impacto Social**: Nequi está cambiando la vida de millones de colombianos, facilitando la inclusión financiera y democratizando el acceso a servicios bancarios. Ser parte de este impacto social es profundamente motivador.

2. **Excelencia Técnica**: Nequi es reconocido por su stack tecnológico de vanguardia y sus prácticas de ingeniería de software de clase mundial. Trabajar en un ambiente donde se valora la calidad del código, la arquitectura limpia y las mejores prácticas es fundamental para mi desarrollo profesional.

3. **Cultura de Innovación**: La capacidad de Nequi para innovar constantemente, experimentar con nuevas tecnologías y mantenerse a la vanguardia en el sector fintech es inspiradora. Quiero ser parte de un equipo que no teme desafiar el status quo.

4. **Escalabilidad y Complejidad**: Los desafíos técnicos que Nequi enfrenta diariamente (millones de transacciones, alta disponibilidad, escalabilidad masiva) son exactamente el tipo de problemas complejos que me apasionan resolver.

5. **Aprendizaje Continuo**: El equipo de Nequi, conformado por profesionales de alto nivel, representa una oportunidad invaluable para aprender, crecer y evolucionar como ingeniero de software.

Este proyecto demuestra mi compromiso con la excelencia técnica, mi capacidad para implementar arquitecturas limpias y escalables, y mi dominio de tecnologías reactivas modernas, competencias que estoy ansioso por aplicar en Nequi.

---

## 🏗️ Arquitectura

Este proyecto implementa **Clean Architecture** (Arquitectura Limpia) propuesta por Robert C. Martin, utilizando el **Scaffold generado por Bancolombia**, garantizando:

- ✅ Separación clara de responsabilidades
- ✅ Independencia de frameworks
- ✅ Testabilidad
- ✅ Independencia de la UI y la base de datos
- ✅ Regla de dependencia (las dependencias apuntan hacia adentro)

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Capas de la Aplicación

```
┌─────────────────────────────────────────────────────────┐
│                    Applications                         │
│              (MainApplication + Config)                 │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────┐
│                  Infrastructure                         │
│  ┌──────────────────────┐  ┌──────────────────────┐    │
│  │   Entry Points       │  │   Driven Adapters    │    │
│  │   - reactive-web     │  │   - mongo-repository │    │
│  │   (API REST)         │  │   (Persistencia)     │    │
│  └──────────────────────┘  └──────────────────────┘    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────┐
│                      Domain                             │
│  ┌──────────────────────┐  ┌──────────────────────┐    │
│  │      UseCases        │  │       Model          │    │
│  │  (Lógica Negocio)    │  │  (Entidades + Ports) │    │
│  └──────────────────────┘  └──────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### Descripción de Capas

#### **Domain (Núcleo del Negocio)**

**Model**: Contiene las entidades de dominio y los puertos (interfaces)
- `Franchise`, `Branch`, `Product` (Entidades)
- `FranchiseRepository`, `BranchRepository`, `ProductRepository` (Puertos)

**UseCase**: Implementa la lógica de negocio pura
- 9 casos de uso implementados con programación reactiva (Mono/Flux)
- Orquesta el flujo de datos entre entry points y repositories

#### **Infrastructure (Detalles de Implementación)**

**Entry Points**: Puntos de entrada a la aplicación
- `reactive-web`: API REST con Spring WebFlux (Handlers + Routers)
- DTOs para entrada/salida de datos

**Driven Adapters**: Implementaciones de servicios externos
- `mongo-repository`: Implementación reactiva con Spring Data MongoDB
- Mapeo entre entidades de dominio y modelos de datos

#### **Applications**

Módulo de configuración y arranque
- `MainApplication`: Punto de entrada de la aplicación
- `UseCasesConfig`: Auto-configuración de beans de casos de uso

---

## 🚀 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 21 LTS | Lenguaje de programación |
| **Spring Boot** | 3.x | Framework base |
| **Spring WebFlux** | 3.x | Programación reactiva no bloqueante |
| **Project Reactor** | - | Librería reactiva (Mono/Flux) |
- **Java 21** instalado (para compilar el proyecto)
| **Spring Data MongoDB Reactive** | 3.x | Persistencia reactiva en MongoDB |
| **MongoDB** | 7.0 | Base de datos NoSQL |
| **Lombok** | - | Reducción de boilerplate |
| **Gradle** | 8.x | Build tool |
### Ejecutar en Dos Simples Pasos
| **Bancolombia Scaffold** | - | Generación de estructura Clean Architecture |
**Paso 1: Compilar el proyecto**
```bash
# En Windows
.\gradle clean build -x test

# En Linux/Mac
./gradle clean build -x test
```

**Paso 2: Iniciar con Docker**

---

## 📋 Funcionalidades Implementadas
Docker Compose se encargará de:
1. ✅ Construir la imagen de la aplicación con el JAR compilado
2. ✅ Iniciar MongoDB con persistencia de datos
3. ✅ Iniciar Mongo Express (interfaz web para MongoDB)
4. ✅ Iniciar la API de Franquicias con health checks
| `POST` | `/api/franchises/{id}/branches` | Agregar sucursal a franquicia |
| `POST` | `/api/franchises/{id}/branches/{id}/products` | Agregar producto a sucursal |
| `DELETE` | `/api/franchises/{id}/branches/{id}/products/{id}` | Eliminar producto |
| `PATCH` | `/api/franchises/{id}/branches/{id}/products/{id}/stock` | Actualizar stock |
| `GET` | `/api/franchises/{id}/highest-stock-products` | Obtener productos con mayor stock por sucursal |
| `PATCH` | `/api/franchises/{id}/name` | Actualizar nombre de franquicia |
| `PATCH` | `/api/franchises/{id}/branches/{id}/name` | Actualizar nombre de sucursal |
| `PATCH` | `/api/franchises/{id}/branches/{id}/products/{id}/name` | Actualizar nombre de producto |

### Casos de Uso Implementados

1. ✅ **CreateFranchiseUseCase**: Crear nuevas franquicias
2. ✅ **AddBranchToFranchiseUseCase**: Agregar sucursales
3. ✅ **AddProductToBranchUseCase**: Agregar productos
4. ✅ **RemoveProductFromBranchUseCase**: Eliminar productos
5. ✅ **UpdateProductStockUseCase**: Gestionar inventario
6. ✅ **GetHighestStockProductsUseCase**: Consulta de productos con mayor stock
7. ✅ **UpdateFranchiseNameUseCase**: Actualizar franquicias
8. ✅ **UpdateBranchNameUseCase**: Actualizar sucursales
9. ✅ **UpdateProductNameUseCase**: Actualizar productos

---

## 🐳 Inicio Rápido con Docker

### Prerrequisitos

- **Docker Desktop** instalado y ejecutándose
- **Puerto 8080** disponible (API)
- **Puerto 27017** disponible (MongoDB)
- **Puerto 8081** disponible (Mongo Express UI)

### Ejecutar Todo con Un Solo Comando

```bash
docker-compose up -d
```

Eso es todo. Docker Compose se encargará de:
1. ✅ Descargar las imágenes necesarias (primera vez)
2. ✅ Construir la imagen de la aplicación
3. ✅ Iniciar MongoDB con persistencia de datos
4. ✅ Iniciar Mongo Express (interfaz web para MongoDB)
5. ✅ Iniciar la API de Franquicias con health checks

### Verificar que Todo Funciona

```bash
# Ver estado de los contenedores
docker-compose ps

# Ver logs de la API
docker-compose logs -f franquicias-api

# Verificar health de la aplicación
curl http://localhost:8080/actuator/health
```

### Acceder a los Servicios

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **API REST** | http://localhost:8080 | - |
| **Health Check** | http://localhost:8080/actuator/health | - |
| **Prometheus Metrics** | http://localhost:8080/actuator/prometheus | - |
| **Mongo Express (UI)** | http://localhost:8081 | `admin` / `admin123` |

### Detener los Servicios

```bash
# Detener contenedores (mantiene los datos)
docker-compose down

# Detener y eliminar todo (incluyendo volúmenes de datos)
docker-compose down -v
```

---

## 💻 Desarrollo Local (Sin Docker)

Si prefieres ejecutar la aplicación localmente:

### Prerrequisitos

- **Java 21** instalado
- **MongoDB** ejecutándose en `localhost:27017`
- **Gradle** (incluido en el proyecto)

### Iniciar MongoDB Localmente

```bash
# Con Docker (solo MongoDB)
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# O instalar MongoDB localmente desde mongodb.com
```

### Ejecutar la Aplicación

```bash
# Con Gradle
./gradlew bootRun

# O desde tu IDE favorito
# Ejecutar la clase: co.com.nequi.franquicias.MainApplication
```

---

## 🧪 Ejemplos de Uso

### Crear una Franquicia

```bash
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Nequi"}'
```

**Respuesta:**
```json
{
  "id": "abc123...",
  "name": "Nequi",
  "branches": []
}
```

### Agregar una Sucursal

```bash
curl -X POST http://localhost:8080/api/franchises/abc123/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Bogotá Centro"}'
```

### Agregar un Producto

```bash
curl -X POST http://localhost:8080/api/franchises/abc123/branches/def456/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Smartphone", "stock": 50}'
```

### Actualizar Stock

```bash
curl -X PATCH http://localhost:8080/api/franchises/abc123/branches/def456/products/xyz789/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 75}'
```

### Consultar Productos con Mayor Stock

```bash
curl http://localhost:8080/api/franchises/abc123/highest-stock-products
```

**Respuesta:**
```json
[
  {
    "productId": "xyz789",
    "productName": "Smartphone",
    "stock": 75,
    "branchId": "def456",
    "branchName": "Sucursal Bogotá Centro"
  }
]
```

### Usar con Postman

#### 1. Crear Franquicia
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/franchises`
- **Headers**: `Content-Type: application/json`
- **Body (raw - JSON)**:
```json
{
  "name": "Nequi"
}
```
- **Respuesta**:
```json
{
  "id": "673c5a8f9e1d2a3b4c5d6e7f",
  "name": "Nequi",
  "branches": []
}
```

#### 2. Agregar Sucursal
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/franchises/673c5a8f9e1d2a3b4c5d6e7f/branches`
- **Headers**: `Content-Type: application/json`
- **Body (raw - JSON)**:
```json
{
  "name": "Sucursal Bogotá Centro"
}
```
- **Respuesta**:
```json
{
  "id": "673c5a8f9e1d2a3b4c5d6e7f",
  "name": "Nequi",
  "branches": [
    {
      "id": "673c5b1a2f3e4d5c6a7b8c9d",
      "name": "Sucursal Bogotá Centro",
      "products": []
    }
  ]
}
```

#### 3. Agregar Producto
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/franchises/673c5a8f9e1d2a3b4c5d6e7f/branches/673c5b1a2f3e4d5c6a7b8c9d/products`
- **Headers**: `Content-Type: application/json`
- **Body (raw - JSON)**:
```json
{
  "name": "Smartphone Samsung",
  "stock": 50
}
```
- **Respuesta**:
```json
{
  "id": "673c5a8f9e1d2a3b4c5d6e7f",
  "name": "Nequi",
  "branches": [
    {
      "id": "673c5b1a2f3e4d5c6a7b8c9d",
      "name": "Sucursal Bogotá Centro",
      "products": [
        {
          "id": "673c5c2d3e4f5a6b7c8d9e0f",
          "name": "Smartphone Samsung",
          "stock": 50
        }
      ]
    }
  ]
}
```

#### 4. Actualizar Stock
- **Método**: `PATCH`
- **URL**: `http://localhost:8080/api/franchises/673c5a8f9e1d2a3b4c5d6e7f/branches/673c5b1a2f3e4d5c6a7b8c9d/products/673c5c2d3e4f5a6b7c8d9e0f/stock`
- **Headers**: `Content-Type: application/json`
- **Body (raw - JSON)**:
```json
{
  "stock": 75
}
```

#### 5. Eliminar Producto
- **Método**: `DELETE`
- **URL**: `http://localhost:8080/api/franchises/673c5a8f9e1d2a3b4c5d6e7f/branches/673c5b1a2f3e4d5c6a7b8c9d/products/673c5c2d3e4f5a6b7c8d9e0f`
- **Headers**: No requiere

#### 6. Obtener Productos con Mayor Stock
- **Método**: `GET`
- **URL**: `http://localhost:8080/api/franchises/673c5a8f9e1d2a3b4c5d6e7f/highest-stock-products`
- **Headers**: No requiere
- **Respuesta**:
```json
[
  {
    "productId": "673c5c2d3e4f5a6b7c8d9e0f",
    "productName": "Smartphone Samsung",
    "stock": 75,
    "branchId": "673c5b1a2f3e4d5c6a7b8c9d",
    "branchName": "Sucursal Bogotá Centro"
  },
  {
    "productId": "673c5d3e4f5a6b7c8d9e0f1a",
    "productName": "Laptop HP",
    "stock": 120,
    "branchId": "673c5e4f5a6b7c8d9e0f1a2b",
    "branchName": "Sucursal Medellín"
  }
]
```

> **Tip**: Copia y guarda los IDs que recibes en las respuestas para usarlos en las siguientes peticiones

---

## 🛠️ Comandos Útiles

### Docker

```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Reiniciar API (tras cambios de código)
docker-compose restart franquicias-api

# Reconstruir imagen
docker-compose build --no-cache franquicias-api
docker-compose up -d franquicias-api

# Ver estado
docker-compose ps

# Detener todo
docker-compose down
```

### Gradle

```bash
# Compilar
./gradlew clean build

# Ejecutar tests
./gradlew test

# Ejecutar aplicación
./gradlew bootRun

# Ver dependencias
./gradlew dependencies
```

---

## 📐 Estructura del Proyecto

```
API - Franquicias/
├── applications/
│   └── app-service/              # Configuración y arranque
│       ├── src/main/
│       │   ├── java/
│       │   │   └── co/com/nequi/franquicias/
│       │   │       ├── MainApplication.java
│       │   │       └── config/
│       │   │           └── UseCasesConfig.java
│       │   └── resources/
│       │       ├── application.yaml
│       │       └── application-docker.yaml
│       └── build.gradle
│
├── domain/
│   ├── model/                     # Entidades y Puertos
│   │   └── src/main/java/
│   │       └── co/com/nequi/franquicias/model/
│   │           ├── franchise/
│   │           │   ├── Franchise.java
│   │           │   └── gateways/FranchiseRepository.java
│   │           ├── branch/
│   │           │   ├── Branch.java
│   │           │   └── gateways/BranchRepository.java
│   │           └── product/
│   │               ├── Product.java
│   │               └── gateways/ProductRepository.java
│   │
│   └── usecase/                   # Lógica de Negocio
│       └── src/main/java/
│           └── co/com/nequi/franquicias/usecase/
│               ├── createfranchise/
│               ├── addbranchtofranchise/
│               ├── addproducttobranch/
│               ├── removeproductfrombranch/
│               ├── updateproductstock/
│               ├── gethigheststockproducts/
│               └── ...
│
├── infrastructure/
│   ├── entry-points/
│   │   └── reactive-web/          # API REST
│   │       └── src/main/java/
│   │           └── co/com/nequi/franquicias/api/
│   │               ├── Handler.java
│   │               ├── RouterRest.java
│   │               └── dto/
│   │
│   └── driven-adapters/
│       └── mongo-repository/      # Persistencia MongoDB
│           └── src/main/java/
│               └── co/com/nequi/franquicias/mongo/
│                   ├── MongoRepositoryAdapter.java
│                   ├── MongoDBRepository.java
│                   ├── data/
│                   └── mapper/
│
├── deployment/
│   └── Dockerfile                 # Multi-stage build
│
├── docker-compose.yml             # Orquestación de servicios
├── .dockerignore
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🧬 Scaffold de Bancolombia

Este proyecto fue generado utilizando el **Scaffold de Bancolombia**, una herramienta CLI que automatiza la creación de proyectos siguiendo Clean Architecture.

### ¿Qué es el Scaffold de Bancolombia?

Es un plugin de Gradle desarrollado por Bancolombia que permite generar la estructura base de proyectos con Clean Architecture de manera rápida y estandarizada. Facilita:

- ✅ Generación automática de la estructura de carpetas
- ✅ Configuración de módulos Gradle
- ✅ Creación de casos de uso, entidades y adaptadores
- ✅ Integración con diferentes tecnologías (WebFlux, MongoDB, etc.)
- ✅ Cumplimiento de mejores prácticas

### Comandos Utilizados

```bash
# Generar proyecto base
gradle cleanArchitecture

# Generar modelo de dominio
gradle generateModel --name Franchise
gradle generateModel --name Branch
gradle generateModel --name Product

# Generar casos de uso
gradle generateUseCase --name CreateFranchise
gradle generateUseCase --name AddBranchToFranchise
# ... etc

# Generar adaptadores
gradle generateDrivenAdapter --type reactive-mongodb
gradle generateEntryPoint --type reactive-web
```

### Recursos

- [Documentación Scaffold Bancolombia](https://github.com/bancolombia/scaffold-clean-architecture)
- [Artículo Clean Architecture - Bancolombia](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

---

## ✨ Características Destacadas

### Programación Reactiva

- **100% no bloqueante**: Todas las operaciones usan `Mono` y `Flux` de Project Reactor
- **Alta concurrencia**: Manejo eficiente de múltiples peticiones simultáneas
- **Escalabilidad**: Optimizado para alto rendimiento

### Clean Architecture

- **Independencia de frameworks**: La lógica de negocio no depende de Spring
- **Testeable**: Fácil de testear cada capa de forma aislada
- **Mantenible**: Código organizado y fácil de entender
- **Flexible**: Fácil cambiar implementaciones (ej: cambiar MongoDB por DynamoDB)

### Seguridad y Buenas Prácticas

- ✅ Usuario no-root en contenedor Docker
- ✅ Health checks configurados
- ✅ Actuator para monitoreo
- ✅ CORS configurado
- ✅ Manejo de errores reactivo
- ✅ Validación de datos

---

## 📊 Monitoreo y Observabilidad

### Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas Prometheus
curl http://localhost:8080/actuator/prometheus

# Información de la aplicación
curl http://localhost:8080/actuator/info
```

### Logs

```bash
# Ver logs de la API
docker-compose logs -f franquicias-api

# Ver logs de MongoDB
docker-compose logs -f mongodb
```

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con coverage
./gradlew test jacocoTestReport

# Ver reporte de coverage
open build/reports/jacoco/test/html/index.html
```

---

## 🚀 Despliegue en Producción

### Recomendaciones

1. **Base de Datos**: Usar MongoDB Atlas o servicio gestionado
2. **Variables de Entorno**: Configurar secrets de forma segura
3. **Recursos**: Ajustar límites de CPU y memoria en `docker-compose.yml`
4. **Monitoring**: Integrar con Prometheus + Grafana
5. **Logging**: Usar ELK Stack o servicio cloud
6. **Seguridad**: Implementar autenticación y autorización (JWT, OAuth2)

### Configuración para Producción

Modificar `application.yaml`:

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}  # Desde variable de entorno
```

---

## 🌐 **Probar la API Desplegada en Azure**

La aplicación ya está desplegada y funcionando en Azure. Puedes probarla directamente:

### **URL de la API:**
```
https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io
```

### **Pruebas Rápidas con cURL:**

#### Health Check
```bash
curl https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io/actuator/health
```

#### Crear Franquicia
```bash
curl -X POST https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name":"Nequi Colombia"}'
```

#### Agregar Sucursal
```bash
curl -X POST https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io/api/franchises/{id}/branches \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Medellín"}'
```

### **Pruebas con Postman:**

Usa esta **Base URL** en Postman:
```
https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io
```

Luego sigue los ejemplos de la [sección de Postman](#usar-con-postman) más abajo.

---

## 📚 Documentación Adicional

### Guías de Deployment
- **[🚀 QUICKSTART-AZURE.md](QUICKSTART-AZURE.md)**: Guía rápida de despliegue en Azure (30-40 min)
- **[📖 AZURE-DEPLOYMENT.md](AZURE-DEPLOYMENT.md)**: Guía completa y detallada para Azure
- **[🔧 Terraform Guide](terraform/README.md)**: Infraestructura como Código con Terraform

### Recursos Técnicos
- **Arquitectura Clean**: [Medium - Bancolombia](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)
- **Spring WebFlux**: [Documentación Oficial](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- **Project Reactor**: [Reactor Docs](https://projectreactor.io/docs)
- **MongoDB Reactive**: [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive)
- **Terraform**: [Terraform Docs](https://www.terraform.io/docs)
- **Azure Container Apps**: [Azure Docs](https://docs.microsoft.com/azure/container-apps/)

---

## ⭐ Puntos Extra Implementados

Este proyecto incluye **TODOS** los puntos extra solicitados:

### ✅ 1. Empaquetado con Docker
- **Docker Compose** completo para desarrollo local
- **Multi-stage Dockerfile** optimizado para producción
- **Docker Compose AWS** para despliegue en la nube
- Health checks y monitoreo integrado

### ✅ 2. Programación Funcional y Reactiva
- **100% Reactivo** con Spring WebFlux
- Uso de **Mono** y **Flux** de Project Reactor
- Operaciones no bloqueantes en todas las capas
- Manejo funcional de errores con operadores reactivos

### ✅ 3. Actualizar Nombre de Franquicia
- **Endpoint**: `PATCH /api/franchises/{franchiseId}/name`
- Actualización reactiva del nombre
- Validación y manejo de errores

### ✅ 4. Actualizar Nombre de Sucursal
- **Endpoint**: `PATCH /api/franchises/{franchiseId}/branches/{branchId}/name`
- Búsqueda y actualización eficiente
- Persistencia inmediata

### ✅ 5. Actualizar Nombre de Producto
- **Endpoint**: `PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name`
- Navegación profunda en la estructura
- Operaciones atómicas

### ✅ 6. Infraestructura como Código (IaC)
- **Terraform** completo para MongoDB Atlas + Azure
- Provisión automática de:
  - MongoDB Atlas Cluster (M0 Free Tier) en región de Azure
  - Azure Key Vault para credenciales
  - Azure Container Registry para imágenes Docker
  - Database users y permisos
  - IP Whitelisting
- Código reutilizable y versionado

### ✅ 7. Despliegue en la Nube
- **Azure Container Apps** para contenedores serverless
- **Azure Container Registry (ACR)** para registro de imágenes Docker
- **Auto-scaling** (1-5 réplicas según demanda)
- **HTTPS automático** con certificados gestionados
- **Azure Monitor + Log Analytics** para logs y métricas
- **MongoDB Atlas** en la nube (región Azure)
- Scripts de despliegue automatizado (3 opciones)
- Guías completas: rápida y detallada

### 📁 Estructura de Archivos para Despliegue

├── terraform/                          # Infraestructura como Código
│   ├── main.tf                        # Terraform para Azure + MongoDB Atlas
│   ├── terraform-azure.tfvars.example # Ejemplo de variables
│   ├── .gitignore                     # Ignorar secrets
│   └── README.md                      # Guía de Terraform
│   ├── .gitignore              # Ignorar secrets
│   └── README.md               # Guía de Terraform
│   ├── Dockerfile                     # Multi-stage build optimizado
│   ├── deploy-azure.sh               # Build & push a Azure ACR
│   ├── deploy-azure-aci.sh           # Azure Container Instances
│   └── deploy-azure-container-apps.sh # Azure Container Apps (Recomendado)
│   ├── deploy-aws.sh          # Script de despliegue AWS
├── AZURE-DEPLOYMENT.md                # Guía completa de despliegue en Azure
├── QUICKSTART-AZURE.md               # Guía rápida (30-40 min)
└── docker-compose.yml                # Para desarrollo local
### 🚀 Despliegue Completo en Azure (Lo que Hicimos)

El proyecto está completamente desplegado en **Microsoft Azure** con la siguiente infraestructura:
└── docker-compose.aws.yml     # Para producción AWS
**Recursos Creados:**
- ✅ **MongoDB Atlas M0** (GRATIS) en región Azure
- ✅ **Azure Container Registry**: `acrfranquiciasnequidev.azurecr.io`
- ✅ **Azure Key Vault**: Almacena credenciales de MongoDB
- ✅ **Azure Container Apps**: App corriendo con auto-scaling (1-5 réplicas)
- ✅ **HTTPS**: Automático con dominio `*.azurecontainerapps.io`
- ✅ **Costo**: ~$20-30/mes

**Comando de Despliegue (Resumen):**
```
# 1. Provisionar infraestructura con Terraform
### 🚀 Despliegue Rápido en AWS
terraform apply -var-file="terraform-azure.tfvars"
# 1. Provisionar MongoDB Atlas con Terraform
# 2. Compilar y subir a Azure Container Registry
gradle clean build -x test
az acr login --name acrfranquiciasnequidev
docker build -t acrfranquiciasnequidev.azurecr.io/franquicias-api:latest .
docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:latest

# 3. Desplegar en Azure Container Apps
az containerapp env create --name franquicias-nequi-env --resource-group rg-franquicias-nequi-dev --location eastus
az containerapp create --name franquicias-nequi-api ...

# 4. Verificar deployment
curl https://franquicias-nequi-api.blueplant-b4ada0ac.eastus.azurecontainerapps.io/actuator/health
./deploy-aws.sh

# 3. Verificar deployment
curl http://<ALB-DNS>/actuator/health
```

### 🚀 Despliegue Rápido en Azure

**¿Primera vez desplegando?** Usa la guía rápida:

📖 **[QUICKSTART-AZURE.md](QUICKSTART-AZURE.md)** - Despliegue paso a paso en 30-40 minutos

**¿Quieres todos los detalles?** Usa la guía completa:

📖 **[AZURE-DEPLOYMENT.md](AZURE-DEPLOYMENT.md)** - Guía completa con explicaciones

#### Comandos Rápidos (Resumen)

```bash
# 1. Configurar Terraform
cd terraform
terraform apply -var-file="terraform-azure.tfvars"

# 2. Compilar y desplegar
cd ..
gradle clean build -x test
az acr login --name acrfranquiciasnequidev
docker build -t acrfranquiciasnequidev.azurecr.io/franquicias-api:latest .
docker push acrfranquiciasnequidev.azurecr.io/franquicias-api:latest

# 3. Crear Container App
az containerapp env create --name franquicias-nequi-env --resource-group rg-franquicias-nequi-dev --location eastus
az containerapp create --name franquicias-nequi-api ... # Ver guía completa
```

Ver **[Guía Completa de Azure](AZURE-DEPLOYMENT.md)** para instrucciones detalladas.

---

## 🏆 Resumen de Cumplimiento

| Requisito | Estado | Detalles |
|-----------|--------|----------|
| **Criterios de Aceptación** | | |
| Spring Boot | ✅ | Spring Boot 3.x + Java 21 |
| Endpoint agregar franquicia | ✅ | `POST /api/franchises` |

---

## 👨‍💻 Autor

**Desarrollador Backend** apasionado por arquitecturas limpias, programación reactiva y excelencia técnica.

Proyecto desarrollado como parte del proceso de aplicación a **Nequi** 🚀

---

## 📄 Licencia

Este proyecto fue desarrollado para propósitos de evaluación técnica.

---

## 🙏 Agradecimientos

- **Nequi** por la oportunidad de demostrar mis habilidades técnicas
- **Bancolombia** por el excelente Scaffold de Clean Architecture
- La comunidad de **Spring** y **Reactor** por crear tecnologías increíbles

---

**Desarrollado con ❤️ y pasión por la ingeniería de software de calidad**

