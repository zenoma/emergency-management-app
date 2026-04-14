-- Crear extensión PostGIS si no existe
CREATE
EXTENSION IF NOT EXISTS postgis;

-- Configurar SRID
SET
search_path TO public;
SELECT PostGIS_Extensions_Upgrade();

-- Crear tablas
CREATE TABLE emergency
(
    id          BIGSERIAL PRIMARY KEY,
    description VARCHAR(255),
    created_at  TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    location    geometry(Point, 25829),
    type        VARCHAR(255),
    emergency_index  VARCHAR(255) NOT NULL,
    extinguished_at TIMESTAMP(3)
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
    GEOM           geometry(MultiPolygon, 25829),
    emergency_id        BIGINT,
    emergency_linked_at TIMESTAMP(3)
);


CREATE TABLE team
(
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(255)                              NOT NULL,
    created_at      TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    organization_id BIGINT                                    NOT NULL,
    quadrant_gid    BIGINT,
    deploy_at       TIMESTAMP(3),
    dismantle_at    TIMESTAMP(3)
);

CREATE TABLE team_quadrant_log
(
    id           BIGSERIAL PRIMARY KEY,
    team_id      BIGINT,
    quadrant_gid BIGINT,
    deploy_at    TIMESTAMP(3) NOT NULL,
    retract_at   TIMESTAMP(3) NOT NULL
);

CREATE TABLE vehicle
(
    id              BIGSERIAL PRIMARY KEY,
    vehicle_plate   VARCHAR(255) UNIQUE,
    type            VARCHAR(255),
    created_at      TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    organization_id BIGINT                                    NOT NULL,
    quadrant_gid    BIGINT,
    deploy_at       TIMESTAMP(3),
    dismantle_at    TIMESTAMP(3)
);

CREATE TABLE vehicle_quadrant_log
(
    id           BIGSERIAL PRIMARY KEY,
    vehicle_id   BIGINT,
    quadrant_gid BIGINT,
    deploy_at    TIMESTAMP(3) NOT NULL,
    retract_at   TIMESTAMP(3) NOT NULL
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


CREATE TABLE emergency_quadrant
(
    id           BIGSERIAL PRIMARY KEY,
    emergency_id BIGINT NOT NULL,
    quadrant_gid BIGINT NOT NULL,
    notes        VARCHAR(255)
);

CREATE TABLE emergency_quadrant_log
(
    id              BIGSERIAL PRIMARY KEY,
    emergency_id    BIGINT,
    quadrant_gid    BIGINT,
    linked_at       TIMESTAMP(3) NOT NULL,
    extinguished_at TIMESTAMP(3) NOT NULL
);

CREATE TABLE assignment
(
    id                    BIGSERIAL PRIMARY KEY,
    emergency_quadrant_id BIGINT NOT NULL,
    resource_type         VARCHAR(50) NOT NULL,
    resource_id           BIGINT NOT NULL,
    assigned_at           TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
    released_at           TIMESTAMP(3),
    status                VARCHAR(50),
    removed               BOOLEAN DEFAULT FALSE NOT NULL
);

-- Añadir restricciones de llave foránea
ALTER TABLE organization
    ADD CONSTRAINT fk_organization_type_id
        FOREIGN KEY (organization_type_id)
            REFERENCES organization_type (id);

ALTER TABLE quadrants
    ADD CONSTRAINT fk_quadrants_emergency_id
        FOREIGN KEY (emergency_id)
            REFERENCES emergency (id);

ALTER TABLE emergency_quadrant_log
    ADD CONSTRAINT fk_vehicle_quadrant_log_emergency_id
        FOREIGN KEY (emergency_id)
            REFERENCES emergency (id);

ALTER TABLE emergency_quadrant_log
    ADD CONSTRAINT fk_emergency_quadrant_log_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

ALTER TABLE team
    ADD CONSTRAINT fk_organization_id
        FOREIGN KEY (organization_id)
            REFERENCES organization (id);

ALTER TABLE team
    ADD CONSTRAINT fk_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

ALTER TABLE team_quadrant_log
    ADD CONSTRAINT fk_team_quadrant_log_team_id
        FOREIGN KEY (team_id)
            REFERENCES team (id);

ALTER TABLE team_quadrant_log
    ADD CONSTRAINT fk_team_quadrant_log_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

ALTER TABLE vehicle
    ADD CONSTRAINT fk_vehicle_organization_id
        FOREIGN KEY (organization_id)
            REFERENCES organization (id);

ALTER TABLE vehicle
    ADD CONSTRAINT fk_vehicle_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

ALTER TABLE vehicle_quadrant_log
    ADD CONSTRAINT fk_vehicle_quadrant_log_vehicle_id
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicle (id);

ALTER TABLE vehicle_quadrant_log
    ADD CONSTRAINT fk_vehicle_quadrant_log_quadrant_gid
        FOREIGN KEY (quadrant_gid)
            REFERENCES quadrants (gid);

ALTER TABLE "user"
    ADD CONSTRAINT fk_team_id
        FOREIGN KEY (team_id)
            REFERENCES team (id);

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

ALTER TABLE assignment
    ADD CONSTRAINT fk_assignment_emergency_quadrant_id
        FOREIGN KEY (emergency_quadrant_id)
            REFERENCES emergency_quadrant (id);

-- Índices para búsquedas rápidas
CREATE INDEX idx_emergency_location ON emergency USING GIST(location);
CREATE INDEX idx_emergency_quadrant_emergency_id ON emergency_quadrant (emergency_id);
CREATE INDEX idx_assignment_resource ON assignment (resource_type, resource_id);
CREATE INDEX idx_assignment_emergency_quadrant ON assignment (emergency_quadrant_id);
