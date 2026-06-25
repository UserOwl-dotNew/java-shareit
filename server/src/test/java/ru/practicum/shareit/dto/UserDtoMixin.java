// Файл: src/test/java/ru/practicum/shareit/dto/UserDtoMixin.java (альтернативная версия)

package ru.practicum.shareit.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.practicum.shareit.dto.user.UserDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserDtoMixin {

    @JsonCreator
    public static UserDto create(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("email") String email
    ) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}