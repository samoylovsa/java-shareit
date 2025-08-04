package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    @NotBlank(message = "Name must not be empty")
    private String name;
    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email should be valid")
    private String email;
}