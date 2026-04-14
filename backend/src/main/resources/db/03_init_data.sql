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
       ('Entidades Locales');
