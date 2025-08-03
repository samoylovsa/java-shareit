package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer id = 0;

    @Override
    public Item createItem(Item item) {
        if (item.getId() != null) {
            throw new IllegalArgumentException("New item must not have ID");
        }
        item.setId(++id);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Optional<Item> findItem(Integer itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> findItemsByOwner(Integer ownerId) {
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findAllAvailableItems() {
        return items.values().stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
