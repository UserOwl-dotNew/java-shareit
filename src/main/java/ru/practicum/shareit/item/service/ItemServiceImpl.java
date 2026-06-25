package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ErrorIsNotOwner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long ownerId, NewItemDto dto) {
        userExist(ownerId);
        Item item = itemMapper.toItem(dto);
        item.setOwnerId(ownerId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, NewCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));

        LocalDateTime now = LocalDateTime.now();
        log.info("Проверка для userId={}, now={}, item={}", userId, now, item);

        try {
            List<Booking> allApproved = bookingRepository.findByBookerIdAndItemIdAndStatus(userId, itemId);
            log.info("Найдено подходящих бронирований для комментария: {}", allApproved.size());
            log.info("Всего APPROVED бронирований для этой пары: {}", allApproved.size());
            for (Booking b : allApproved) {
                log.info("  id={}, end={}, now={} end < now = {}", b.getId(), b.getEnd(), now, b.getEnd().isBefore(now));
            }
        } catch (Exception e) {
            log.error("Ошибка при вызове репозитория", e);
            throw e;
        }

        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndDateBefore(
                userId, itemId, now);
        log.info("Найдено подходящих бронирований для комментария: {}", bookings.size());

        if (bookings.isEmpty()) {
            throw new ValidationException("Нельзя оставить комментарий: у вас нет завершенных бронирований для этой вещи");
        }

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setText(dto.getText());
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto dto) {
        userExist(ownerId);
        Item findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));

        if (!findItem.getOwnerId().equals(ownerId)) {
            throw new ErrorIsNotOwner("Пользователь с id=" + ownerId + " не является владельцем вещи " + itemId);
        }

        itemMapper.updateItemFields(dto, findItem);
        log.info("Вещь с id=" + itemId + " обновлена: " + findItem);
        return itemMapper.toItemDto(itemRepository.save(findItem));
    }

    @Override
    public Item getItemEntity(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));
    }

    @Override
    public ItemWithBookingDto getItemFromUser(Long itemId, Long userId) {
        userExist(userId);

        Item item = getItemEntity(itemId);
        ItemWithBookingDto dto = convertToItemByOwnerDto(item);
        dto.setLastBooking(null);
        dto.setNextBooking(null);

        // Добавляем комментарии (видны всем)
        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        dto.setComments(comments);

        return dto;
    }

    @Override
    public List<ItemWithBookingDto> getAllItemsWithBookings(Long ownerId) {
        userExist(ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> {
                    ItemWithBookingDto dto = convertToItemByOwnerDto(item);
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                            .stream()
                            .map(commentMapper::toCommentDto)
                            .toList();
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));

        ItemDto dto = itemMapper.toItemDto(findItem);

        List<CommentDto> comments = commentRepository.findByItemId(itemId)
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();
        dto.setComments(comments);

        return dto;
    }

    @Override
    public ItemWithBookingDto getItemByOwnerForUser(Long ownerId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));

        ItemWithBookingDto itemDto = convertToItemByOwnerDto(item);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList();
        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        userExist(ownerId);
        Item findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с id=" + itemId + " не найдено"));
        if (!findItem.getOwnerId().equals(ownerId)) {
            throw new ErrorIsNotOwner("Пользователь с id=" + ownerId + " не является владельцем вещи " + itemId);
        }
        itemRepository.delete(findItem);
        log.info("Вещь с id=" + itemId + " удалена");
    }

    @Override
    public List<ItemWithBookingDto> getAllItemsFromOwner(Long ownerId) {
        userExist(ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(this::convertToItemByOwnerDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsByText(String text) {
        log.info("Start ItemServiceImpl.getAllItemsByText(text), text={}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<ItemDto> itemDtos = itemRepository.searchAvailableByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("ItemServiceImpl.getAllItemsByText(text) complete itemDtosSize={}", itemDtos.size());
        return itemDtos;
    }

    private void userExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователя с id=" + id + " не найдено");
        }
    }

    private ItemWithBookingDto convertToItemByOwnerDto(Item item) {
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto();
        itemWithBookingDto.setId(item.getId());
        itemWithBookingDto.setName(item.getName());
        itemWithBookingDto.setDescription(item.getDescription());
        itemWithBookingDto.setAvailable(item.getAvailable());

        List<Booking> pastBookings = bookingRepository.findPastBookingsByItemId(
                item.getId(), LocalDateTime.now());

        if (!pastBookings.isEmpty()) {
            Booking lastBooking = pastBookings.getFirst();
            itemWithBookingDto.setLastBooking(new BookingShortDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            ));
        }

        List<Booking> futureBookings = bookingRepository.findFutureByItemId(
                item.getId(), LocalDateTime.now());

        if (!futureBookings.isEmpty()) {
            Booking futureBooking = futureBookings.getFirst();
            itemWithBookingDto.setNextBooking(new BookingShortDto(
                    futureBooking.getId(),
                    futureBooking.getBooker().getId(),
                    futureBooking.getStart(),
                    futureBooking.getEnd()
            ));
        }

        return itemWithBookingDto;
    }
}
