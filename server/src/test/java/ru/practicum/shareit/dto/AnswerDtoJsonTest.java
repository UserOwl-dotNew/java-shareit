package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.answer.AnswerDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AnswerDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeAnswerDto_shouldMatchJson() throws Exception {
        AnswerDto dto = new AnswerDto();
        dto.setId(1L);
        dto.setRequestId(2L);
        dto.setItemId(3L);
        dto.setName("Answer Item");
        dto.setOwnerId(4L);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"requestId\":2");
        assertThat(json).contains("\"itemId\":3");
        assertThat(json).contains("\"name\":\"Answer Item\"");
        assertThat(json).contains("\"ownerId\":4");
    }

    @Test
    void deserializeAnswerDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "id": 1,
                    "requestId": 2,
                    "itemId": 3,
                    "name": "Answer Item",
                    "ownerId": 4
                }
                """;

        AnswerDto dto = objectMapper.readValue(json, AnswerDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRequestId()).isEqualTo(2L);
        assertThat(dto.getItemId()).isEqualTo(3L);
        assertThat(dto.getName()).isEqualTo("Answer Item");
        assertThat(dto.getOwnerId()).isEqualTo(4L);
    }

    @Test
    void deserializeAnswerDto_withoutName_shouldCreateWithNullName() throws Exception {
        String json = """
                {
                    "id": 1,
                    "requestId": 2,
                    "itemId": 3,
                    "ownerId": 4
                }
                """;

        AnswerDto dto = objectMapper.readValue(json, AnswerDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRequestId()).isEqualTo(2L);
        assertThat(dto.getItemId()).isEqualTo(3L);
        assertThat(dto.getName()).isNull();
        assertThat(dto.getOwnerId()).isEqualTo(4L);
    }
}