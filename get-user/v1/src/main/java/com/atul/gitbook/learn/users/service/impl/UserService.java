package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class UserService implements IUserService {

    private List<User> users = new ArrayList<>();

    @Override
    public User createUser(UserDto userDto) {
        validateNotNull(userDto);
        final var user = User.with(userDto, UUID.randomUUID());
        users.add(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        validateNotNull(id);
        for (var user : users) {
            if (id.equals(user.getId()))
                return user;
        }
        throw new NoSuchElementException();
    }
}
