package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.*;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Integer userId, UpdateUserRequest request);

    UserResponse getUser(Integer userId);

    void deleteUser(Integer userId);
}
