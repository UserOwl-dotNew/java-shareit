package ru.practicum.shareit.gateway.controllers.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.dto.item.NewItemDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.gateway.constants.Headers.REQUEST_HEADER_SHARER_USER_ID;

@WebMvcTest(ItemController.class)
public class ItemControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BaseClient baseClient;

    private NewItemDto newItemDto;

    @BeforeEach
    void setUp() {
        newItemDto = new NewItemDto();
        newItemDto.setName("Test Item");
        newItemDto.setDescription("Test Description");
        newItemDto.setAvailable(true);
    }

    @Test
    void addItem_WithEmptyTitle_ShouldReturnBadRequest() throws Exception {
        newItemDto.setName("");

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_WithEmptyDescription_ShouldReturnBadRequest() throws Exception {
        newItemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addItem_WithNullAvailable_ShouldReturnBadRequest() throws Exception {
        newItemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());
    }
}