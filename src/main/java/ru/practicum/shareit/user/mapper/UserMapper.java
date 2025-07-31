package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User mapToUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
