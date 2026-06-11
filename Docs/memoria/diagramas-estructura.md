# Diagramas de estructura del proyecto

## Backend

El backend sigue una arquitectura por capas dentro del modelo MVC de Spring Boot. Los paquetes `model/`, `rest/` y `config/` separan claramente la lógica de negocio, la exposición de la API y la configuración global. Dentro de `model/`, cada entidad del dominio tiene su propio subpaquete (assignment, emergency, resource, etc.), lo que facilita la navegación y el mantenimiento al aislar cada concepto del dominio. Los servicios se agrupan por funcionalidad (assignment, emergency, logs, personal, resources...), manteniendo una responsabilidad única y bien definida. En `rest/`, los controladores se separan de los mappers y de la gestión de errores, siguiendo el principio de separación de preocupaciones. Los recursos `openApi/` y `db/` reflejan el enfoque API-first y la gestión de esquemas de base de datos como parte del código fuente.

```
backend/
├── src/main/java/es/udc/emergencyproject/backend/
│   ├── model/
│   │   ├── assignment/
│   │   ├── emergency/
│   │   ├── image/
│   │   ├── logs/
│   │   ├── mobiledevice/
│   │   ├── notice/
│   │   ├── organization/
│   │   ├── quadrant/
│   │   ├── resource/
│   │   │   ├── team/
│   │   │   └── vehicle/
│   │   ├── user/
│   │   ├── exceptions/
│   │   └── services/
│   │       ├── assignment/
│   │       ├── emergency/
│   │       ├── logs/
│   │       ├── notice/
│   │       ├── notifications/
│   │       ├── personal/
│   │       ├── resources/
│   │       └── utils/
│   ├── rest/
│   │   ├── common/
│   │   ├── config/
│   │   ├── controllers/
│   │   ├── mappers/
│   │   └── exceptions/
│   ├── config/
│   └── util/
├── src/main/resources/
│   ├── openApi/
│   │   ├── assignments/
│   │   ├── emergencies/
│   │   ├── teams/
│   │   ├── vehicles/
│   │   ├── users/
│   │   ├── quadrants/
│   │   ├── organizations/
│   │   ├── organizationTypes/
│   │   ├── notices/
│   │   ├── recommendationRules/
│   │   ├── logManagement/
│   │   └── common/
│   ├── db/
│   ├── application.yml
│   ├── docker-compose.yaml
│   └── messages_*.properties
└── pom.xml
```

## Frontend

El frontend se organiza por funcionalidad siguiendo el patrón de ducks modular. La carpeta `api/` centraliza todo el acceso a datos mediante RTK Query, con un archivo por cada dominio del backend (emergencyApi, teamApi, userApi...), lo que mantiene la correspondencia directa con los endpoints del servidor. La carpeta `features/` contiene un subdirectorio por cada funcionalidad de la aplicación (assignment, emergency, map, user, vehicle...), encapsulando sus propios componentes, estilos y lógica; esto permite que cada módulo sea independiente y fácil de localizar, modificar o eliminar sin afectar al resto. Las carpetas `app/`, `locales/`, `theme/` y `errors/` agrupan preocupaciones transversales (estado global, internacionalización, personalización visual y gestión de errores), separándolas de la lógica de cada funcionalidad.

```
frontend/src/
├── api/
│   ├── assignmentApi.js
│   ├── baseApi.js
│   ├── emergencyApi.js
│   ├── logApi.js
│   ├── noticeApi.js
│   ├── organizationApi.js
│   ├── quadrantApi.js
│   ├── recommendationRuleApi.js
│   ├── teamApi.js
│   ├── userApi.js
│   ├── vehicleApi.js
│   └── weatherApi.js
├── app/
│   ├── assets/
│   ├── utils/
│   ├── rtkQueryErrorHandler.js
│   └── store.js
├── features/
│   ├── assignment/
│   ├── dashboard/
│   ├── drawer/
│   ├── emergency/
│   ├── history/
│   ├── map/
│   ├── notice/
│   ├── organization/
│   ├── point/
│   ├── quadrant/
│   ├── team/
│   ├── theme/
│   ├── user/
│   │   ├── login/
│   │   ├── signUp/
│   │   ├── profile/
│   │   └── management/
│   ├── utils/
│   ├── vehicle/
│   └── weather/
├── locales/
│   ├── en/
│   ├── es/
│   └── gl/
├── components/
├── theme/
├── errors/
├── assets/
├── App.jsx
├── i18n.js
└── index.jsx
```

## Android

La aplicación Android sigue una organización por capas y funcionalidades. El paquete `ui/` contiene las pantallas agrupadas por dominio (emergencies, map, myteam, notices, organizations, profile...), todas implementadas con Jetpack Compose para mantener una interfaz declarativa y coherente. El paquete `net/` aísla toda la comunicación HTTP en un único cliente reutilizable, evitando duplicar la lógica de conexión en cada pantalla. El paquete `data/dto/` contiene los modelos de intercambio con el backend, separando así la representación externa de la lógica interna. El paquete `messaging/` gestiona la recepción de notificaciones push Firebase de forma independiente al resto de la aplicación. Finalmente, `util/` agrupa utilidades transversales (transformación de coordenadas, formato de fechas, mapeo de iconos) que pueden ser usadas desde cualquier pantalla sin crear dependencias circulares.

```
EmergencyApp/app/src/main/
├── AndroidManifest.xml
├── java/es/udc/emergencyapp/
│   ├── LoginActivity.kt
│   ├── MainActivity.kt
│   ├── SignupActivity.kt
│   ├── LocaleHelper.kt
│   ├── data/dto/
│   │   ├── EmergencyDto.kt
│   │   ├── NoticeDto.kt
│   │   └── OrganizationDto.kt
│   ├── messaging/
│   │   └── AppFirebaseMessagingService.kt
│   ├── net/
│   │   └── HttpClient.kt
│   ├── ui/
│   │   ├── common/
│   │   ├── emergencies/
│   │   ├── map/
│   │   ├── myteam/
│   │   ├── notices/
│   │   ├── organizations/
│   │   ├── profile/
│   │   └── theme/
│   └── util/
│       ├── CoordinateTransforms.kt
│       ├── DateUtils.kt
│       └── EmergencyTypeIcon.kt
└── res/
    ├── drawable/
    ├── values/
    ├── values-es/
    ├── values-gl/
    └── values-night/
```
