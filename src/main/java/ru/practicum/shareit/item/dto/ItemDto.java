package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.ToString;

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
}
