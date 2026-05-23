package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ErrorIsNotOwner;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {

    @Autowired
    private final ItemRepository itemRepository;
    private final ItemController itemController;
    private final UserRepository userRepository;
    private final UserController userController;

    @BeforeEach
    void setUp() {
        userRepository.clear();
        itemRepository.clear();
    }

    /*
     * Тесты для User
     */

    private ItemDto buildItemDto(String name, String description, boolean available) {
        ItemDto item = new ItemDto();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        return item;
    }

    private UserDto buildUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }

    @Test
    @Rollback
    void testSizeUsersListIsZero() {
        Collection<UserDto> users = userController.getAll();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testCreateUser() {
        Collection<UserDto> users = userController.getAll();
        assertThat(users.size()).isEqualTo(0);

        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto dto = userController.add(userDto);

        users = userController.getAll();
        assertThat(users.size()).isEqualTo(1);

        assertThat(dto)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "User")
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru")
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Rollback
    void testCreateUserWithFailEmail() {
        UserDto userDto = buildUserDto("User", "useryandex.ru");
        assertThrows(ValidationException.class, () -> {
            userController.add(userDto);
        });
    }

    @Test
    @Rollback
    void testCreateUserWithoutEmail() {
        UserDto userDto = buildUserDto("User", "");
        assertThrows(ValidationException.class, () -> {
            userController.add(userDto);
        });
    }

    @Test
    @Rollback
    void testCreateUserWithExistEmail() {
        UserDto userDto = buildUserDto("User", "user1@yandex.ru");
        UserDto dto = userController.add(userDto);

        UserDto userDto1 = buildUserDto("User1", "user1@yandex.ru");

        assertThrows(DuplicatedDataException.class, () -> {
            UserDto dto1 = userController.add(userDto1);
        });
    }

    @Test
    @Rollback
    void testCreateUserWithoutName() {
        UserDto userDto = buildUserDto("", "useryandex.ru");
        assertThrows(ValidationException.class, () -> {
            userController.add(userDto);
        });
    }

    @Test
    @Rollback
    void testUpdateUser() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto newUser = userController.add(userDto);
        assertThat(newUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "User")
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru")
                .hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);

        UserDto userForUpdate = buildUserDto("New Name", "New@mail.ru");
        UserDto updateUser = userController.update(1L, userForUpdate);

        assertThat(updateUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "New Name")
                .hasFieldOrPropertyWithValue("email", "New@mail.ru")
                .hasFieldOrPropertyWithValue("id", newUser.getId());
    }

    @Test
    @Rollback
    void testGetUserById() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);

        UserDto getUserById = userController.getById(1L);
        assertThat(addUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "User")
                .hasFieldOrPropertyWithValue("email", "user@yandex.ru");
    }

    @Test
    @Rollback
    void testGetUserByFailId() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);

        assertThrows(NotFoundException.class, () -> {
            userController.getById(999L);
        });
    }

    @Test
    @Rollback
    void testDeleteUser() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);

        userController.delete(1L);
        assertThat(userController.getAll().size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testDeleteUserByUnknownId() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);

        assertThrows(NotFoundException.class, () -> {
            userController.delete(999L);
        });
    }

    /*
     * Тесты для Item
     */

    @Test
    @Rollback
    void testGetEmptyListFromItems() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();
    }

    @Test
    @Rollback
    void testAddItem() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item", "Item description", true);
        itemController.add(itemDto, addUser.getId());

        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testAddItemWithoutAvailable() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item name");
        itemDto.setDescription("Item description");

        assertThrows(ValidationException.class, () -> {
            itemController.add(itemDto, addUser.getId());
        });
    }

    @Test
    @Rollback
    void testAddItemWithoutName() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("Item description");
        itemDto.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemController.add(itemDto, addUser.getId());
        });
    }

    @Test
    @Rollback
    void testAddItemWithoutDescription() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item name");
        itemDto.setAvailable(true);

        assertThrows(ValidationException.class, () -> {
            itemController.add(itemDto, addUser.getId());
        });
    }

    @Test
    @Rollback
    void testUpdateItem() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        ItemDto itemForUpdate = new ItemDto();
        itemForUpdate.setAvailable(false);
        itemController.update(addUser.getId(), addItem.getId(), itemForUpdate);
        ItemDto updateItem = itemController.get(addItem.getId());
        assertThat(updateItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("available", false);
    }

    @Test
    @Rollback
    void testUpdateItemNotOwner() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        UserDto userDto1 = buildUserDto("User1", "user1@yandex.ru");
        UserDto addUser1 = userController.add(userDto1);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        ItemDto itemForUpdate = new ItemDto();
        itemForUpdate.setAvailable(false);

        assertThrows(ErrorIsNotOwner.class, () -> {
            itemController.update(addUser1.getId(), addItem.getId(), itemForUpdate);
        });
    }

    @Test
    @Rollback
    void testFindItemByTextInName() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("name");
        assertThat(findItems.size()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testFindItemByTextInDescription() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("scri");
        assertThat(findItems.size()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testFindItemByTextInUpperCase() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("sCRi");
        assertThat(findItems.size()).isEqualTo(1);
    }

    @Test
    @Rollback
    void testFindItemByNotExistText() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", true);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("sdfllksdf");
        assertThat(findItems.size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testFindItemByTextWithFalseAvailable() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", false);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("name");
        assertThat(findItems.size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testFindItemByEmptyText() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", false);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        Collection<ItemDto> findItems = itemController.getBySearch("");
        assertThat(findItems.size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testDeleteItem() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        ItemDto itemDto = buildItemDto("Item name", "Item description", false);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        itemController.delete(addUser.getId(), addItem.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(0);
    }

    @Test
    @Rollback
    void testDeleteItemNotOwner() {
        UserDto userDto = buildUserDto("User", "user@yandex.ru");
        UserDto addUser = userController.add(userDto);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        assertThat(userController.getAll().size()).isEqualTo(1);
        Collection<ItemDto> itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos).isEmpty();

        UserDto userDto1 = buildUserDto("User1", "user1@yandex.ru");
        UserDto addUser1 = userController.add(userDto1);
        assertThat(addUser)
                .isNotNull().hasFieldOrPropertyWithValue("id", 1L);

        ItemDto itemDto = buildItemDto("Item name", "Item description", false);
        ItemDto addItem = itemController.add(itemDto, addUser.getId());
        itemDtos = itemController.getAll(addUser.getId());
        assertThat(itemDtos.size()).isEqualTo(1);

        assertThrows(ErrorIsNotOwner.class, () -> {
            itemController.delete(addUser1.getId(), addItem.getId());
        });
    }
}
