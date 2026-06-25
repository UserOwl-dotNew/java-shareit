package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NewCommentDto {
    private Long id;

    @NotNull(message = "text не может быть пустым")
    private String text;
}
