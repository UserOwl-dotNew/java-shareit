package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long ownerId, ItemDto dto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto dto);

    ItemDto getItem(Long itemId);

    void deleteItem(Long ownerId, Long itemId);

    List<ItemDto> getAllItemsFromOwner(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);

    void clear();
}
