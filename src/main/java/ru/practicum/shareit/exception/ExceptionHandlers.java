package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@ControllerAdvice("ru.practicum.shareit")
@RestControllerAdvice
@Slf4j
public class ExceptionHandlers {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public ErrorResponse handlerNotFoundException(final NotFoundException e) {
        log.error("Not found error: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ValidationException.class})
    public ErrorResponse handlerValidationException(final ValidationException e) {
        log.error("Ошибка валидации: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("Constraint violation: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации: " + e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DuplicatedDataException.class})
    public ErrorResponse handleDuplicatedDataException(final DuplicatedDataException e) {
        log.error("Duplicate data: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({ErrorIsNotOwner.class, ErrorIsNotBooker.class, ErrorIsNotOwnerAndBooker.class})
    public ErrorResponse handleNotBookerOrOwnerException(final Exception e) {
        log.error("Is not Owner or Booker: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handlerThrowable(final Throwable e) {
        log.error("Unexpected error: ", e);
        return new ErrorResponse("Произошла непредвиденная ошибка." + e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Validation error: {}", errorMessage, e);
        return new ErrorResponse("Ошибка валидации: " + errorMessage);
    }
}
