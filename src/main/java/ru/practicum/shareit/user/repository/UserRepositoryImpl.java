package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        log.info("Start UserRepositoryIml.save() user={}", user);
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public User update(Long id, UserDto userDto) {
        log.info("Start UserRepositoryImpl.update() Long id={}, userDto={}", id, userDto);
        User existingUser = findById(id);
        log.info("existingUser={}", existingUser);
        userMapper.updateUserFields(userDto, existingUser);
        log.info("UserRepositoryImpl.update() Long id={}, existingUser={}", id, existingUser);
        return existingUser;
    }

    @Override
    public User delete(Long id) {
        User deleteUser = findById(id);
        users.remove(deleteUser);
        return deleteUser;
    }

    @Override
    public User findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + id + " не найден"));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Start UserRepositoryImpl.findByEmail() email={}", email);
        Optional<User> findUser = users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
        log.info("findUserFind={}", findUser.isPresent());
        return findUser;
    }

    @Override
    public void clear() {
        users.clear();
    }

    private Long getId() {
        long lastId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++lastId;
    }
}
