package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String REQUEST_HEADER_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@Valid @RequestBody NewItemDto itemDto,
                       @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long ownerId) {
        log.info("Post /items - запрос на добавление вещи ownerId={}, itemDto={}", ownerId, itemDto);
        ItemDto dto = itemService.addItem(ownerId, itemDto);
        log.info("Post /items - вещь добавлена dto={}", dto);
        return dto;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @Valid @RequestBody NewCommentDto dto) {
        log.info("Post /items/{itemId}/comment - запрос на добавление комментария " +
                        "userId={}, itemId={}, textComment={}",
                userId, itemId, dto.getText());
        CommentDto commentDto = itemService.addComment(userId, itemId, dto);
        log.info("Post /items/{itemId}/comment - комментарий успешно добавлен");
        return commentDto;
    }


    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long ownerId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Patch /items/{itemId} - запрос на обновление вещи itemId={}, itemDto={}", itemId, itemDto);
        ItemDto dto = itemService.updateItem(ownerId, itemId, itemDto);
        log.info("Patch /items/{itemId} - вещь обновлена dto={}", dto);
        return dto;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Object get(@PathVariable("itemId") Long itemId,
                      @RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        log.info("Get /items/{itemId} - запрос на получение вещи по id={}", itemId);
        Item item = itemService.getItemEntity(itemId);

        if (item.getOwnerId().equals(userId)) {
            /*
             *Владелец получает расширенный DTO с lastBooking/nextBooking
             */
            ItemWithBookingDto dto = itemService.getItemByOwnerForUser(userId, itemId);
            log.info("Get /items/{} - вещь получена владельцем", itemId);
            return dto;
        } else {
            /*
             *Обычный пользователь получает базовый DTO с комментариями (без lastBooking/nextBooking)
             */
            ItemWithBookingDto dto = itemService.getItemFromUser(itemId, userId);
            log.info("Get /items/{} - вещь получена пользователем", itemId);
            return dto;
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemWithBookingDto> getAll(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long ownerId) {
        log.info("Get /items - запрос на получение всех вещей владельца с id={}", ownerId);
        Collection<ItemWithBookingDto> itemDtos = itemService.getAllItemsWithBookings(ownerId);
        log.info("Get /items - вещи владельца получены itemDtosSize={}", itemDtos.size());
        return itemDtos;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getBySearch(@RequestParam String text) {
        log.info("Get /items/search?text={}", text);
        Collection<ItemDto> itemDtos = itemService.getAllItemsByText(text);
        log.info("Get /items/search?text={} return itemDtos.size()={}", text, itemDtos.size());
        return itemDtos;
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long ownerId,
                       @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(ownerId, itemId);
    }
}
