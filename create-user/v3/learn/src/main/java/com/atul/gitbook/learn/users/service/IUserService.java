package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

public interface IUserService {

    /**
     * Creates and returns a new user.
     *
     * @param userDto contains the necessary information for the User.
     * @return the created user
     * @throws IllegalArgumentException if fails validation.
     */
    User createUser(UserDto userDto) throws IllegalArgumentException;
}
