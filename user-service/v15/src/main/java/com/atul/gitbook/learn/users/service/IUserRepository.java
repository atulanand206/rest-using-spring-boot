package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

import java.util.UUID;

public abstract class IUserRepository {

    private User fAdministrator;
    private User fUser;

    public User getDefaultAdministrator() {
        return fAdministrator;
    }

    public User getDefaultUser() {
        return fUser;
    }

    protected void setDefaultAdministrator(User administrator) {
        this.fAdministrator = administrator;
    }

    protected void setDefaultUser(User user) {
        this.fUser = user;
    }

    /**
     * Creates and returns a new user.
     *
     * @param userDto contains the necessary information for the User.
     * @return the created user
     */
    public abstract User createUser(UserDto userDto);

    /**
     * Returns the user with the provided userId.
     *
     * @param id the userId of the user being queried.
     */
    public abstract User getUser(UUID id);

    /**
     * Updates the user.
     *
     * @param id userId of the user being queried.
     * @param userDto contains the new information for the User.
     */
    public abstract void updateUser(UUID id, UpdateUserDto userDto);

    /**
     * Deletes the user.
     *
     * @param id userId of the user to be deleted.
     */
    public abstract void deleteUser(UUID id);
}
