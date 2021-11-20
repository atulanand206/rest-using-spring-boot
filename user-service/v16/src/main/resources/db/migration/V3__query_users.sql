CREATE OR REPLACE FUNCTION public.fn_user_by_id(user_id UUID)
    RETURNS SETOF users AS
$$
DECLARE
    result users;
BEGIN
    EXECUTE FORMAT(
            'SELECT * from users where id = %L',
            user_id) INTO STRICT result;
    RETURN NEXT result;
EXCEPTION
    WHEN TOO_MANY_ROWS THEN
        RAISE EXCEPTION 'id must be unique in the table.';
    WHEN NO_DATA_FOUND THEN
        RETURN;
END;
$$ LANGUAGE plpgsql;