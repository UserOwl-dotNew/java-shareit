package ru.practicum.shareit.item.service;

import ru.practicum.shareit.dto.comment.CommentDto;
import ru.practicum.shareit.dto.comment.NewCommentDto;
import ru.practicum.shareit.dto.item.ItemDto;
import ru.practicum.shareit.dto.item.ItemWithBookingDto;
import ru.practicum.shareit.dto.item.NewItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

/**
 * Сервис для управления вещами (Item).
 * <p>
 * Предоставляет методы для выполнения операций с вещами:
 * создание, обновление, получение, удаление, а также управление комментариями.
 * </p>
 */
public interface ItemService {

    /**
     * Добавление новой вещи.
     *
     * @param ownerId ID владельца вещи
     * @param dto     данные для создания вещи
     * @return созданная вещь
     */
    ItemDto addItem(Long ownerId, NewItemDto dto);

    /**
     * Добавление комментария к вещи.
     *
     * @param userId ID пользователя, оставляющего комментарий
     * @param itemId ID вещи
     * @param dto    данные для создания комментария
     * @return созданный комментарий
     */
    CommentDto addComment(Long userId, Long itemId, NewCommentDto dto);

    /**
     * Обновление существующей вещи.
     *
     * @param ownerId ID владельца вещи
     * @param itemId  ID обновляемой вещи
     * @param dto     новые данные для вещи
     * @return обновлённая вещь
     */
    ItemDto updateItem(Long ownerId, Long itemId, ItemDto dto);

    /**
     * Получение вещи по ID.
     *
     * @param itemId ID вещи
     * @return найденная вещь
     */
    ItemDto getItem(Long itemId);

    /**
     * Удаление вещи.
     *
     * @param ownerId ID владельца вещи
     * @param itemId  ID удаляемой вещи
     */
    void deleteItem(Long ownerId, Long itemId);

    /**
     * Получение всех вещей владельца (без информации о бронированиях).
     *
     * @param ownerId ID владельца
     * @return список вещей владельца
     */
    List<ItemWithBookingDto> getAllItemsFromOwner(Long ownerId);

    /**
     * Получение всех вещей владельца с информацией о бронированиях.
     *
     * @param ownerId ID владельца
     * @return список вещей владельца с бронированиями
     */
    List<ItemWithBookingDto> getAllItemsWithBookings(Long ownerId);

    /**
     * Поиск вещей по тексту (по названию и описанию).
     *
     * @param text текст для поиска
     * @return список найденных вещей
     */
    List<ItemDto> getAllItemsByText(String text);

    /**
     * Получение вещи с информацией о бронированиях для владельца.
     *
     * @param ownerId ID владельца
     * @param itemId  ID вещи
     * @return вещь с информацией о бронированиях
     */
    ItemWithBookingDto getItemByOwnerForUser(Long ownerId, Long itemId);

    /**
     * Получение сущности вещи по ID (для внутреннего использования).
     *
     * @param itemId ID вещи
     * @return сущность вещи
     */
    Item getItemEntity(Long itemId);

    /**
     * Получение вещи для пользователя с учётом прав доступа.
     * <p>
     * Если пользователь является владельцем, возвращается полная информация с бронированиями.
     * Если нет - только основная информация и комментарии.
     * </p>
     *
     * @param itemId ID вещи
     * @param userId ID пользователя
     * @return вещь с учётом прав пользователя
     */
    ItemWithBookingDto getItemFromUser(Long itemId, Long userId);

    /**
     * Получение вещи для пользователя с учётом того, кто хозяин вещи
     * <p>
     * Определяет, какой метод использовать getItemFromUser или getItemByOwnerForUser
     * </p>
     *
     * @param ownerId ID хозяина вещи
     * @param userId  ID пользователя
     * @param itemId  ID вещи
     * @return вещь, с учетом прав пользователя
     */
    Object getItemForUserOrOwner(Long ownerId, Long userId, Long itemId);
}
