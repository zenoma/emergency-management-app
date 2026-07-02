# Jerarquía de Datos - Backend

```mermaid
flowchart TB
    subgraph Org["ORGANIZACIÓN"]
        direction TB
        O1["Organización<br/><small>dueña de recursos</small>"]
        OT["OrganizationType<br/><small>clasificación</small>"]
        O1 --- OT
    end

    subgraph Res["RECURSOS"]
        direction TB
        R["Recurso (abstracto)<br/><small>status, deployAt, dismantleAt</small>"]
        T["Equipo (Team)<br/><small>código</small>"]
        V["Vehículo (Vehicle)<br/><small>matrícula, tipo</small>"]
        R --> T
        R --> V
        U["Usuario (User)<br/><small>rol, email, dni</small>"]
        M["MobileDevice<br/><small>fcmToken</small>"]
        T -.-> U
        U -.-> M
    end

    Org --> |"pertenece a"| R

    subgraph Emer["EMERGENCIAS"]
        direction TB
        E["Emergencia<br/><small>índice, descripción, location</small>"]
        ET["EmergencyType<br/><small>tipo (incendio, inundación...)</small>"]
        ETR["EmergencyTypeRule<br/><small>reglas de recomendación</small>"]
        E --- ET
        ET --- ETR
    end

    subgraph Geo["VINCULACIÓN GEOGRÁFICA"]
        direction TB
        EQ["EmergencyQuadrant<br/><small>tabla puente con linkedAt</small>"]
        Q["Quadrante<br/><small>polígono geoespacial</small>"]
        E --- EQ
        EQ --- Q
    end

    subgraph Assign["ASIGNACIONES"]
        direction TB
        A["Asignación (Assignment)<br/><small>status, notas, fechas</small>"]
        AL["AssignmentLog<br/><small>auditoría de eventos</small>"]
        A --- AL
    end

    E --- A
    T --- A
    V --- A
```

**Niveles jerárquicos:**

| Nivel | Entidad | Dependencia |
|-------|---------|-------------|
| 1 | **Organización** | Raíz organizativa |
| 2 | **Recurso** (Team/Vehicle) | Pertenece a Organización |
| 3 | **Usuario** | Pertenece a Team |
| 4 | **MobileDevice** | 1:1 con Usuario |
| 1 | **Emergencia** | Raíz operativa |
| 2 | **EmergencyType** | Clasifica Emergencia |
| 2 | **EmergencyQuadrant / Quadrant** | Vinculación geográfica |
| 2 | **Assignment** | Puente Emergencia ↔ Recurso |
| 3 | **AssignmentLog** | Auditoría de Assignment |
