package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_ShouldPersistUserInDatabase() {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserResponse response = userService.createUser(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        User savedUser = userRepository.findById(response.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void updateUser_ShouldUpdateUserInDatabase() {
        User existingUser = userRepository.save(User.builder()
                .name("Old Name")
                .email("old@example.com")
                .build());

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("New Name")
                .email("new@example.com")
                .build();

        UserResponse response = userService.updateUser(existingUser.getId(), request);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo("new@example.com");

        User updatedUser = userRepository.findById(existingUser.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("New Name");
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void getUser_ShouldReturnUserFromDatabase() {
        User savedUser = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        UserResponse response = userService.getUser(savedUser.getId());

        assertThat(response.getId()).isEqualTo(savedUser.getId());
        assertThat(response.getName()).isEqualTo("Test User");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void deleteUser_ShouldRemoveUserFromDatabase() {
        User savedUser = userRepository.save(User.builder()
                .name("To Delete")
                .email("delete@example.com")
                .build());

        userService.deleteUser(savedUser.getId());

        boolean userExists = userRepository.existsById(savedUser.getId());
        assertThat(userExists).isFalse();
    }
}
