package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class InMemoryRepository implements IUserRepository {

    public static User ADMINISTRATOR = new User(UUID.fromString("f994c61d-ebd1-463c-a8d8-ebe5989aa501"), "King Kong", "9999999999", "king@kong.com", true);
    public static User USER = new User(UUID.fromString("1109a8c8-49a3-4921-aa80-65e730d587fe"), "David Marshal", "9999999999", "david@marshall.com", false);
    private List<User> users = new ArrayList<>();

    public InMemoryRepository() {
        users.add(ADMINISTRATOR);
        users.add(USER);
    }

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

    @Override
    public void updateUser(UUID id, UpdateUserDto userDto) {
        validateNotNull(id);
        validateNotNull(userDto);
        for (var i = 0; i < users.size(); i++) {
            final var user = users.get(i);
            if (user.getId().equals(id)) {
                users.set(i, User.with(userDto, user));
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
