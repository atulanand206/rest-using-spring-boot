package com.atul.gitbook.learn.users.models;

import static com.atul.gitbook.learn.Preconditions.validateEmail;
import static com.atul.gitbook.learn.Preconditions.validateNotNull;
import static com.atul.gitbook.learn.Preconditions.validatePhoneNumber;

public class UserDto {

    private final String fName;
    private final String fPhone;
    private final String fEmail;

    /**
     * @param name  the name of the user.
     * @param phone the phone number of the user.
     * @param email the email of the user.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public UserDto(String name, String phone, String email) throws IllegalArgumentException {
        validateNotNull(name);
        validateNotNull(phone);
        validateNotNull(email);
        validatePhoneNumber(phone);
        validateEmail(email);
        fName = name;
        fPhone = phone;
        fEmail = email;
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
