package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class InMemoryRepository implements IUserRepository {

    private static final String ERROR_GET_USER = "User %s not found.";

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
        throw new NoSuchElementException(String.format(ERROR_GET_USER, id));
    }

    @Override
    public void updateUser(UUID id, UserDto userDto) {
        validateNotNull(id);
        validateNotNull(userDto);
        for (var i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.set(i, User.with(userDto, id));
                return;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void deleteUser(UUID id) {
        validateNotNull(id);
        for (var i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(id)) {
                users.remove(i);
                return;
            }
        }
        throw new NoSuchElementException();
    }
}
