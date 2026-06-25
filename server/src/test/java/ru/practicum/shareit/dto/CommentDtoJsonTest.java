package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.dto.comment.NewCommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeCommentDto_shouldMatchJson() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Great item!");
        dto.setAuthorName("Test User");
        dto.setCreated(LocalDateTime.of(2026, 6, 25, 10, 0, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"Test User\"");
        assertThat(json).contains("2026-06-25T10:00:00");
    }

    @Test
    void deserializeCommentDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "id": 1,
                    "text": "Great item!",
                    "authorName": "Test User",
                    "created": "2026-06-25T10:00:00"
                }
                """;

        CommentDto dto = objectMapper.readValue(json, CommentDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getAuthorName()).isEqualTo("Test User");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 6, 25, 10, 0, 0));
    }

    @Test
    void serializeNewCommentDto_shouldMatchJson() throws Exception {
        NewCommentDto dto = new NewCommentDto();
        dto.setText("New comment");

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"text\":\"New comment\"");
        // В NewCommentDto нет полей id, authorName, created
        assertThat(json).doesNotContain("\"id\"");
        assertThat(json).doesNotContain("\"authorName\"");
        assertThat(json).doesNotContain("\"created\"");
    }

    @Test
    void deserializeNewCommentDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "text": "New comment"
                }
                """;

        NewCommentDto dto = objectMapper.readValue(json, NewCommentDto.class);

        assertThat(dto.getText()).isEqualTo("New comment");
    }
}