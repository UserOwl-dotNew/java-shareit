package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.enums.booking.BookingState;
import ru.practicum.shareit.enums.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(NewBookingDto dto, Long userId) {
        log.info("Create booking for user={}, item={}", userId, dto.getItemId());
        userExist(userId);

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с таким id=" + dto.getItemId() + " не найдено"));

        if (item.getOwnerId().equals(userId)) {
            throw new OwnerCannotBookHisItemException("Владелец не может бронировать свою вещь");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableItemException("Вещь недоступна для бронирования");
        }

        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart())) {
            throw new UncorrectedDateException("Дата окончания должна быть после даты начала");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        User user = new User();
        user.setId(userId);
        booking.setBooker(user);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование с id={} добавлено", savedBooking.getId());

        return bookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, boolean approved) {
        log.info("Updating booking id={}, userId={}, approved={}", bookingId, userId, approved);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + bookingId + " не существует"));

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }

        if (!isOwner(booking.getItem(), userId)) {
            throw new ErrorIsNotOwner("Пользователь с id=" + userId + " не является владельцем вещи");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated to {}", bookingId, updatedBooking.getStatus());

        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        userExist(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирования с id=" + bookingId + " не существует"));

        if (!booking.getItem().getOwnerId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new ErrorIsNotOwnerAndBooker("Пользователь с id=" + userId + " не является участником бронирования");
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByState(String state, Long userId) {
        log.info("Getting bookings for user={} with state={}", userId, state);

        userExist(userId);
        BookingState bookingState = BookingState.valueOf(state);

        List<Booking> bookings = switchBookingStateByBooker(bookingState, userId);

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getByOwnerAndState(String state, Long userId) {
        log.info("Getting bookings for owner={} with state={}", userId, state);

        userExist(userId);
        BookingState bookingState = BookingState.valueOf(state);

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();

        List<Booking> bookings = switchBookingStateByItem(bookingState, itemIds);

        return bookings.stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> switchBookingStateByItem(BookingState bookingState, List<Long> itemIds) {
        return switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByItemIds(itemIds);
            case PAST -> bookingRepository.findPastByItemIds(itemIds);
            case FUTURE -> bookingRepository.findFutureByItemIds(itemIds);
            case WAITING -> bookingRepository.findByItemIdsAndStatus(itemIds, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemIdsAndStatus(itemIds, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByItemIds(itemIds);
        };
    }

    private List<Booking> switchBookingStateByBooker(BookingState bookingState, Long userId) {
        return switch (bookingState) {
            case CURRENT -> bookingRepository.findCurrentByBookerId(userId);
            case PAST -> bookingRepository.findPastByBookerId(userId);
            case FUTURE -> bookingRepository.findFutureByBookerId(userId);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerId(userId);
        };
    }

    private boolean isOwner(Item item, Long userId) {
//        userExist(userId);
        return item.getOwnerId().equals(userId);
    }

    private void userExist(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователя с id=" + id + " не найдено");
        }
    }
}
