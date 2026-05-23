package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto dto) {
        userExist(ownerId);
        Item item = itemMapper.toItem(dto);
        return itemMapper.toItemDto(itemRepository.add(ownerId, item));
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto dto) {
        userExist(ownerId);
        return itemMapper.toItemDto(itemRepository.update(ownerId, itemId, dto));
    }

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(itemRepository.get(id));
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        itemRepository.delete(ownerId, itemId);
    }

    @Override
    public List<ItemDto> getAllItemsFromOwner(Long ownerId) {
        return itemRepository.getByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        log.info("Start ItemServiceImpl.getAllItemsByText(text), text={}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<ItemDto> itemDtos = itemRepository.getByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("ItemServiceImpl.getAllItemsByText(text) complete itemDtosSize={}", itemDtos.size());
        return itemDtos;
    }

    @Override
    public void clear() {
        itemRepository.clear();
    }

    private void userExist(Long id) {
        userRepository.findById(id);
    }
}
