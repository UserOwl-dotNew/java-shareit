package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.Answer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class ItemRequestDtoWithAnswers {
    private String description;
    private LocalDateTime createdAt;
    private List<Answer> answers;
}
