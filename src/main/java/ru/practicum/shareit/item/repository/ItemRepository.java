package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item add(Long ownerId, Item item);

    Item update(Long ownerId, Long id, ItemDto itemDto);

    Item get(Long itemId);

    void delete(Long ownerId, Long itemId);

    List<Item> getByOwner(Long ownerId);

    List<Item> getByText(String text);

    void clear();
}
