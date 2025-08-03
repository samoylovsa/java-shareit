package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateItemRequest {
    @NotBlank(message = "Name must not be empty")
    private String name;
    @NotBlank(message = "Description must not be empty")
    private String description;
    @NotNull(message = "Available must not be null")
    private Boolean available;
}
