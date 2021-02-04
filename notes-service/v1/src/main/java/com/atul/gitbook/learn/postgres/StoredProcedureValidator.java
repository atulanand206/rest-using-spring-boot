package com.atul.gitbook.learn.postgres;

import com.atul.gitbook.learn.Preconditions;

import java.sql.CallableStatement;
import java.util.regex.Pattern;

/**
 * Stored procedure names need to be sanitized to avoid SQL Injection attacks, especially while
 * building a {@link CallableStatement} dynamically. For now, a stored procedure name should be
 * alphanumeric and can contain underscores. It should also start with an alphabet.
 */
public final class StoredProcedureValidator {
    private static final Pattern DEFAULT_SPROC_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");

    private StoredProcedureValidator() {
    }

    public static void validateStoredProcedure(final String sproc) {
        Preconditions.validateNotNull(sproc);
        Preconditions.validateIsTrue(
                DEFAULT_SPROC_PATTERN.matcher(sproc).matches(),
                "StoredProcedure is not in correct format.");
    }
}
