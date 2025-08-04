package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponse createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Received create user request: {}", request);
        UserResponse response = userService.createUser(request);
        log.debug("Returning created user: {}", response);
        return response;
    }

    @PatchMapping("/{userId}")
    public UserResponse updateUser(
            @Positive @PathVariable("userId") Integer userId,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Received update request for user ID {}: {}", userId, request);
        UserResponse response = userService.updateUser(userId, request);
        log.debug("Returning updated user: {}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(
            @Positive @PathVariable("userId") Integer userId) {
        log.info("Received get request for user ID: {}", userId);
        UserResponse response = userService.getUser(userId);
        log.debug("Returning user data: {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Positive @PathVariable("userId") Integer userId) {
        log.info("Received delete request for user ID: {}", userId);
        userService.deleteUser(userId);
        log.debug("User with ID {} deleted successfully", userId);
    }
}