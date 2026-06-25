package ru.practicum.shareit.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Email(message = "Неправильный формат email")
    @NotBlank(message = "Требуется email")
    private String email;

    @JsonCreator // Эта аннотация говорит Jackson использовать именно этот конструктор для десериализации
    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
