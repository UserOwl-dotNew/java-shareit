package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.practicum.shareit.dto.user.UserDto;

@TestConfiguration
public class TestJacksonConfig {

    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Регистрируем модуль для работы с Java 8 Time API
        mapper.registerModule(new JavaTimeModule());

        // Настройки для корректной десериализации
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Регистрируем модуль с MixIn для UserDto
        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(UserDto.class, UserDtoMixin.class);
        mapper.registerModule(module);

        return mapper;
    }
}