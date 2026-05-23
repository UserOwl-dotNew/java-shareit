package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validators.ItemValidator;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        ItemValidator.itemValidator(itemDto);
        log.info("Post /items - запрос на добавление вещи ownerId={}, itemDto={}", ownerId, itemDto);
        ItemDto dto = itemService.addItem(ownerId, itemDto);
        log.info("Post /items - вещь добавлена dto={}", dto);
        return dto;
    }


    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable("itemId") Long itemId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Patch /items/{itemId} - запрос на обновление вещи itemId={}, itemDto={}", itemId, itemDto);
        ItemDto dto = itemService.updateItem(ownerId, itemId, itemDto);
        log.info("Patch /items/{itemId} - вещь обновлена dto={}", dto);
        return dto;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable("itemId") Long itemId) {
        log.info("Get /items/{itemId} - запрос на получение вещи по id={}", itemId);
        ItemDto dto = itemService.getItem(itemId);
        log.info("Get /items/{itemId} - вещь получена dto={}", dto);
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get /items - запрос на получение всех вещей владельца с id={}", ownerId);
        Collection<ItemDto> itemDtos = itemService.getAllItemsFromOwner(ownerId);
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
    public void delete(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable("itemId") Long itemId) {
        itemService.deleteItem(ownerId, itemId);
    }
}
