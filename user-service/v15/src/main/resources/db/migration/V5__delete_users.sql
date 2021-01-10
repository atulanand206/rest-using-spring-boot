CREATE OR REPLACE FUNCTION fn_user_delete(entity_id UUID)
  RETURNS VOID AS
$$
BEGIN
  EXECUTE FORMAT(
      'DELETE FROM users WHERE id = %L', entity_id);
END;
$$
LANGUAGE plpgsql;