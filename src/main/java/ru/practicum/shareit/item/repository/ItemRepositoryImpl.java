package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ErrorIsNotOwner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validators.ItemValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final List<Item> items = new ArrayList<>();
    private final ItemMapper itemMapper;
    private static final Long START_INDEX = 0L;

    @Override
    public Item add(Long ownerId, Item item) {
        item.setId(getId());
        item.setOwnerId(ownerId);
        ItemValidator.itemValidator(itemMapper.toItemDto(item));
        items.add(item);
        return item;
    }

    @Override
    public Item update(Long ownerId, Long id, ItemDto dto) {
        Item existingItem = get(id);
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new ErrorIsNotOwner("Редактировать item может только её владелец");
        }
        itemMapper.updateItemFields(dto, existingItem);
        return existingItem;
    }

    @Override
    public Item get(Long itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item с таким id=" + itemId + " не найден"));
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        Item existingItem = get(itemId);
        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new ErrorIsNotOwner("Удалять item может только её владелец");
        }
        items.remove(existingItem);
    }

    @Override
    public List<Item> getByOwner(Long ownerId) {
        return items.stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByText(String text) {
        log.info("Start ItemRepositoryImpl.getByText(text), text={}", text);
        String lowerText = text.toLowerCase();
        List<Item> itemList = items.stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    log.info("item={}", item);
                    if (item.getName().toLowerCase().contains(lowerText)
                            || item.getDescription().toLowerCase().contains(lowerText)) {
                        return true;
                    }
                    return false;
                })
                .toList();
        log.info("ItemRepositoryImpl.getByText(text) complete, itemListSize={}", itemList.size());
        return itemList;
    }

    @Override
    public void clear() {
        items.clear();
    }

    private Long getId() {
        long id = items.stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(START_INDEX);
        return ++id;
    }
}
