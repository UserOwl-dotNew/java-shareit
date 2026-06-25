package ru.practicum.shareit; // Или любой другой подходящий пакет

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.mixin.UserMixin;

@Configuration
public class JacksonConfig {

    /**
     * Явно создаем бин ObjectMapper с модулем поддержки Java Time API (LocalDate и т.д.).
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .addMixIn(UserDto.class, UserMixin.class);
    }
}