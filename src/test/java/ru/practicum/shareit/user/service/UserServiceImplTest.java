package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setName("John Doe");
        testUserDto.setEmail("john@example.com");
    }

    @Test
    void saveUser_shouldSaveAndReturnUser() {
        UserDto saved = userService.saveUser(testUserDto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("John Doe");
        assertThat(saved.getEmail()).isEqualTo("john@example.com");

        User persisted = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(persisted.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void saveUser_shouldThrowException_whenEmailAlreadyExists() {
        userService.saveUser(testUserDto);
        UserDto duplicate = new UserDto();
        duplicate.setName("Another");
        duplicate.setEmail("john@example.com");

        assertThatThrownBy(() -> userService.saveUser(duplicate))
                .isInstanceOf(DuplicatedDataException.class)
                .hasMessageContaining("уже используется");
    }

    @Test
    void updateUser_shouldUpdateFields() {
        UserDto saved = userService.saveUser(testUserDto);
        UserDto update = new UserDto();
        update.setName("Updated Name");
        update.setEmail("newemail@example.com");

        UserDto updated = userService.updateUser(saved.getId(), update);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void updateUser_shouldThrowNotFound_whenUserDoesNotExist() {
        UserDto update = new UserDto();
        update.setName("Nobody");
        update.setEmail("no@one.com");

        assertThatThrownBy(() -> userService.updateUser(999L, update))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldReturnUser() {
        UserDto saved = userService.saveUser(testUserDto);
        UserDto found = userService.findById(saved.getId());

        assertThat(found).isEqualTo(saved);
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        UserDto saved = userService.saveUser(testUserDto);
        userService.deleteUser(saved.getId());

        assertThat(userRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void getAllUsers_shouldReturnAll() {
        userService.saveUser(testUserDto);
        UserDto second = new UserDto();
        second.setName("Jane");
        second.setEmail("jane@example.com");
        userService.saveUser(second);

        List<UserDto> users = userService.getAllUsers();
        assertThat(users).hasSize(2);
    }
}