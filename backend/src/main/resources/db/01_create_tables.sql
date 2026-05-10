-- Crear extensión PostGIS si no existe
CREATE
EXTENSION IF NOT EXISTS postgis;

-- Configurar SRID
SET
search_path TO public;
SELECT PostGIS_Extensions_Upgrade();

-- Crear tablas

CREATE TABLE IF NOT EXISTS emergency_type
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS emergency
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(255),
    created_at  TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    location    geometry(Point, 25829),
    type_id     BIGINT,
    emergency_index  VARCHAR(255) NOT NULL,
    resolved_at TIMESTAMP(3)
);

CREATE TABLE organization_type
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE organization
(
    id                   BIGSERIAL PRIMARY KEY,
    code                 VARCHAR(255) UNIQUE,
    name                 VARCHAR(255) UNIQUE,
    headquarters_address VARCHAR(255),
    location             geometry(Point, 25829),
    created_at           TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    organization_type_id BIGINT                                    NOT NULL
);

CREATE TABLE quadrants
(
    gid            SERIAL PRIMARY KEY,
    clasico        VARCHAR(50),
    ccff           VARCHAR(50),
    escala         VARCHAR(50),
    nombre         VARCHAR(50),
    folla50        VARCHAR(50),
    folla25        VARCHAR(50),
    folla5         VARCHAR(50),
    revision       VARCHAR(50),
    GEOM           geometry(MultiPolygon, 25829)
);


-- Base table for Resource (joined inheritance strategy)
CREATE TABLE resource
(
    id              BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    organization_id BIGINT NOT NULL,
    quadrant_gid    BIGINT,
    deploy_at       TIMESTAMP(3),
    dismantle_at    TIMESTAMP(3),
    removed         BOOLEAN DEFAULT FALSE NOT NULL,
    dismantled      BOOLEAN DEFAULT FALSE NOT NULL,
    status          VARCHAR(50),
    resource_type   VARCHAR(50)
);

CREATE TABLE team
(
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(255) NOT NULL,
CONSTRAINT fk_team_resource_id FOREIGN KEY (id) REFERENCES resource (id)
);

CREATE TABLE vehicle
(
    id              BIGINT PRIMARY KEY,
    vehicle_plate   VARCHAR(255) UNIQUE,
    type            VARCHAR(255),
CONSTRAINT fk_vehicle_resource_id FOREIGN KEY (id) REFERENCES resource (id)
);

CREATE TABLE "user"
(
    id           BIGSERIAL PRIMARY KEY,
    email        VARCHAR(255) UNIQUE,
    password     VARCHAR(255),
    first_name   VARCHAR(255),
    last_name    VARCHAR(255),
    dni          VARCHAR(255),
    phone_number INTEGER,
    user_role    VARCHAR(255)                              NOT NULL,
    created_at   TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    team_id      BIGINT
);

CREATE TABLE notice
(
    id         BIGSERIAL PRIMARY KEY,
    body       VARCHAR(255),
    status     VARCHAR(255)                              NOT NULL,
    location   geometry(Point, 25829) NOT NULL,
    created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    user_id    BIGINT
);

CREATE TABLE image
(
    id         BIGSERIAL PRIMARY KEY,
    notice_id  BIGINT,
    name       VARCHAR(64),
    created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL
);

CREATE TABLE mobile_device
(
    id           BIGSERIAL PRIMARY KEY,
    fcm_token    VARCHAR(255) UNIQUE NOT NULL,
    last_seen_at TIMESTAMP(3),
    user_id      BIGINT UNIQUE NOT NULL
);


CREATE TABLE emergency_quadrant
(
    id           BIGSERIAL PRIMARY KEY,
    emergency_id BIGINT NOT NULL,
    quadrant_gid BIGINT NOT NULL,
    linked_at    TIMESTAMP(3),
    notes        VARCHAR(255)
);

CREATE TABLE assignment
(
    id                    BIGSERIAL PRIMARY KEY,
    emergency_quadrant_id BIGINT,
    emergency_id           BIGINT,
    resource_id           BIGINT NOT NULL,
    status                VARCHAR(50),
    notes                 VARCHAR(255),
    assigned_at           TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    accepted_at           TIMESTAMP(3),
    completed_at          TIMESTAMP(3),
    removed               BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE assignment_log
(
    id           BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT,
    emergency_id BIGINT,
    quadrant_id  INTEGER,
    resource_id  BIGINT,
    event_type   VARCHAR(255) NOT NULL,
    event_at     TIMESTAMP(3) NOT NULL,
    details      TEXT
);


ALTER TABLE assignment_log
    ADD CONSTRAINT fk_assignment_log_assignment_id
        FOREIGN KEY (assignment_id)
            REFERENCES assignment (id);

ALTER TABLE assignment_log
    ADD CONSTRAINT fk_assignment_log_emergency_id
        FOREIGN KEY (emergency_id)
            REFERENCES emergency (id);

ALTER TABLE assignment_log
    ADD CONSTRAINT fk_assignment_log_quadrant_id
        FOREIGN KEY (quadrant_id)
            REFERENCES quadrants (gid);

ALTER TABLE assignment_log
    ADD CONSTRAINT fk_assignment_log_resource_id
        FOREIGN KEY (resource_id)
            REFERENCES resource (id);

CREATE INDEX idx_assignment_log_emergency ON assignment_log (emergency_id);
CREATE INDEX idx_assignment_log_assignment ON assignment_log (assignment_id);




-- Añadir restricciones de llave foránea
ALTER TABLE organization
    ADD CONSTRAINT fk_organization_type_id
        FOREIGN KEY (organization_type_id)
            REFERENCES organization_type (id);

-- quadrants no longer tiene columna emergency_id; la relación se mantiene en emergency_quadrant

-- legacy emergency_quadrant_log foreign keys removed

-- Resource-level foreign keys: organization and quadrant belong to resource base table
ALTER TABLE resource
    ADD CONSTRAINT fk_resource_organization_id
        FOREIGN KEY (organization_id)
            REFERENCES organization (id);

ALTER TABLE resource
    ADD CONSTRAINT fk_resource_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);


ALTER TABLE "user"
    ADD CONSTRAINT fk_team_id
        FOREIGN KEY (team_id)
            REFERENCES team (id);

ALTER TABLE mobile_device
    ADD CONSTRAINT fk_mobile_device_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (id);

ALTER TABLE notice
    ADD CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES "user" (id);

ALTER TABLE image
    ADD CONSTRAINT fk_notice_id
        FOREIGN KEY (notice_id)
            REFERENCES notice (id);

-- Crear índices espaciales
CREATE INDEX idx_organization_location ON organization USING GIST(location);
CREATE INDEX idx_quadrants_geom ON quadrants USING GIST(GEOM);
CREATE INDEX idx_notice_location ON notice USING GIST(location);

-- Foreign keys para emergency y related entities
ALTER TABLE emergency_quadrant
    ADD CONSTRAINT fk_emergency_quadrant_emergency_id
        FOREIGN KEY (emergency_id)
            REFERENCES emergency (id);

ALTER TABLE emergency_quadrant
    ADD CONSTRAINT fk_emergency_quadrant_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

-- Foreign key from emergency to emergency_type
ALTER TABLE emergency
    ADD CONSTRAINT fk_emergency_type_id
        FOREIGN KEY (type_id)
            REFERENCES emergency_type (id);

ALTER TABLE assignment
    ADD CONSTRAINT fk_assignment_emergency_quadrant_id
        FOREIGN KEY (emergency_quadrant_id)
            REFERENCES emergency_quadrant (id);

ALTER TABLE assignment
    ADD CONSTRAINT fk_assignment_emergency_id
        FOREIGN KEY (emergency_id)
            REFERENCES emergency (id);

-- Índices para búsquedas rápidas
CREATE INDEX idx_emergency_location ON emergency USING GIST(location);
CREATE INDEX idx_emergency_quadrant_emergency_id ON emergency_quadrant (emergency_id);
CREATE INDEX idx_assignment_resource ON assignment (resource_id);
CREATE INDEX idx_assignment_emergency_quadrant ON assignment (emergency_quadrant_id);
CREATE INDEX idx_assignment_emergency ON assignment (emergency_id);

-- Constraints para status enums (representados como VARCHAR en la DB)
ALTER TABLE assignment
    ADD CONSTRAINT chk_assignment_status
        CHECK (status IN ('PENDING','ACCEPTED','COMPLETED'));

-- Resource status constraint (applies to all resource subtypes)
ALTER TABLE resource
    ADD CONSTRAINT chk_resource_status
        CHECK (status IN ('AVAILABLE','BUSY'));
