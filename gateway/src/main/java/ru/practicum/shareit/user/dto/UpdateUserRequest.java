package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private String name;
    @Email(message = "Email should be valid")
    private String email;
}
