// Файл: src/test/java/ru/practicum/shareit/item/ItemControllerIntegrationTest.java

package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.SqlConstants.REQUEST_HEADER_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private NewItemDto newItemDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test Description");
        newItemDto.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setOwnerId(1L);
    }

    @Test
    void addItem_WithRequestId_ShouldCreateAnswer() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        newItemDto.setRequestId(requestId);

        when(itemService.addItem(eq(userId), any(NewItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addItem_WithoutRequestId_ShouldCreateItemWithoutAnswer() throws Exception {
        Long userId = 1L;
        newItemDto.setRequestId(null);

        when(itemService.addItem(eq(userId), any(NewItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));

        verify(itemService, times(1)).addItem(eq(userId), any(NewItemDto.class));
    }

    @Test
    void addItem_WithAlreadyAnsweredRequest_ShouldThrowDuplicatedDataException() throws Exception {
        Long userId = 1L;
        newItemDto.setRequestId(1L);

        // НАСТРАИВАЕМ MOCK НА ВЫБРОС ИСКЛЮЧЕНИЯ
        when(itemService.addItem(eq(userId), any(NewItemDto.class)))
                .thenThrow(new DuplicatedDataException("На запрос id=1 уже был дан ответ"));

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isConflict())  // 409
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("На запрос id=1 уже был дан ответ"));
    }

    @Test
    void addItem_WithInvalidRequestId_ShouldThrowNotFoundException() throws Exception {
        Long userId = 1L;
        newItemDto.setRequestId(999L);

        // НАСТРАИВАЕМ MOCK НА ВЫБРОС ИСКЛЮЧЕНИЯ
        when(itemService.addItem(eq(userId), any(NewItemDto.class)))
                .thenThrow(new NotFoundException("Запрос с id=999 не найден"));

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isNotFound())  // 404
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Запрос с id=999 не найден"));
    }

    @Test
    void addItem_WithInvalidUser_ShouldThrowNotFoundException() throws Exception {
        Long userId = 999L;
        newItemDto.setRequestId(1L);

        // НАСТРАИВАЕМ MOCK НА ВЫБРОС ИСКЛЮЧЕНИЯ
        when(itemService.addItem(eq(userId), any(NewItemDto.class)))
                .thenThrow(new NotFoundException("Пользователь с id=999 не найден"));

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isNotFound())  // 404
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Пользователь с id=999 не найден"));
    }

    @Test
    void addItem_WithOwnRequestId_ShouldThrowValidationException() throws Exception {
        Long userId = 1L;
        newItemDto.setRequestId(1L);

        // НАСТРАИВАЕМ MOCK НА ВЫБРОС ИСКЛЮЧЕНИЯ
        when(itemService.addItem(eq(userId), any(NewItemDto.class)))
                .thenThrow(new ValidationException("Нельзя добавить вещь в ответ на свой собственный запрос"));

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Нельзя добавить вещь в ответ на свой собственный запрос"));
    }
}