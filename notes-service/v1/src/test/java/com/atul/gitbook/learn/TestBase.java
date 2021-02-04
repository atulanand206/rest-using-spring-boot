package com.atul.gitbook.learn;

import com.atul.gitbook.learn.jackson.Serializer;
import com.atul.gitbook.learn.jackson.Serializers;
import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ActiveProfiles("test")
@EnableAutoConfiguration
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = CommandLineRunner.class))
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true",
        classes = {AppConfig.class, TestContainerConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase {

    @Autowired
    protected IUserRepository fUserRepository;

    @Autowired
    private WebApplicationContext fAppContext;

    protected MockMvc fMockMvc;

    @BeforeAll
    void setUp() {
        fMockMvc = MockMvcBuilders.webAppContextSetup(fAppContext).build();
    }

    protected static final Serializer<User> USER_SERIALIZER = Serializers.newJsonSerializer(User.class);

    private static final String CREATE_USER = "/v1/%s/user";

    protected static RequestBuilder createUserRequest(@Nullable UUID requesterId, @Nullable UserDto userDto) {
        final var requestBuilder = MockMvcRequestBuilders
                .post(String.format(CREATE_USER, requesterId))
                .contentType(APPLICATION_JSON);
        if (userDto != null)
            requestBuilder.content(userDto.toString());
        return requestBuilder;
    }

    private static final String GET_USER = "/v1/%s/user/%s";

    protected static RequestBuilder getUserRequest(@Nullable UUID requesterId, @Nullable UUID userId) {
        return MockMvcRequestBuilders
                .get(String.format(GET_USER, requesterId, userId))
                .contentType(APPLICATION_JSON);
    }

    private static final String UPDATE_USER = "/v1/%s/user/%s";

    protected static RequestBuilder updateUserRequest(@Nullable UUID requesterId, @Nullable UUID userId, @Nullable UpdateUserDto updateUserDto) {
        final var requestBuilder = MockMvcRequestBuilders
                .put(String.format(UPDATE_USER, requesterId, userId))
                .contentType(APPLICATION_JSON);
        if (updateUserDto != null)
            requestBuilder.content(updateUserDto.toString());
        return requestBuilder;
    }

    private static final String DELETE_USER = "/v1/%s/user/%s";

    protected static RequestBuilder deleteUserRequest(@Nullable UUID requesterId, @Nullable UUID userId) {
        return MockMvcRequestBuilders
                .delete(String.format(DELETE_USER, requesterId, userId))
                .contentType(APPLICATION_JSON);
    }
}
