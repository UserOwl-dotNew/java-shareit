package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
}
