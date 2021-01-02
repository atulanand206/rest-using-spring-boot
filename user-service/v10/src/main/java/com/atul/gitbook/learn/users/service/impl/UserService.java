package com.atul.gitbook.learn.users.service.impl;

import com.atul.gitbook.learn.exceptions.ForbiddenException;
import com.atul.gitbook.learn.exceptions.UnauthorizedException;
import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import com.atul.gitbook.learn.users.service.IUserRepository;
import com.atul.gitbook.learn.users.service.IUserService;

import java.util.NoSuchElementException;
import java.util.UUID;

import static com.atul.gitbook.learn.Preconditions.validateNotNull;

public class UserService implements IUserService {

    private final IUserRepository fUserRepository;

    public UserService(IUserRepository fUserRepository) {
        this.fUserRepository = fUserRepository;
    }

    @Override
    public User createUser(UUID requesterId, UserDto userDto) {
        validateNotNull(requesterId);
        validateNotNull(userDto);
        try {
            final var user = fUserRepository.getUser(requesterId);
            if (!user.isAdministrator())
                throw new ForbiddenException("Requester is not an administrator and cannot request user creation.");
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("Requester is not present.");
        }
        return fUserRepository.createUser(userDto);
    }

    @Override
    public User getUser(UUID requesterId, UUID userId) {
        validateNotNull(requesterId);
        validateNotNull(userId);
        User requester;
        try {
            requester = fUserRepository.getUser(requesterId);
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("Requester is not present.");
        }
        if (requester.isAdministrator()) {
            return fUserRepository.getUser(userId);
        }
        if (!requesterId.equals(userId)) {
            throw new ForbiddenException("User can only request own profile details.");
        }
        return fUserRepository.getUser(userId);
    }

    @Override
    public User updateUser(UUID requesterId, UUID id, UpdateUserDto userDto) {
        validateNotNull(requesterId);
        validateNotNull(id);
        validateNotNull(userDto);
        try {
            fUserRepository.getUser(requesterId);
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("Requester is not present.");
        }
        if (!requesterId.equals(id)) {
            throw new ForbiddenException("User can only update own profile details.");
        }
        fUserRepository.updateUser(id, userDto);
        return fUserRepository.getUser(id);
    }

    @Override
    public void deleteUser(UUID requesterId, UUID id) {
        validateNotNull(requesterId);
        validateNotNull(id);
        try {
            fUserRepository.getUser(requesterId);
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException("Requester is not present.");
        }
        if (!requesterId.equals(id)) {
            throw new ForbiddenException("User can only delete own profile details.");
        }
        fUserRepository.deleteUser(id);
    }
}
