package ru.practicum.shareit.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.dto.answer.AnswerDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.constants.Constants.DATA_PATTERN;

@Data
@AllArgsConstructor(staticName = "of")
public class ItemRequestDtoWithAnswers {
    private Long id;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime created;
    private List<AnswerDto> items;
}
