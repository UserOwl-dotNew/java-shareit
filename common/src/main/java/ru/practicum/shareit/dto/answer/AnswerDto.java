package ru.practicum.shareit.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    private Long id;

    @NotNull(message = "ID запроса не может быть пустым")
    private Long requestId;

    @NotNull(message = "ID вещи не может быть пустым")
    private Long itemId;

    @NotBlank(message = "Название ответа не может быть пустым")
    private String name;

    @NotNull(message = "ID владельца не может быть пустым")
    private Long ownerId;
}