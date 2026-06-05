package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;
    private NewBookingDto validBookingDto;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@book.com");
        ownerId = userService.saveUser(owner).getId();

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@book.com");
        bookerId = userService.saveUser(booker).getId();

        NewItemDto itemDto = new NewItemDto();
        itemDto.setName("Laptop");
        itemDto.setDescription("Gaming laptop");
        itemDto.setAvailable(true);
        itemId = itemService.addItem(ownerId, itemDto).getId();

        validBookingDto = new NewBookingDto();
        validBookingDto.setItemId(itemId);
        validBookingDto.setStart(LocalDateTime.now().plusDays(1));
        validBookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void create_shouldSaveBooking() {
        BookingDto booking = bookingService.create(validBookingDto, bookerId);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(booking.getBooker().getId()).isEqualTo(bookerId);
        assertThat(booking.getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void create_shouldThrow_whenOwnerBooksOwnItem() {
        assertThatThrownBy(() -> bookingService.create(validBookingDto, ownerId))
                .isInstanceOf(OwnerCannotBookHisItemException.class);
    }

    @Test
    void create_shouldThrow_whenItemNotAvailable() {
        NewItemDto unavailableItem = new NewItemDto();
        unavailableItem.setName("Broken");
        unavailableItem.setDescription("Not working");
        unavailableItem.setAvailable(false);
        Long unavailableId = itemService.addItem(ownerId, unavailableItem).getId();

        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(unavailableId);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        assertThatThrownBy(() -> bookingService.create(dto, bookerId))
                .isInstanceOf(NotAvailableItemException.class);
    }

    @Test
    void create_shouldThrow_whenEndDateBeforeStart() {
        validBookingDto.setStart(LocalDateTime.now().plusDays(2));
        validBookingDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThatThrownBy(() -> bookingService.create(validBookingDto, bookerId))
                .isInstanceOf(UncorrectedDateException.class);
    }

    @Test
    void update_shouldApproveBooking() {
        BookingDto created = bookingService.create(validBookingDto, bookerId);
        BookingDto updated = bookingService.update(created.getId(), ownerId, true);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void update_shouldRejectBooking() {
        BookingDto created = bookingService.create(validBookingDto, bookerId);
        BookingDto updated = bookingService.update(created.getId(), ownerId, false);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        BookingDto created = bookingService.create(validBookingDto, bookerId);
        Long stranger = userService.saveUser(createUser("stranger@example.com")).getId();

        assertThatThrownBy(() -> bookingService.update(created.getId(), stranger, true))
                .isInstanceOf(ErrorIsNotOwner.class);
    }

    @Test
    void getById_shouldReturnForBooker() {
        BookingDto created = bookingService.create(validBookingDto, bookerId);
        BookingDto found = bookingService.getById(created.getId(), bookerId);

        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getById_shouldThrowForUnauthorizedUser() {
        BookingDto created = bookingService.create(validBookingDto, bookerId);
        Long stranger = userService.saveUser(createUser("stranger@ex.com")).getId();

        assertThatThrownBy(() -> bookingService.getById(created.getId(), stranger))
                .isInstanceOf(ErrorIsNotOwnerAndBooker.class);
    }

    @Test
    void getByState_shouldReturnCurrentBookings() throws InterruptedException {
        // Создаём текущее бронирование
        NewBookingDto currentDto = new NewBookingDto();
        currentDto.setItemId(itemId);
        currentDto.setStart(LocalDateTime.now().minusMinutes(1));
        currentDto.setEnd(LocalDateTime.now().plusMinutes(30));
        bookingService.create(currentDto, bookerId);

        Thread.sleep(1000); // чтобы время точно попало в интервал

        List<BookingDto> current = bookingService.getByState("CURRENT", bookerId);
        assertThat(current).hasSize(1);
    }

    @Test
    void getByOwnerAndState_shouldReturnOwnerBookings() {
        bookingService.create(validBookingDto, bookerId);
        List<BookingDto> all = bookingService.getByOwnerAndState("ALL", ownerId);
        assertThat(all).hasSize(1);
    }

    private UserDto createUser(String email) {
        UserDto dto = new UserDto();
        dto.setName("Temp");
        dto.setEmail(email);
        return dto;
    }
}