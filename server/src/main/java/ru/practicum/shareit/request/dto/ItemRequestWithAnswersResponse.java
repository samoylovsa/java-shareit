package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemAnswer;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestWithAnswersResponse {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemAnswer> items;
}