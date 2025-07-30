package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        validateEmailAlreadyExists(request.getEmail());
        User user = UserMapper.mapToUser(request);
        user = userRepository.createUser(user);

        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUser(Integer userId, UpdateUserRequest request) {
        User existingUser = findUser(userId);

        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            validateEmailAlreadyExists(request.getEmail());
        }

        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }

        User savedUser = userRepository.updateUser(existingUser);

        return UserMapper.mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUser(Integer userId) {
        User user = findUser(userId);
        return UserMapper.mapToUserResponse(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }

    private void validateEmailAlreadyExists(String email) {
        if (userRepository.isExistsByEmail(email)) {
            throw new AlreadyExistsException("The email provided is already in use.");
        }
    }

    private User findUser(Integer userId) {
        return userRepository.findUser(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }
}
