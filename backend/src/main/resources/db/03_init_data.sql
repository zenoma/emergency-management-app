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

INSERT INTO emergency_type_rule (emergency_type_id, priority, rule_json) VALUES
  ((SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'), 1, '{"when":{"type":"name_contains","value":"incend"},"then":{"teams":3,"vehicles":2,"maxDistanceKm":120,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Inundación'), 1, '{"when":{"type":"name_contains","value":"inund"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":90,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 1, '{"when":{"type":"name_contains","value":"desprend"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":60,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 2, '{"when":{"type":"name_contains","value":"derrum"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":60,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Accidente vial'), 1, '{"when":{"type":"name_contains","value":"accident"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":40,"preferredOrganizationType":"Patrulla"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Emergencia sanitaria'), 1, '{"when":{"type":"name_contains","value":"sanit"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":30,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo químico'), 1, '{"when":{"type":"name_contains","value":"quim"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo industrial'), 1, '{"when":{"type":"name_contains","value":"industrial"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Temporal/Evento meteorológico'), 1, '{"when":{"type":"name_contains","value":"temporal"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":80,"preferredOrganizationType":"Patrulla"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Otros'), 1, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":25}}'::jsonb)
ON CONFLICT DO NOTHING;


INSERT INTO organization_type (name)
VALUES ('Centro Coordinación'),
       ('Brigada'),
       ('Patrulla de vigilancia'),
       ('Mecánicos'),
       ('Vehículos'),
       ('Entidades Locales')
ON CONFLICT (name) DO NOTHING;
