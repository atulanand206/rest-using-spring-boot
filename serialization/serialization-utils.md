# Serialization Utils

## Reducing dependencies

We've autowired `IUserService` to create a user in the delete user success test, which should ideally not be happening. We should be making a `createUserRequest` to create a new user rather than directly interacting with the service. In our system, all the validations are in the service layer, should there won't be any abnormal behaviour but that won't be the case with every system and we should treat our tests as though written from the users perspective and users only have access to the endpoints.

In all of our tests we've never tried to deserialise the response body. For creating a user, we'd be required to parse the response body into a `User` object. We should now look into implementing the Jackson deserialisers in order to eliminate the auto wiring.

Also, there are many tests which depend on the `USER` object from `InMemoryRepository`. We should ideally be creating those test users by making the create request itself. We can use the `ADMINISTRATOR` object right now as there is no way to add administrator at this point. We would look into creating that endpoint as well later. We definitely would be removing it once we move to a persistent database.

## Serialization/Deserialization

Serialization is a generic feature and it'd be nice to have a separate package to handle serialization of all types.

Let's add a utils package for handling these types of functionalities.

You can delete the already present test.

### Maven Dependencies for Jackson

```markup
<dependency>
   <groupId>com.fasterxml.jackson.core</groupId>
   <artifactId>jackson-annotations</artifactId>
   <version>2.10.0</version>
</dependency>
<dependency>
   <groupId>com.fasterxml.jackson.datatype</groupId>
   <artifactId>jackson-datatype-jdk8</artifactId>
   <version>2.10.0</version>
</dependency>
<dependency>
   <groupId>com.fasterxml.jackson.datatype</groupId>
   <artifactId>jackson-datatype-jsr310</artifactId>
   <version>2.10.0</version>
</dependency>
```

Let's add a test class `SerializationTest` in this new package.

## Write a new test

```java
@ParameterizedTest
@MethodSource("parametersForTestDeserializationFailsOnMalformedInput")
<T> void testDeserializationFailsOnMalformedInput(
        final Serializer<T> serializer,
        final String malformedInput) {
    Assertions.assertThrows(
            SerializationException.class,
            () -> serializer.deserialize(malformedInput));
}

static Stream<Arguments> parametersForTestDeserializationFailsOnMalformedInput() {
    final var malformedInput = "{";
    return Stream.of(Arguments.of(Serializers.newJsonSerializer(DummyModel.class), malformedInput));
}
```

### Try to run the test

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[20,19] cannot find symbol
  symbol:   class Serializer
  location: class com.atul.gitbook.learn.utils.SerializationTest
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[23,17] cannot find symbol
  symbol:   class SerializationException
  location: class com.atul.gitbook.learn.utils.SerializationTest
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[29,69] cannot find symbol
  symbol:   class DummyModel
  location: class com.atul.gitbook.learn.utils.SerializationTest
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[29,39] cannot find symbol
  symbol:   variable Serializers
  location: class com.atul.gitbook.learn.utils.SerializationTest
```

### Let's try to resolve the failure

We have to create a few classes now. We will be implementing the `Serializer`, `Serializers`, `SerializationException` and the `DummyModel`.

```java
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
```

```java
public interface Serializer<T extends Object> {

    /**
     * Deserializes an object from the given serialized contents.
     *
     * @param content The serialized content.
     * @return The object.
     * @throws SerializationException If there was a problem deserializing the object.
     */
    T deserialize(String content);
}
```

```java
public class JacksonJsonSerializer<T> implements Serializer<T> {
    public <T> JacksonJsonSerializer(Class<T> clazz) {
    }

    @Override
    public T deserialize(String content) {
        return null;
    }
} 
```

```java
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
```

```java
public class SerializationException extends RuntimeException {

    private SerializationException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }

}
```

### Try to run the test

```java
[ERROR] testDeserializationFailsOnMalformedInput{Serializer, String}[1]  Time elapsed: 0.061 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: Expected com.atul.gitbook.learn.jackson.SerializationException to be thrown, but nothing was thrown.
        at com.atul.gitbook.learn.utils.SerializationTest.testDeserializationFailsOnMalformedInput(SerializationTest.java:32)
```

We'll now implement the serialization logic in `JacksonJsonSerializer`. We added a few additional classes to allow for Generic serialization. We can add a new type of `Serializer` by adding a new static method in `Serializers`.

### Let's try to resolve the failure

```java
public class JacksonJsonSerializer<T> implements Serializer<T> {

    private final Class<T> fClass;

    public JacksonJsonSerializer(Class<T> clazz) {
        fClass = clazz;
    }

    @Override
    public T deserialize(String content) {
        throw SerializationException.newDeserializationException(new Exception());
    }
}
```

```java
public class SerializationException extends RuntimeException {

    private SerializationException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }

    static SerializationException newDeserializationException(
            @Nullable final Throwable cause) {
        return new SerializationException("Failed to deserialize object.", cause);
    }
}
```

### Try to run the test

The tests should be passing now.

## Write a new test

```java
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
```

### Try to run the test

```java
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[53,30] cannot find symbol
  symbol:   method copy(T)
  location: variable serializer of type com.atul.gitbook.learn.jackson.Serializer<T>
[ERROR] /Users/creations/IdeaProjects/rest-using-spring-boot/user-service/v12/src/test/java/com/atul/gitbook/learn/utils/SerializationTest.java:[58,41] cannot find symbol
  symbol:   method serializePretty(T)
  location: variable serializer of type com.atul.gitbook.learn.jackson.Serializer<T>
```

### Let's try to resolve the failure

```java
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
```

```java
public class JacksonJsonSerializer<T> implements Serializer<T> {

    private final Class<T> fClass;

    public JacksonJsonSerializer(Class<T> clazz) {
        fClass = clazz;
    }

    @Override
    public T deserialize(String content) {
        throw SerializationException.newDeserializationException(new Exception());
    }

    @Override
    public String serialize(T object) {
        return null;
    }

    @Override
    public String serializePretty(T object) {
        return null;
    }
}
```

### Try to run the test

```java
[ERROR] testSerializer{Serializer, Object}[1]  Time elapsed: 0.049 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <com.atul.gitbook.learn.utils.SerializationTest$DummyModel@c03cf28> but was: <null>
        at com.atul.gitbook.learn.utils.SerializationTest.testSerializer(SerializationTest.java:56)
```

###  Let's try to resolve the failure

`Jackson` exposes a few databind classes which are used for serialization/deserialization, `ObjectMapper`, `ObjectReader`, `ObjectWriter`. Our abstraction will be using it internally to expose the generic methods.

Let's configure the `JacksonJsonSerializer` first. As we're using Instant for dates, we must register an `InstantDeserializer` to do the job for JacksonJsonSerializer.

```java
private final Class<T> fClass;
private final ObjectMapper fMapper;
private final ObjectReader fObjectReader;
private final ObjectWriter fObjectWriter;

public JacksonJsonSerializer(Class<T> clazz) {
    fClass = clazz;
    fMapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule().addDeserializer(Instant.class,
                    new InstantDeserializer()))
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    fObjectReader = fMapper.readerFor(fClass);
    fObjectWriter = fMapper.writerFor(fClass);
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
```

```java
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
```

And to actually perform the serialization and deserialization, we'd need to update the inherited methods in JacksonJsonSerializer.

```java
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
    } catch (JsonProcessingException ex) {
        throw SerializationException.newSerializationException(ex);
    }
}
```

We've also added a new `SerializationException`, let's modify the exception class.

```java
public class SerializationException extends RuntimeException {

...
    static SerializationException newSerializationException(
            @Nullable final Throwable cause) {
        return new SerializationException("Failed to serialize object.", cause);
    }

...

}
```

### Try to run the test

The tests should be passing now.

## Project Status

We now have exposed a method in the Serializers interface which will take a class name and return a serializer object for that class with methods to convert to and from object and string. 

We now have exposed a method in the Serializers interface which will take a class name and return a serializer object for that class with methods to convert to and from object and string.

One thing to note is that models should have a default constructor when being deserialized using Jackson.

Let's integrate that with our user-service in the next chapter.

