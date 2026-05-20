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
  ((SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'), 0, '{"when":{"type":"name_contains","value":"forestal"},"then":{"teams":4,"vehicles":2,"maxDistanceKm":150,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'), 1, '{"when":{"type":"name_contains","value":"estructural"},"then":{"teams":3,"vehicles":2,"maxDistanceKm":100,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'), 2, '{"when":{"type":"name_contains","value":"incend"},"then":{"teams":3,"vehicles":2,"maxDistanceKm":120,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Incendio (forestal/estructural)'), 3, '{"when":{"type":"default"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":80,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Inundación'), 0, '{"when":{"type":"name_contains","value":"inund"},"then":{"teams":4,"vehicles":2,"maxDistanceKm":120,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Inundación'), 1, '{"when":{"type":"name_contains","value":"inunda"},"then":{"teams":3,"vehicles":2,"maxDistanceKm":90,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Inundación'), 2, '{"when":{"type":"name_contains","value":"riada"},"then":{"teams":3,"vehicles":1,"maxDistanceKm":90,"preferredOrganizationType":"Entidades Locales"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Inundación'), 3, '{"when":{"type":"default"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":70,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 0, '{"when":{"type":"name_contains","value":"desprend"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":60,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 1, '{"when":{"type":"name_contains","value":"derrumbe"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":60,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 2, '{"when":{"type":"name_contains","value":"derrum"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Brigada"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Derrumbe/Desprendimiento'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":40,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Accidente vial'), 0, '{"when":{"type":"name_contains","value":"accident"},"then":{"teams":2,"vehicles":2,"maxDistanceKm":50,"preferredOrganizationType":"Patrulla de vigilancia"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Accidente vial'), 1, '{"when":{"type":"name_contains","value":"vial"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":40,"preferredOrganizationType":"Patrulla de vigilancia"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Accidente vial'), 2, '{"when":{"type":"name_contains","value":"carretera"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":35,"preferredOrganizationType":"Patrulla de vigilancia"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Accidente vial'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":30,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Emergencia sanitaria'), 0, '{"when":{"type":"name_contains","value":"sanit"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":25,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Emergencia sanitaria'), 1, '{"when":{"type":"name_contains","value":"sanitaria"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":20,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Emergencia sanitaria'), 2, '{"when":{"type":"name_contains","value":"emergencia"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":15,"preferredOrganizationType":"Entidades Locales"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Emergencia sanitaria'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":15,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Riesgo químico'), 0, '{"when":{"type":"name_contains","value":"quim"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo químico'), 1, '{"when":{"type":"name_contains","value":"quím"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo químico'), 2, '{"when":{"type":"name_contains","value":"riesgo"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":40,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo químico'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":35,"preferredOrganizationType":"Mecánicos"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Riesgo industrial'), 0, '{"when":{"type":"name_contains","value":"industrial"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo industrial'), 1, '{"when":{"type":"name_contains","value":"indus"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":50,"preferredOrganizationType":"Mecánicos"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo industrial'), 2, '{"when":{"type":"name_contains","value":"riesgo"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":40,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Riesgo industrial'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":35,"preferredOrganizationType":"Mecánicos"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Temporal/Evento meteorológico'), 0, '{"when":{"type":"name_contains","value":"meteor"},"then":{"teams":2,"vehicles":1,"maxDistanceKm":80,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Temporal/Evento meteorológico'), 1, '{"when":{"type":"name_contains","value":"temporal"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":80,"preferredOrganizationType":"Patrulla de vigilancia"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Temporal/Evento meteorológico'), 2, '{"when":{"type":"name_contains","value":"evento"},"then":{"teams":1,"vehicles":1,"maxDistanceKm":60,"preferredOrganizationType":"Entidades Locales"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Temporal/Evento meteorológico'), 3, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":50,"preferredOrganizationType":"Patrulla de vigilancia"}}'::jsonb),

  ((SELECT id FROM emergency_type WHERE name = 'Otros'), 0, '{"when":{"type":"name_contains","value":"otros"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":25,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Otros'), 1, '{"when":{"type":"name_contains","value":"otro"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":25,"preferredOrganizationType":"Centro Coordinación"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Otros'), 2, '{"when":{"type":"default"},"then":{"teams":1,"vehicles":0,"maxDistanceKm":25,"preferredOrganizationType":"Entidades Locales"}}'::jsonb),
  ((SELECT id FROM emergency_type WHERE name = 'Otros'), 3, '{"when":{"type":"default"},"then":{"teams":0,"vehicles":0,"maxDistanceKm":15}}'::jsonb)
ON CONFLICT DO NOTHING;


INSERT INTO organization_type (name)
VALUES ('Centro Coordinación'),
       ('Brigada'),
       ('Patrulla de vigilancia'),
       ('Mecánicos'),
       ('Vehículos'),
       ('Entidades Locales')
ON CONFLICT (name) DO NOTHING;
