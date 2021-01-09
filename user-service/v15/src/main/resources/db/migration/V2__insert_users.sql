CREATE OR REPLACE FUNCTION fn_user_create(entity_id UUID, body JSONB)
  RETURNS VOID AS
$$
BEGIN
  INSERT INTO users (id, name, phone, email, administrator)
  VALUES (entity_id, body ->> 'name', body ->> 'phone', body ->> 'email', (body ->> 'administrator') :: BOOLEAN);
END;
$$
LANGUAGE plpgsql;