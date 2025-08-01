package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item createItem(Item item);

    Optional<Item> findItem(Integer itemId);

    Item updateItem(Item item);

    List<Item> findItemsByOwner(Integer ownerId);

    List<Item> findAllAvailableItems();
}
