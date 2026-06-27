package ru.practicum.shareit.user.service;

import ru.practicum.shareit.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(Long id, UserDto newUserDto);

    void deleteUser(Long id);

    UserDto findById(Long id);
}
