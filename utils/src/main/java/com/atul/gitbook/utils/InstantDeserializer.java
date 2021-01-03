package com.atul.gitbook.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstantDeserializer extends StdDeserializer<Instant> {

    public InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(
            final JsonParser jsonParser,
            final DeserializationContext context)
            throws IOException {
        final var mapper = (ObjectMapper) jsonParser.getCodec();
        TextNode root = mapper.readTree(jsonParser);
        try {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME
                    .parse(root.textValue(), Instant::from);
        } catch (DateTimeParseException ex) {
            throw SerializationException.newDeserializationException(ex);
        }
    }
}