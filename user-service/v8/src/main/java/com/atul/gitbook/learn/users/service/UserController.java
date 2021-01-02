package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.User;
import com.atul.gitbook.learn.users.models.UserDto;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    private final IUserService fUserService;

    public UserController(IUserService userService) {
        this.fUserService = userService;
    }

    @PostMapping("/v1/{requesterId}/user")
    public User createUser(@PathVariable("requesterId") UUID requesterId,
                           @RequestBody UserDto userDto) {
        return fUserService.createUser(requesterId, userDto);
    }

    @GetMapping("/v1/{requesterId}/user/{userId}")
    public User getUser(@PathVariable("requesterId") UUID requesterId,
                        @PathVariable("userId") UUID userId) {
        return fUserService.getUser(requesterId, userId);
    }
}