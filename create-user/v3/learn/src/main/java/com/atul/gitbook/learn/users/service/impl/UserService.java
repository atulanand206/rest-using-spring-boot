package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserService;

import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class UserService implements IUserService {

    @Override
    public User createUser(UserDto userDto) throws IllegalArgumentException {
        validateNotNull(userDto);
        return User.with(userDto, UUID.randomUUID());
    }
}
