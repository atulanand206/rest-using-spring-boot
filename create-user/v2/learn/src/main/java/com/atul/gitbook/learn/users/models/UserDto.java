package com.atul.gitbook.learn.users.models;

public class UserDto {

    public UserDto(String name, String phone, String email) throws IllegalArgumentException {
        if (!phone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException();
        }
        if (!email.matches("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$")) {
            throw new IllegalArgumentException();
        }
    }
}
