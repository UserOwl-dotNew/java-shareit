package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.enums.booking.BookingStatus;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService; // ← реальная реализация

    private User owner;
    private User booker;
    private Item item;
    private NewBookingDto validBookingDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");

        item = new Item();
        item.setId(1L);
        item.setName("Laptop");
        item.setDescription("Gaming laptop");
        item.setAvailable(true);
        item.setOwnerId(owner.getId());

        validBookingDto = new NewBookingDto();
        validBookingDto.setItemId(1L);
        validBookingDto.setStart(LocalDateTime.now().plusDays(1));
        validBookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldSaveBooking() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(1L);
        expectedDto.setStatus(BookingStatus.WAITING);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.create(validBookingDto, 2L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void create_shouldThrow_whenOwnerBooksOwnItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(validBookingDto, 1L))
                .isInstanceOf(OwnerCannotBookHisItemException.class);
    }

    @Test
    void create_shouldThrow_whenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(validBookingDto, 2L))
                .isInstanceOf(NotAvailableItemException.class);
    }

    @Test
    void create_shouldThrow_whenEndDateBeforeStart() {
        validBookingDto.setStart(LocalDateTime.now().plusDays(2));
        validBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.create(validBookingDto, 2L))
                .isInstanceOf(UncorrectedDateException.class);
    }

    @Test
    void update_shouldApproveBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(1L);
        expectedDto.setStatus(BookingStatus.APPROVED);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.update(1L, 1L, true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void update_shouldRejectBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(1L);
        expectedDto.setStatus(BookingStatus.REJECTED);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.update(1L, 1L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.update(1L, 999L, true))
                .isInstanceOf(ErrorIsNotOwner.class);
    }

    @Test
    void getById_shouldReturnForBooker() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(1L);
        when(bookingMapper.toBookingDto(any(Booking.class))).thenReturn(expectedDto);

        BookingDto result = bookingService.getById(1L, 2L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_shouldThrowForUnauthorizedUser() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getById(1L, 999L))
                .isInstanceOf(ErrorIsNotOwnerAndBooker.class);
    }
}