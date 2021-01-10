CREATE OR REPLACE FUNCTION fn_user_update(entity_id UUID, body JSONB)
  RETURNS VOID AS
$$
BEGIN
  EXECUTE FORMAT(
      'UPDATE users SET name = %L, phone = %L, email = %L WHERE id = %L',
      body ->> 'name', body ->> 'phone', body ->> 'email', entity_id);
END;
$$
LANGUAGE plpgsql;