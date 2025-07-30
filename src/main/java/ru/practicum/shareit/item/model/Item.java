package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    Integer id;
    String name;
    String description;
    Boolean available;
    User owner;
    Integer requestId;
}