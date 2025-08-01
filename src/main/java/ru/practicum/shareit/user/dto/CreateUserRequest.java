package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private String email;
}