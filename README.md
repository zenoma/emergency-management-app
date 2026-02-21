# Firefighter Coordination App

Aplicacion web para la coordinacion de emergencias de incendios forestales. Permite gestionar incendios, cuadrantes, equipos, vehiculos, organizaciones y avisos en tiempo real con soporte geoespacial completo.

## Tecnologias

### Frontend

| Tecnologia | Version | Descripcion |
|---|---|---|
| **React** | 18 | Libreria principal de UI |
| **Vite** | 6.4 | Servidor de desarrollo y bundler |
| **Redux Toolkit** | 1.8 | Gestion de estado global y RTK Query para data fetching |
| **React Router** | 6.30 | Enrutamiento del lado del cliente |
| **Material UI (MUI)** | 5.9 | Libreria de componentes UI |
| **MUI X Data Grid** | 5.17 | Tablas de datos avanzadas |
| **MUI X Date Pickers** | 6.2 | Selectores de fecha/hora |
| **MapLibre GL** | 5.5 | Renderizado de mapas interactivos |
| **react-map-gl** | 8.0 | Wrapper React para MapLibre |
| **proj4** | 2.8 | Transformacion de coordenadas (EPSG:25829 <-> EPSG:4326) |
| **i18next** | 22.4 | Internacionalizacion (ES, EN, GL) |
| **react-i18next** | 12.1 | Integracion de i18next con React |
| **React Toastify** | 9.0 | Notificaciones toast |
| **Day.js** | 1.11 | Manipulacion de fechas |
| **JavaScript (JSX)** | ES6+ | Lenguaje principal |

**APIs externas integradas:**
- **OpenWeatherMap** - Datos meteorologicos en tiempo real
- **MapTiler** - Tiles de mapas topograficos

### Backend

| Tecnologia | Version | Descripcion |
|---|---|---|
| **Java** | 21 | Lenguaje principal |
| **Spring Boot** | 3.4 | Framework de aplicacion |
| **Spring Web** | (managed) | API REST con Tomcat embebido |
| **Spring Data JPA** | (managed) | Acceso a datos con JPA |
| **Spring Security** | (managed) | Autenticacion y autorizacion |
| **Spring Validation** | (managed) | Validacion de beans (Jakarta) |
| **Hibernate** | 6.6 | Implementacion JPA/ORM |
| **Hibernate Spatial** | 6.6 | Soporte de tipos geoespaciales |
| **PostgreSQL** | - | Base de datos relacional |
| **PostGIS** | 3.1.4+ | Extension geoespacial para PostgreSQL |
| **JTS (Java Topology Suite)** | 1.19 | Operaciones geometricas |
| **JJWT** | 0.12.5 | Generacion y validacion de tokens JWT |
| **Lombok** | 1.18 | Reduccion de codigo boilerplate |
| **OpenAPI 3.0** | - | Especificacion contract-first de la API |
| **openapi-generator** | 7.9 | Generacion de codigo desde OpenAPI |
| **springdoc-openapi (Swagger UI)** | 2.8 | Documentacion interactiva de la API |
| **Jackson** | - | Serializacion JSON (incluye soporte JTS y nullable) |
| **Apache Maven** | 3.6+ | Build y gestion de dependencias |

### DevOps / Infraestructura

| Tecnologia | Descripcion |
|---|---|
| **Docker** | Contenedores para base de datos (PostGIS) |
| **Docker Compose** | Orquestacion del contenedor PostgreSQL/PostGIS |
| **GitHub Actions** | CI pipeline (build en push/PR a `main`) |
| **GitLab CI/CD** | Pipeline alternativo (verify en MRs, deploy en rama principal) |
| **Eclipse Temurin** | Distribucion JDK 21 |

### Testing

| Tecnologia | Descripcion |
|---|---|
| **Spring Boot Test** | JUnit 5, Mockito, AssertJ, Spring Test |
| **Spring Security Test** | Utilidades de test para contextos de seguridad |
| **Testcontainers** | Contenedores Docker desechables para tests de integracion |
| **Testcontainers PostgreSQL** | Modulo PostgreSQL/PostGIS para tests |

## Requisitos

- Node 18+
- Java 21 (Eclipse Temurin)
- Maven 3.6+
- PostgreSQL 14+
- PostGIS 3.1.4+
- Docker (para la base de datos)

## Ejecucion

### Backend

Dentro de la carpeta `backend/`:

1. Levantar la base de datos con Docker:
```bash
docker compose -f src/main/resources/docker-compose.yaml up -d
```

2. Ejecutar la aplicacion:
```bash
mvn spring-boot:run
```

### Frontend

Dentro de la carpeta `frontend/`:

```bash
npm install    # solo la primera vez
npm run dev    # servidor de desarrollo con Vite
```
