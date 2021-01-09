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

    private static final String ERROR_UPDATE_OWN_PROFILE = "User can only update own profile details.";
    private static final String ERROR_DELETE_OWN_PROFILE = "User can only delete own profile details.";
    private static final String ERROR_REQUESTER_UNAVAILABLE = "Requester is not present.";
    private static final String ERROR_REQUESTER_CANT_CREATE = "Requester is not an administrator and cannot request user creation.";
    private static final String ERROR_REQUESTER_CANT_GET = "Requester can not request to get the user's profile details.";

    private final IUserRepository fUserRepository;

    public UserService(IUserRepository fUserRepository) {
        this.fUserRepository = fUserRepository;
    }

    @Override
    public User createUser(UUID requesterId, UserDto userDto) {
        validateNotNull(requesterId);
        validateNotNull(userDto);
        final var requester = getRequester(requesterId);
        validateRequesterCanCreateUser(requester);
        return fUserRepository.createUser(userDto);
    }

    @Override
    public User getUser(UUID requesterId, UUID userId) {
        validateNotNull(requesterId);
        validateNotNull(userId);
        final var requester = getRequester(requesterId);
        validateRequesterCanGetUser(requester, userId);
        return fUserRepository.getUser(userId);
    }

    @Override
    public User updateUser(UUID requesterId, UUID userId, UpdateUserDto userDto) {
        validateNotNull(requesterId);
        validateNotNull(userId);
        validateNotNull(userDto);
        getRequester(requesterId);
        validateRequesterSameAsUser(requesterId, userId, ERROR_UPDATE_OWN_PROFILE);
        fUserRepository.updateUser(userId, userDto);
        return fUserRepository.getUser(userId);
    }

    @Override
    public void deleteUser(UUID requesterId, UUID userId) {
        validateNotNull(requesterId);
        validateNotNull(userId);
        getRequester(requesterId);
        validateRequesterSameAsUser(requesterId, userId, ERROR_DELETE_OWN_PROFILE);
        fUserRepository.deleteUser(userId);
    }

    private User getRequester(UUID requesterId) {
        try {
            return fUserRepository.getUser(requesterId);
        } catch (NoSuchElementException e) {
            throw new UnauthorizedException(ERROR_REQUESTER_UNAVAILABLE);
        }
    }

    private void validateRequesterCanCreateUser(User requester) {
        if (!requester.isAdministrator())
            throw new ForbiddenException(ERROR_REQUESTER_CANT_CREATE);
    }

    private void validateRequesterCanGetUser(User requester, UUID userId) {
        if (!requester.isAdministrator() && !userId.equals(requester.getId()))
            throw new ForbiddenException(ERROR_REQUESTER_CANT_GET);
    }

    private void validateRequesterSameAsUser(UUID requesterId, UUID userId, String message) {
        if (!requesterId.equals(userId))
            throw new ForbiddenException(message);
    }
}
