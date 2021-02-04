package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.UpdateUserDto;
import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;

import java.util.UUID;

public abstract class IUserRepository {

    private User fAdministrator;
    private User fUser;

    /**
     * @return the default administrator account. Used for creation of new users and is used in the tests.
     */
    public User getDefaultAdministrator() {
        return fAdministrator;
    }

    /**
     * @return the default user account. Used in tests.
     */
    public User getDefaultUser() {
        return fUser;
    }

    /**
     * This must be set when a new repository is introduced, else the tests will fail.
     * @param administrator the default administrator account.
     */
    protected void setDefaultAdministrator(User administrator) {
        this.fAdministrator = administrator;
    }

    /**
     * This must be set when a new repository is introduced, else the tests will fail.
     * @param user the default user account.
     */
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
