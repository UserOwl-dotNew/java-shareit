package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
public class User {
    private Long id;
    private String name;
    @Email(message = "Неправильный формат email")
    @NotBlank(message = "Требуется email")
    private String email;
}
