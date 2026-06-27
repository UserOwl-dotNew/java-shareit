package ru.practicum.shareit.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewCommentDto {
    @NotBlank(message = "text не может быть пустым")
    private String text;
}
