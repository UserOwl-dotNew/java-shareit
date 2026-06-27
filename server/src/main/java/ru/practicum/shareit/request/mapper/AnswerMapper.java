package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.dto.answer.AnswerDto;
import ru.practicum.shareit.request.model.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "name", source = "name")
    AnswerDto toDto(Answer answer);

    @Mapping(target = "name", source = "name")
    Answer toEntity(AnswerDto dto);
}