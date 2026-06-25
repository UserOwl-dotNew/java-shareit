package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long ownerId, NewItemDto dto);

    CommentDto addComment(Long userId, Long itemId, NewCommentDto dto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto dto);

    ItemDto getItem(Long itemId);

    void deleteItem(Long ownerId, Long itemId);

    List<ItemWithBookingDto> getAllItemsFromOwner(Long ownerId);

    List<ItemWithBookingDto> getAllItemsWithBookings(Long ownerId);

    List<ItemDto> getAllItemsByText(String text);

    ItemWithBookingDto getItemByOwnerForUser(Long ownerId, Long itemId);

    Item getItemEntity(Long itemId);

    ItemWithBookingDto getItemFromUser(Long itemId, Long userId);
}
