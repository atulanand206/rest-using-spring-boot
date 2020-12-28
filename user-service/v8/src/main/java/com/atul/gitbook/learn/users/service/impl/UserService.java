package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.users.service.IUserService;

import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class UserService implements IUserService {

    private final IUserRepository fUserRepository;

    public UserService(IUserRepository fUserRepository) {
        this.fUserRepository = fUserRepository;
    }

    @Override
    public User createUser(UserDto userDto) {
        validateNotNull(userDto);
        return fUserRepository.createUser(userDto);
    }

    @Override
    public User getUser(UUID requesterId, UUID userId) {
        validateNotNull(requesterId);
        validateNotNull(userId);
        return fUserRepository.getUser(userId);
    }

    @Override
    public void updateUser(UUID id, UserDto userDto) {
        validateNotNull(id);
        validateNotNull(userDto);
        fUserRepository.updateUser(id, userDto);
    }

    @Override
    public void deleteUser(UUID id) {
        validateNotNull(id);
        fUserRepository.deleteUser(id);
    }
}
