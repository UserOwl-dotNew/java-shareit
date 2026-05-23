package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
public class Item {
    private Long id;
    @NotNull
    private Long ownerId;
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private boolean available;
}
