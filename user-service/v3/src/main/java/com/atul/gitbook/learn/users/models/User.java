package com.atul.gitbook.learn.users.models;

import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateEmail;
import static com.atul.gitbook.learn.Preconditions.validateNotNull;
import static com.atul.gitbook.learn.Preconditions.validatePhoneNumber;

public class User {

    private final UUID fId;
    private final String fName;
    private final String fPhone;
    private final String fEmail;

    /**
     * @param id    the user id of the user.
     * @param name  the name of the user.
     * @param phone the phone number of the user.
     * @param email the email of the user.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public User(UUID id, String name, String phone, String email) throws IllegalArgumentException {
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
    }

    public static User with(UserDto userDto, UUID userId) {
        return new User(userId, userDto.getName(), userDto.getPhone(), userDto.getEmail());
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
}
