package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.service.IUserService;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

public class UserService implements IUserService {

    @Override
    public User createUser(UserDto userDto) throws IllegalArgumentException {
        if (userDto == null) {
            throw new IllegalArgumentException();
        }
        return null;
    }
}
