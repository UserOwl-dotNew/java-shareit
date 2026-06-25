package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Поле ownerId должно быть указано")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotBlank(message = "Поле name должно быть указано")
    @Size(max = 100, message = "Поле name не может быть длиннее 100 символов")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Поле description должно быть указано")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Поле available должно быть указано")
    @Column(name = "available", nullable = false)
    private Boolean available;
}
