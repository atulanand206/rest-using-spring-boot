package com.atul.gitbook.learn.utils;

public class Serializers {
    /**
     * Creates a typed JSON serializer.
     *
     * @param clazz The object's type.
     * @param <T>   The object's type.
     * @return A new {@link Serializer}.
     */
    public static <T> Serializer<T> newJsonSerializer(
            final Class<T> clazz) {
        return new JacksonJsonSerializer<>(clazz);
    }
}
