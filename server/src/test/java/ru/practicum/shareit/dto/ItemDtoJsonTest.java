package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.NewItemDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemDto_shouldMatchJson() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Test Item");
        dto.setDescription("Test Description");
        dto.setAvailable(true);
        dto.setOwnerId(1L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void deserializeNewItemDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "name": "New Item",
                    "description": "New Description",
                    "available": true,
                    "requestId": 1
                }
                """;

        NewItemDto dto = objectMapper.readValue(json, NewItemDto.class);

        assertThat(dto.getName()).isEqualTo("New Item");
        assertThat(dto.getDescription()).isEqualTo("New Description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void deserializeNewItemDto_withEmptyName_shouldFailValidation() throws Exception {
        String json = """
                {
                    "name": "",
                    "description": "Description",
                    "available": true
                }
                """;

        NewItemDto dto = objectMapper.readValue(json, NewItemDto.class);

        assertThat(dto.getName()).isEmpty();
    }
}
