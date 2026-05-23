package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Start UserServiceImpl.saveUser() userDto={}", userDto);
        emailExist(userDto);
        User savedUser = userRepository.save(userMapper.toUser(userDto));
        log.info("UserServiceImpl.saveUser() savedUser={}", savedUser);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto newUserDto) {
        log.info("Start UserServiceImpl.updateUser Long id={}, newUserDto={}", id, newUserDto);
        if (newUserDto.getEmail() != null) {
            emailExist(newUserDto);
        }
        User updateUser = userRepository.update(id, newUserDto);
        log.info("UserServiceImpl.updateUser Long id={}, updateUser={}", id, updateUser);
        return userMapper.toUserDto(updateUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    @Override
    public UserDto findById(Long id) {
        return userMapper.toUserDto(userRepository.findById(id));
    }

    @Override
    public void clear() {
        userRepository.clear();
    }

    private void emailExist(UserDto dto) {
        log.info("Start UserServiceImpl.emailExist() dto={}", dto);
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        log.info("UserServiceImpl.emailExist() email={} is not use", dto.getEmail());
    }
}
