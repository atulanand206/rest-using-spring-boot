package com.atul.gitbook.learn.utils;

import org.springframework.lang.Nullable;

public class SerializationException extends RuntimeException {

    private SerializationException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }

    static SerializationException newSerializationException(
            @Nullable final Throwable cause) {
        return new SerializationException("Failed to serialize object.", cause);
    }

    static SerializationException newDeserializationException(
            @Nullable final Throwable cause) {
        return new SerializationException("Failed to deserialize object.", cause);
    }
}
