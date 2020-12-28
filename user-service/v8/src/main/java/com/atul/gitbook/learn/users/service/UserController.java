package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController {

    private final IUserService fUserService;

    public UserController(IUserService userService) {
        this.fUserService = userService;
    }

    @GetMapping("/v1/{requesterId}/users/{userId}")
    public User getUser(@PathVariable("requesterId") UUID requesterId,
                        @PathVariable("userId") UUID userId) {
        return fUserService.getUser(requesterId, userId);
    }
}
