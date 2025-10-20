package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Received create user request: {}", request);
        ResponseEntity<Object> response = userClient.createUser(request);
        log.debug("Returning created user: {}", response);
        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @Positive @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Received update request for user ID {}: {}", userId, request);
        ResponseEntity<Object> response = userClient.updateUser(userId, request);
        log.debug("Returning updated user: {}", response);
        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(
            @Positive @PathVariable("userId") Long userId) {
        log.info("Received get request for user ID: {}", userId);
        ResponseEntity<Object> response = userClient.getUser(userId);
        log.debug("Returning user data: {}", response);
        return response;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteUser(
            @Positive @PathVariable("userId") Long userId) {
        log.info("Received delete request for user ID: {}", userId);
        ResponseEntity<Object> response = userClient.deleteUser(userId);
        log.debug("User with ID {} deleted successfully", userId);
        return response;
    }
}
