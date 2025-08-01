package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> findUser(Integer userId);

    void deleteUser(Integer userId);

    boolean isExistsByEmail(String email);
}
