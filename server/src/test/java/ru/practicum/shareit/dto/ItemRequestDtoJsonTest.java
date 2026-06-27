package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestDtoWithAnswers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemRequestDto_shouldMatchJson() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need an item");
        dto.setCreated(LocalDateTime.of(2026, 6, 25, 10, 0, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need an item\"");
        assertThat(json).contains("2026-06-25T10:00:00");
    }

    @Test
    void deserializeItemRequestDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need an item",
                    "requestorId": 2,
                    "createdAt": "2026-06-25T10:00:00"
                }
                """;

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need an item");
    }

    @Test
    void deserializeItemRequestDtoWithAnswers_shouldMatchObject() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need an item",
                    "requestorId": 2,
                    "createdAt": "2026-06-25T10:00:00",
                    "answers": [
                        {
                            "id": 1,
                            "requestId": 1,
                            "itemId": 2,
                            "name": "Answer Item",
                            "ownerId": 3
                        }
                    ]
                }
                """;

        ItemRequestDtoWithAnswers dto = objectMapper.readValue(json, ItemRequestDtoWithAnswers.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need an item");
    }

    @Test
    void deserializeItemRequestDto_withoutCreatedAt_shouldCreateWithNullCreatedAt() throws Exception {
        String json = """
                {
                    "id": 1,
                    "description": "Need an item",
                    "requestorId": 2
                }
                """;

        ItemRequestDto dto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need an item");
    }
}