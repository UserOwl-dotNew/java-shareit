package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.NewItemDto;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Answer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.AnswerRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private NewItemDto newItemDto;
    private ItemRequest itemRequest;
    private Answer answer;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        item = new Item();
        item.setId(1L);
        item.setOwnerId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setOwnerId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test Description");
        newItemDto.setAvailable(true);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setOwnerId(2L); // Владелец запроса - другой пользователь
        itemRequest.setDescription("Need item");
        itemRequest.setCreated(LocalDateTime.now());

        answer = new Answer();
        answer.setId(1L);
        answer.setRequestId(1L);
        answer.setItemId(1L);
        answer.setName("Test Item");
        answer.setOwnerId(1L);
    }

    @Test
    void addItem_WithRequestId_ShouldCreateItemAndAnswer() {
        // Arrange
        Long userId = 1L;
        Long requestId = 1L;
        newItemDto.setRequestId(requestId);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(answerRepository.existsByRequestId(requestId)).thenReturn(false);
        when(itemMapper.toItem(newItemDto)).thenReturn(item);
        when(itemRepository.save(eq(item))).thenReturn(item);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act
        ItemDto result = itemService.addItem(userId, newItemDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");

        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findById(requestId);
        verify(answerRepository, times(1)).existsByRequestId(requestId);
        verify(itemRepository, times(1)).save(eq(item));
        verify(answerRepository, times(1)).save(any(Answer.class));
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    void addItem_WithoutRequestId_ShouldCreateItemOnly() {
        // Arrange
        Long userId = 1L;
        newItemDto.setRequestId(null);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemMapper.toItem(newItemDto)).thenReturn(item);
        when(itemRepository.save(eq(item))).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act
        ItemDto result = itemService.addItem(userId, newItemDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(userRepository, times(1)).existsById(userId);
        verify(requestRepository, never()).findById(anyLong());
        verify(answerRepository, never()).existsByRequestId(anyLong());
        verify(itemRepository, times(1)).save(eq(item)); // ✅ Проверка с eq(item)
        verify(answerRepository, never()).save(any(Answer.class));
        verify(itemMapper, times(1)).toItemDto(item);
    }

    @Test
    void addItem_WithInvalidRequestId_ShouldThrowNotFoundException() {
        // Arrange
        Long userId = 1L;
        Long invalidRequestId = 999L;
        newItemDto.setRequestId(invalidRequestId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(invalidRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> itemService.addItem(userId, newItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Запрос с id=" + invalidRequestId + " не найден");

        verify(userRepository, times(1)).existsById(userId);
        verify(requestRepository, times(1)).findById(invalidRequestId);
        verify(itemRepository, never()).save(any(Item.class));
        verify(answerRepository, never()).save(any(Answer.class));
        verify(itemMapper, never()).toItemDto(any(Item.class));
    }

    @Test
    void addItem_WithOwnRequestId_ShouldThrowValidationException() {
        // Arrange
        Long userId = 1L;
        Long requestId = 1L;
        itemRequest.setOwnerId(userId); // Владелец запроса = текущий пользователь
        newItemDto.setRequestId(requestId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        // Act & Assert
        assertThatThrownBy(() -> itemService.addItem(userId, newItemDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Нельзя добавить ответ на собственный запрос");

        verify(userRepository, times(1)).existsById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(answerRepository, never()).existsByRequestId(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void addItem_WithAlreadyAnsweredRequest_ShouldThrowDuplicatedDataException() {
        // Arrange
        Long userId = 1L;
        Long requestId = 1L;
        newItemDto.setRequestId(requestId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(answerRepository.existsByRequestId(anyLong())).thenReturn(true);

        assertThatThrownBy(() -> itemService.addItem(userId, newItemDto))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessage("Ответ с этим itemId=1 вещи уже создан");

        verify(userRepository, times(1)).existsById(userId);
        verify(requestRepository, times(1)).findById(requestId);
        verify(answerRepository, times(1)).existsByRequestId(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void addItem_WithInvalidUser_ShouldThrowNotFoundException() {
        // Arrange
        Long invalidUserId = 999L;
        newItemDto.setRequestId(1L);

        when(userRepository.existsById(invalidUserId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> itemService.addItem(invalidUserId, newItemDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Пользователя с id=" + invalidUserId + " не найдено");

        verify(userRepository, times(1)).existsById(invalidUserId);
        verify(requestRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void addItem_WithNullRequestId_ShouldNotCheckRequestExistence() {
        // Arrange
        Long userId = 1L;
        newItemDto.setRequestId(null);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemMapper.toItem(newItemDto)).thenReturn(item);
        when(itemRepository.save(eq(item))).thenReturn(item); // ✅ Использование eq(item)
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        // Act
        ItemDto result = itemService.addItem(userId, newItemDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(userRepository, times(1)).existsById(userId);
        verify(requestRepository, never()).findById(anyLong());
        verify(answerRepository, never()).existsByRequestId(anyLong());
        verify(itemRepository, times(1)).save(eq(item)); // ✅ Проверка с eq(item)
        verify(answerRepository, never()).save(any(Answer.class));
    }
}