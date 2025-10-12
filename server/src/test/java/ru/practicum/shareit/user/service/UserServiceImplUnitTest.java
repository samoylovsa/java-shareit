package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ShouldReturnUserResponse_WhenValidRequest() {
        CreateUserRequest request = CreateUserRequest.builder().name("John").email("john@test.com").build();
        User savedUser = User.builder().id(1).name("John").email("john@test.com").build();

        when(userRepository.existsByEmail("john@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getName()).isEqualTo("John");
        assertThat(response.getEmail()).isEqualTo("john@test.com");
        verify(userRepository).existsByEmail("john@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        CreateUserRequest request = CreateUserRequest.builder().name("John").email("existing@test.com").build();
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("The email provided is already in use.");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        Integer userId = 1;
        UpdateUserRequest request = UpdateUserRequest.builder().name("New Name").email(null).build();
        User existingUser = User.builder().id(1).name("Old Name").email("email@test.com").build();
        User updatedUser = User.builder().id(1).name("New Name").email("email@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userService.updateUser(userId, request);

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getEmail()).isEqualTo("email@test.com");
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_ShouldValidateEmail_WhenNewEmailProvided() {
        Integer userId = 1;
        UpdateUserRequest request = UpdateUserRequest.builder().name(null).email("new@test.com").build();
        User existingUser = User.builder().id(1).name("John").email("old@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userRepository).existsByEmail("new@test.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found with ID: " + userId);
    }

    @Test
    void deleteUser_ShouldFindUserBeforeDeleting_WhenUserExists() {
        Integer userId = 1;
        User existingUser = User.builder().id(1).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        InOrder inOrder = inOrder(userRepository);
        inOrder.verify(userRepository).findById(userId);    // Сначала поиск
        inOrder.verify(userRepository).deleteById(userId);  // Потом удаление
    }
}