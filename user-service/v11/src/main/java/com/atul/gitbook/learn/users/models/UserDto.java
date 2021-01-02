package com.atul.gitbook.learn.users.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.atul.gitbook.learn.Preconditions.*;

public class UserDto {

    @JsonProperty("name")
    private String fName;

    @JsonProperty("phone")
    private String fPhone;

    @JsonProperty("email")
    private String fEmail;

    @JsonProperty("administrator")
    private boolean fAdministrator;

    public UserDto() {
    }

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
        fAdministrator = false;
    }

    /**
     * @param name          the name of the user.
     * @param phone         the phone number of the user.
     * @param email         the email of the user.
     * @param administrator true if the user is an administrator.
     * @throws IllegalArgumentException in any of the following fails to be conformant:
     *                                  1. Phone number : Must be 10 characters in length and all numeric.
     *                                  2. Email : Must contain @ in the middle of the string.
     */
    public UserDto(String name, String phone, String email, boolean administrator) throws IllegalArgumentException {
        validateNotNull(name);
        validateNotNull(phone);
        validateNotNull(email);
        validatePhoneNumber(phone);
        validateEmail(email);
        fName = name;
        fPhone = phone;
        fEmail = email;
        fAdministrator = administrator;
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

    @Override
    public String toString() {
        return "{\n"
                + "\"name\" : \"" + fName + "\",\n"
                + "\"phone\" : \"" + fPhone + "\",\n"
                + "\"email\" : \"" + fEmail + "\",\n"
                + "\"administrator\" : " + fAdministrator + "\n"
                + "}";
    }
}
