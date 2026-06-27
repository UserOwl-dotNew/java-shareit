package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    // Реальный маппер (не мок)
    private final UserMapper userMapper = new UserMapperImpl();

    private UserServiceImpl userService;

    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Создаем сервис с реальным маппером и моком репозитория
        userService = new UserServiceImpl(userRepository, userMapper);

        testUserDto = new UserDto();
        testUserDto.setName("John Doe");
        testUserDto.setEmail("john@example.com");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
    }

    @Test
    void saveUser_shouldSaveAndReturnUser() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDto saved = userService.saveUser(testUserDto);

        // Assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");

        // Проверяем только мок
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.saveUser(testUserDto))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("уже используется");

        // Проверяем только мок
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        // Arrange
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("newemail@example.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("newemail@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDto result = userService.updateUser(1L, updateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("newemail@example.com");

        // Проверяем только мок
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowNotFound_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserDto update = new UserDto();
        update.setName("Nobody");
        update.setEmail("no@one.com");

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(999L, update))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");

        // Проверяем только мок
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_shouldReturnUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Act
        UserDto found = userService.findById(1L);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("John Doe");
        assertThat(found.getEmail()).isEqualTo("john@example.com");

        // Проверяем только мок
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findById_shouldThrowNotFound_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");

        // Проверяем только мок
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_shouldThrowNotFound_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");

        // Проверяем только мок
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllUsers_shouldReturnAll() {
        // Arrange
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setName("Jane Doe");
        secondUser.setEmail("jane@example.com");

        when(userRepository.findAll()).thenReturn(List.of(testUser, secondUser));

        // Act
        List<UserDto> users = userService.getAllUsers();

        // Assert
        assertThat(users).isNotNull();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(0).getName()).isEqualTo("John Doe");
        assertThat(users.get(1).getId()).isEqualTo(2L);
        assertThat(users.get(1).getName()).isEqualTo("Jane Doe");

        // Проверяем только мок
        verify(userRepository, times(1)).findAll();
    }
}