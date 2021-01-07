package com.atul.gitbook.learn.users.service;

import com.atul.gitbook.learn.users.models.UpdateUserDto;
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

    @PutMapping("/v1/{requesterId}/user/{userId}")
    public User updateUser(@PathVariable("requesterId") UUID requesterId,
                           @PathVariable("userId") UUID userId,
                           @RequestBody UpdateUserDto userDto) {
        return fUserService.updateUser(requesterId, userId, userDto);
    }

    @DeleteMapping("/v1/{requesterId}/user/{userId}")
    public void deleteUser(@PathVariable("requesterId") UUID requesterId,
                           @PathVariable("userId") UUID userId) {
        fUserService.deleteUser(requesterId, userId);
    }
}