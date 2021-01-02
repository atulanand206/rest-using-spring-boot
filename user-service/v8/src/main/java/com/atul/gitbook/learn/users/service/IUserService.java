package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

import java.util.UUID;

public interface IUserService {

    /**
     * Creates and returns a new user.
     *
     * @param requesterId id of the user making the request
     * @param userDto contains the necessary information for the User.
     * @return the created user
     */
    User createUser(UUID requesterId, UserDto userDto);

    /**
     * Returns the user with the provided userId.
     *
     * @param id the userId of the user being queried.
     */
    User getUser(UUID id);

    /**
     * Updates the user.
     *
     * @param id userId of the user being queried.
     * @param userDto contains the new information for the User.
     */
    void updateUser(UUID id, UserDto userDto);

    /**
     * Deletes the user.
     *
     * @param id userId of the user to be deleted.
     */
    void deleteUser(UUID id);
}
