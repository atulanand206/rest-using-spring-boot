package com.atul.gitbook.learn.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SerializationTest {

    static class DummyModel {

        @JsonProperty("required_string")
        String requiredString;

        @JsonProperty("instant")
        Instant instant;

        @JsonProperty("list_string")
        List<String> listString;

        public DummyModel() {
        }

        public DummyModel(String requiredString, List<String> listString, Instant instant) {
            this.requiredString = requiredString;
            this.listString = listString;
            this.instant = instant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var that = (DummyModel) o;
            return Objects.equals(requiredString, that.requiredString) &&
                    Objects.equals(instant, that.instant) &&
                    Objects.equals(listString, that.listString);
        }
    }

    @ParameterizedTest
    @MethodSource("parametersForTestDeserializationFailsOnMalformedInput")
    <T> void testDeserializationFailsOnMalformedInput(
            final Serializer<T> serializer,
            final String malformedInput) {
        Assertions.assertThrows(
                SerializationException.class,
                () -> serializer.deserialize(malformedInput));
    }

    private static Stream<Arguments> parametersForTestDeserializationFailsOnMalformedInput() {
        final var malformedInput = "{";
        return Stream.of(Arguments.of(Serializers.newJsonSerializer(DummyModel.class), malformedInput));
    }

    @ParameterizedTest
    @MethodSource("parametersForTestSerializer")
    <T> void testSerializer(
            final Serializer<T> serializer,
            final T expected) {
        // Verify if a new object is created with same values.
        T actual = serializer.copy(expected);
        Assertions.assertNotSame(expected, actual);
        Assertions.assertEquals(expected, actual);

        // Verify deserialization works under pretty output.
        String prettyOutput = serializer.serializePretty(expected);
        actual = serializer.deserialize(prettyOutput);
        Assertions.assertNotSame(expected, actual);
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> parametersForTestSerializer() {
        var expected = new DummyModel("a", Arrays.asList("1", "2", "3"), Instant.now());
        return Stream.of(Arguments.of(Serializers.newJsonSerializer(DummyModel.class), expected));
    }

}
