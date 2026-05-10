-- Insert emergency types using name only (idempotent)
INSERT INTO emergency_type (name) VALUES
  ('Incendio (forestal/estructural)'),
  ('Inundación'),
  ('Derrumbe/Desprendimiento'),
  ('Accidente vial'),
  ('Emergencia sanitaria'),
  ('Riesgo químico'),
  ('Riesgo industrial'),
  ('Temporal/Evento meteorológico'),
  ('Otros')
ON CONFLICT (name) DO NOTHING;


INSERT INTO organization_type (name)
VALUES ('Centro Coordinación'),
       ('Brigada'),
       ('Patrulla de vigilancia'),
       ('Mecánicos'),
       ('Vehículos'),
       ('Entidades Locales')
ON CONFLICT (name) DO NOTHING;

-- Organizations
INSERT INTO organization (id, code, name, headquarters_address, location, organization_type_id)
VALUES (101,
        'ORG-CC-001',
        'Centro Coordinador Galicia',
        'Raxoi, Santiago de Compostela',
        ST_GeomFromText('POINT(539500 4724000)', 25829),
        (SELECT id FROM organization_type WHERE name = 'Centro Coordinación'))
ON CONFLICT (id) DO NOTHING;

INSERT INTO organization (id, code, name, headquarters_address, location, organization_type_id)
VALUES (102,
        'ORG-BR-001',
        'Brigada Ourense Norte',
        'Av. de Zamora, Ourense',
        ST_GeomFromText('POINT(594900 4687400)', 25829),
        (SELECT id FROM organization_type WHERE name = 'Brigada'))
ON CONFLICT (id) DO NOTHING;

-- Resources: teams and vehicles
INSERT INTO resource (id, created_at, organization_id, status, resource_type, removed, dismantled)
VALUES (201, CURRENT_TIMESTAMP(3), 101, 'AVAILABLE', 'TEAM', FALSE, FALSE),
       (202, CURRENT_TIMESTAMP(3), 102, 'AVAILABLE', 'TEAM', FALSE, FALSE),
       (301, CURRENT_TIMESTAMP(3), 101, 'AVAILABLE', 'VEHICLE', FALSE, FALSE),
       (302, CURRENT_TIMESTAMP(3), 102, 'BUSY', 'VEHICLE', FALSE, FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO team (id, code)
VALUES (201, 'BRI-ALFA'),
       (202, 'BRI-BRAVO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO vehicle (id, vehicle_plate, type)
VALUES (301, '1234-BBB', 'Camioneta 4x4'),
       (302, '5678-CCC', 'Bomba nodriza')
ON CONFLICT (id) DO NOTHING;

-- Users, some attached to teams
INSERT INTO "user" (id, email, password, first_name, last_name, dni, phone_number, user_role, created_at, team_id)
VALUES (401, 'coord.galicia@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'John', 'Doe', '11111111A', 611111111, 'COORDINATOR', CURRENT_TIMESTAMP(3), 201),
       (402, 'marta.team@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'Marta', 'Lago', '22222222B', 622222222, 'USER', CURRENT_TIMESTAMP(3), 201),
       (403, 'diego.team@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'Diego', 'Fernandez', '33333333C', 633333333, 'USER', CURRENT_TIMESTAMP(3), 201),
       (404, 'lara.team@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'Lara', 'Mendez', '44444444D', 644444444, 'USER', CURRENT_TIMESTAMP(3), 202),
       (405, 'manager.ourense@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'Iria', 'Souto', '55555555E', 655555555, 'MANAGER', CURRENT_TIMESTAMP(3), NULL),
       (406, 'alex.vehicle@example.com', '$2a$10$.EY8p807N/jaGFK96BfhWOb9sI9NUV/6FtmKflf92nM7hUc7sFj3O', 'Alex', 'Varela', '66666666F', 666666666, 'USER', CURRENT_TIMESTAMP(3), 202)
ON CONFLICT (id) DO NOTHING;

-- Emergencies
INSERT INTO emergency (id, description, created_at, location, type_id, emergency_index, resolved_at)
VALUES (501,
        'Corte de carretera por caída de piedras',
        CURRENT_TIMESTAMP(3),
        ST_GeomFromText('POINT(539650 4724100)', 25829),
        (SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'),
        'UNO',
        NULL),
       (502,
        'Columna de humo en zona forestal',
        CURRENT_TIMESTAMP(3),
        NULL,
        (SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'),
        'DOS',
        NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO emergency_quadrant (id, emergency_id, quadrant_gid, linked_at, notes)
VALUES (601, 502, (SELECT gid FROM quadrants WHERE nombre = 'Ourense' LIMIT 1), CURRENT_TIMESTAMP(3), 'Zona de desprendimiento en el acceso norte')
ON CONFLICT (id) DO NOTHING;

-- Assignments: mix of point-based and quadrant-based
INSERT INTO assignment (id, emergency_quadrant_id, emergency_id, resource_id, status, notes, assigned_at, accepted_at, completed_at, removed)
VALUES (701, 601, 502, 301, 'PENDING', 'Primera salida para valoración de incendio', CURRENT_TIMESTAMP(3), NULL, NULL, FALSE),
       (702, NULL, 501, 201, 'ACCEPTED', 'Equipo asignado al perímetro del derrumbe', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), NULL, FALSE),
       (703, 601, 502, 302, 'COMPLETED', 'Apoyo logístico cerrado tras la intervención', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), FALSE)
ON CONFLICT (id) DO NOTHING;
