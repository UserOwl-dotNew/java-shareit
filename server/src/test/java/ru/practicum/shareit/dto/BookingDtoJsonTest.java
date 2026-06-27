package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.enums.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeBookingDto_shouldMatchJson() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2026, 6, 25, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 6, 25, 12, 0, 0));
        dto.setStatus(BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("2026-06-25T10:00:00");
        assertThat(json).contains("2026-06-25T12:00:00");
    }

    @Test
    void deserializeBookingDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "id": 1,
                    "start": "2026-06-25T10:00:00",
                    "end": "2026-06-25T12:00:00",
                    "status": "WAITING"
                }
                """;

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 6, 25, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 6, 25, 12, 0, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void serializeNewBookingDto_shouldMatchJson() throws Exception {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 6, 25, 10, 0, 0));
        dto.setEnd(LocalDateTime.of(2026, 6, 25, 12, 0, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("2026-06-25T10:00:00");
        assertThat(json).contains("2026-06-25T12:00:00");
    }

    @Test
    void deserializeNewBookingDto_shouldMatchObject() throws Exception {
        String json = """
                {
                    "itemId": 1,
                    "start": "2026-06-25T10:00:00",
                    "end": "2026-06-25T12:00:00"
                }
                """;

        NewBookingDto dto = objectMapper.readValue(json, NewBookingDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 6, 25, 10, 0, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 6, 25, 12, 0, 0));
    }

    @Test
    void deserializeBookingDto_withInvalidStatus_shouldThrowException() throws Exception {
        String json = """
                {
                    "id": 1,
                    "status": "INVALID_STATUS"
                }
                """;

        org.junit.jupiter.api.Assertions.assertThrows(
                com.fasterxml.jackson.databind.exc.InvalidFormatException.class,
                () -> objectMapper.readValue(json, BookingDto.class)
        );
    }
}