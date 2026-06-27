package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.NewItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {
    @Mapping(target = "available", source = "available")
    @Mapping(target = "name", source = "name")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "name", source = "name")
    Item toItem(NewItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "available", source = "available")
    void updateItemFields(ItemDto source, @MappingTarget Item target);
}
