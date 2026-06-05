package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorIsNotOwner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    private Long ownerId;
    private Long bookerId;
    private NewItemDto newItemDto;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        ownerId = userService.saveUser(owner).getId();

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        bookerId = userService.saveUser(booker).getId();

        newItemDto = new NewItemDto();
        newItemDto.setName("Drill");
        newItemDto.setDescription("Powerful drill");
        newItemDto.setAvailable(true);
    }

    @Test
    void addItem_shouldCreateItem() {
        ItemDto saved = itemService.addItem(ownerId, newItemDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Drill");
        assertThat(saved.getDescription()).isEqualTo("Powerful drill");
        assertThat(saved.getAvailable()).isTrue();
    }

    @Test
    void updateItem_shouldModifyFields() {
        ItemDto item = itemService.addItem(ownerId, newItemDto);
        ItemDto update = new ItemDto();
        update.setName("Hammer");
        update.setDescription("Heavy hammer");
        update.setAvailable(false);

        ItemDto updated = itemService.updateItem(ownerId, item.getId(), update);

        assertThat(updated.getName()).isEqualTo("Hammer");
        assertThat(updated.getDescription()).isEqualTo("Heavy hammer");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateItem_shouldThrowError_whenNotOwner() {
        ItemDto item = itemService.addItem(ownerId, newItemDto);
        Long otherUser = userService.saveUser(createUser("other@example.com")).getId();

        assertThatThrownBy(() -> itemService.updateItem(otherUser, item.getId(), new ItemDto()))
                .isInstanceOf(ErrorIsNotOwner.class);
    }

    @Test
    void getItemByOwnerForUser_shouldReturnWithBookings() throws InterruptedException {
        ItemDto item = itemService.addItem(ownerId, newItemDto);

        // Создаём прошлое бронирование
        Booking pastBooking = createBooking(bookerId, item.getId(),
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        pastBooking.setStatus(ru.practicum.shareit.booking.enums.BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Thread.sleep(100); // чтобы now точно изменился

        ItemWithBookingDto dto = itemService.getItemByOwnerForUser(ownerId, item.getId());

        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getBookerId()).isEqualTo(bookerId);
        assertThat(dto.getNextBooking()).isNull(); // будущих нет
    }

    @Test
    void addComment_shouldCreateComment_whenBookingFinished() throws Exception {
        ItemDto item = itemService.addItem(ownerId, newItemDto);

        // Создаём бронирование с end в прошлом
        Booking booking = createBooking(bookerId, item.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));

        // Подтверждаем бронирование владельцем
        bookingService.update(booking.getId(), ownerId, true);

        // Небольшая задержка, чтобы now гарантированно > end
        Thread.sleep(10);

        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Great tool!");

        CommentDto comment = itemService.addComment(bookerId, item.getId(), commentDto);

        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getText()).isEqualTo("Great tool!");
    }

    @Test
    void addComment_shouldThrowException_whenNoFinishedBooking() {
        ItemDto item = itemService.addItem(ownerId, newItemDto);
        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("Not allowed");

        assertThatThrownBy(() -> itemService.addComment(bookerId, item.getId(), commentDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Нельзя оставить комментарий");
    }

    @Test
    void getAllItemsByText_shouldReturnAvailableMatchingItems() {
        itemService.addItem(ownerId, newItemDto);
        NewItemDto unavailable = new NewItemDto();
        unavailable.setName("Drill PRO");
        unavailable.setDescription("Another drill");
        unavailable.setAvailable(false);
        itemService.addItem(ownerId, unavailable);

        List<ItemDto> found = itemService.getAllItemsByText("drill");

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Drill");
    }

    private UserDto createUser(String email) {
        UserDto dto = new UserDto();
        dto.setName("Temp");
        dto.setEmail(email);
        return dto;
    }

    private Booking createBooking(Long bookerId, Long itemId, LocalDateTime start, LocalDateTime end) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        ru.practicum.shareit.item.model.Item item = itemRepository.findById(itemId).orElseThrow();
        booking.setItem(item);
        ru.practicum.shareit.user.model.User booker = new ru.practicum.shareit.user.model.User();
        booker.setId(bookerId);
        booking.setBooker(booker);
        booking.setStatus(ru.practicum.shareit.booking.enums.BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }
}