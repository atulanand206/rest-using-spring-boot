package com.atul.gitbook.learn.utils;

/**
 * A generic serialization interface that supports serializing and deserializing to and from text.
 *
 * @param <T> The object's type.
 */
public interface Serializer<T extends Object> {

    /**
     * Deserializes an object from the given serialized contents.
     *
     * @param content The serialized content.
     * @return The object.
     * @throws SerializationException If there was a problem deserializing the object.
     */
    T deserialize(String content);

    /**
     * Serializes an object.
     *
     * @param object The object to serialize.
     * @return The serialized content.
     * @throws SerializationException If there was a problem serializing the object.
     */
    String serialize(T object);

    /**
     * Serializes an object optionally using a pretty formatter.
     *
     * @param object The object to serialize.
     * @return The serialized content.
     * @throws SerializationException If there was a problem serializing the object.
     */
    default String serializePretty(T object) {
        return serialize(object);
    }

    /**
     * Performs a deep copy of an object by serializing and deserializing it in one go.
     *
     * @param object The object to copy.
     * @return The new object.
     */
    default T copy(T object) {
        return deserialize(serialize(object));
    }
}
