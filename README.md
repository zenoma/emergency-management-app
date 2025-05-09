# Firefigther Coordination App 

## Requirements

- Node 14.15.0+.
- Java 11 (tested with AdoptOpenJDK 11).
- Maven 3.6+.
- PostgreSQL 14+.
- POSTGIS 3.1.4+.

## Running Backend
Inside Backend folder.

- Option 1: using H2 in-memory database
``` 
mvn spring-boot:run -P h2
```

- Option 2: using PostgreSQL database
``` 
mvn spring-boot:run -P postgresql
```

## Running frontend

Inside Frontend folder.

```
npm install (only first time to download libraries)
npm start
```
