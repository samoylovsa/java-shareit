package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateItemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
}
