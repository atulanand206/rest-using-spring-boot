package com.atul.gitbook.learn.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.*;

public class User {

    @JsonProperty("id")
    private UUID fId;

    @JsonProperty("name")
    private String fName;

    @JsonProperty("phone")
    private String fPhone;

    @JsonProperty("email")
    private String fEmail;

    @JsonProperty("administrator")
    private boolean fAdministrator;

    /**
     * @param id            the user id of the user.
     * @param name          the name of the user.
     * @param phone         the phone number of the user.
     * @param email         the email of the user.
     * @param administrator true if the user is an administrator.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public User(UUID id, String name, String phone, String email, boolean administrator) throws IllegalArgumentException {
        validateNotNull(id);
        validateNotNull(name);
        validateNotNull(phone);
        validatePhoneNumber(phone);
        validateNotNull(email);
        validateEmail(email);
        fId = id;
        fName = name;
        fPhone = phone;
        fEmail = email;
        fAdministrator = administrator;
    }

    public static User with(UserDto userDto, UUID userId) {
        return new User(userId, userDto.getName(), userDto.getPhone(), userDto.getEmail(), userDto.isAdministrator());
    }

    public static User with(UpdateUserDto userDto, User user) {
        return new User(user.getId(), userDto.getName(), userDto.getPhone(), userDto.getEmail(), user.isAdministrator());
    }

    public UUID getId() {
        return fId;
    }

    public String getName() {
        return fName;
    }

    public String getPhone() {
        return fPhone;
    }

    public String getEmail() {
        return fEmail;
    }

    public boolean isAdministrator() {
        return fAdministrator;
    }
}
