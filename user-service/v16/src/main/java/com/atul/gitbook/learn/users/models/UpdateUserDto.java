package com.atul.gitbook.learn.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.atul.gitbook.learn.Preconditions.*;

public class UpdateUserDto {

    @JsonProperty("name")
    private String fName;

    @JsonProperty("phone")
    private String fPhone;

    @JsonProperty("email")
    private String fEmail;

    public UpdateUserDto() {
    }

    /**
     * @param name  the name of the user.
     * @param phone the phone number of the user.
     * @param email the email of the user.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public UpdateUserDto(String name, String phone, String email) throws IllegalArgumentException {
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

    @Override
    public String toString() {
        return "{\n"
                + "\"name\" : \"" + fName + "\",\n"
                + "\"phone\" : \"" + fPhone + "\",\n"
                + "\"email\" : \"" + fEmail + "\"\n"
                + "}";
    }
}
