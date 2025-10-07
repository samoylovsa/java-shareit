package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private Integer id;
    private String authorName;
    private LocalDateTime created;
    private String text;
}
