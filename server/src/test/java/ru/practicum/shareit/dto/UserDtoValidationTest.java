package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.dto.user.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserDtoValidationTest {

    @Autowired
    private Validator validator;

    @Test
    void userDto_withValidFields_shouldHaveNoViolations() {
        UserDto dto = new UserDto();
        dto.setName("Valid User");
        dto.setEmail("valid@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void userDto_withEmptyName_shouldHaveViolation() {
        UserDto dto = new UserDto();
        dto.setName("");
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void userDto_withNullName_shouldHaveViolation() {
        UserDto dto = new UserDto();
        dto.setName(null);
        dto.setEmail("test@example.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void userDto_withInvalidEmail_shouldHaveViolation() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void userDto_withNullEmail_shouldHaveViolation() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}