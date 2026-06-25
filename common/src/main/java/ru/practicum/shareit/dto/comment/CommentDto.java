package ru.practicum.shareit.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

import static ru.practicum.shareit.constants.SqlConstants.DATA_PATTERN;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATA_PATTERN)
    private LocalDateTime created;
}
