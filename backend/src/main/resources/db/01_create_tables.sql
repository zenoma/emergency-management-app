-- Crear extensión PostGIS si no existe
CREATE
EXTENSION IF NOT EXISTS postgis;

-- Configurar SRID
SET
search_path TO public;
SELECT PostGIS_Extensions_Upgrade();

-- Crear tablas
CREATE TABLE fire
(
    id              BIGSERIAL PRIMARY KEY,
    description     VARCHAR(255),
    type            VARCHAR(255)                              NOT NULL,
    fire_index      VARCHAR(255)                              NOT NULL,
    created_at      TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NOT NULL,
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
    fire_id        BIGINT,
    fire_linked_at TIMESTAMP(3)
);

CREATE TABLE fire_quadrant_log
(
    id              BIGSERIAL PRIMARY KEY,
    fire_id         BIGINT,
    quadrant_gid    BIGINT,
    linked_at       TIMESTAMP(3) NOT NULL,
    extinguished_at TIMESTAMP(3) NOT NULL
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

-- Añadir restricciones de llave foránea
ALTER TABLE organization
    ADD CONSTRAINT fk_organization_type_id
        FOREIGN KEY (organization_type_id)
            REFERENCES organization_type (id);

ALTER TABLE quadrants
    ADD CONSTRAINT fk_quadrants_fire_id
        FOREIGN KEY (fire_id)
            REFERENCES fire (id);

ALTER TABLE fire_quadrant_log
    ADD CONSTRAINT fk_vehicle_quadrant_log_fire_id
        FOREIGN KEY (fire_id)
            REFERENCES fire (id);

ALTER TABLE fire_quadrant_log
    ADD CONSTRAINT fk_fire_quadrant_log_quadrant_gid
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


