package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserDto saveUser(UserDto userDto) {
        log.info("Start UserServiceImpl.saveUser() userDto={}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicatedDataException("Данный email=" + userDto.getEmail() + " уже используется");
        }
        User savedUser = userRepository.save(userMapper.toUser(userDto));
        log.info("UserServiceImpl.saveUser() savedUser={}", savedUser);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserDto updateUser(Long id, UserDto newUserDto) {
        log.info("Start UserServiceImpl.updateUser Long id={}, newUserDto={}", id, newUserDto);
        if (userRepository.existsByEmail(newUserDto.getEmail())) {
            throw new DuplicatedDataException("Данный email=" + newUserDto.getEmail() + " уже используется");
        }

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        userMapper.updateUserFields(newUserDto, findUser);

        log.info("Complete UserServiceImpl.updateUser Long id={}, updateUser={}", id, findUser);
        return userMapper.toUserDto(userRepository.save(findUser));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь с id=" + id);
    }

    @Override
    public UserDto findById(Long id) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return userMapper.toUserDto(findUser);
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
