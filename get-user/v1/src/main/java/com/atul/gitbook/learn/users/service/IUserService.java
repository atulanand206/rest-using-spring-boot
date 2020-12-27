package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

import java.util.UUID;

public interface IUserService {

    /**
     * Creates and returns a new user.
     *
     * @param userDto contains the necessary information for the User.
     * @return the created user
     */
    User createUser(UserDto userDto);

    /**
     * Returns the user with the provided userId.
     *
     * @param id the userId of the user being queried.
     */
    User getUser(UUID id);
}
