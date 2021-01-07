package com.atul.gitbook.learn.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.Instant;

public class JacksonJsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper fMapper;
    private final ObjectReader fObjectReader;
    private final ObjectWriter fObjectWriter;

    public JacksonJsonSerializer(Class<T> clazz) {
        fMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule().addDeserializer(Instant.class,
                        new InstantDeserializer()))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        fObjectReader = fMapper.readerFor(clazz);
        fObjectWriter = fMapper.writerFor(clazz);
    }

    private ObjectReader getReader() {
        return fObjectReader;
    }

    private ObjectWriter getWriter() {
        return fObjectWriter;
    }

    private ObjectWriter getPrettyWriter() {
        return fMapper.writerWithDefaultPrettyPrinter();
    }

    @Override
    public T deserialize(String content) {
        try {
            return getReader().readValue(content);
        } catch (IOException ex) {
            throw SerializationException.newDeserializationException(ex);
        }
    }

    @Override
    public String serialize(T object) {
        return serialize(getWriter(), object);
    }

    @Override
    public String serializePretty(T object) {
        return serialize(getPrettyWriter(), object);
    }

    private String serialize(
            final ObjectWriter writer,
            final T object) {
        try {
            return writer.writeValueAsString(object);
        } catch (IOException ex) {
            throw SerializationException.newSerializationException(ex);
        }
    }
}
