package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User update(Long id, UserDto userDto);

    User delete(Long id);

    User findById(Long id);

    Optional<User> findByEmail(String email);

    void clear();
}
