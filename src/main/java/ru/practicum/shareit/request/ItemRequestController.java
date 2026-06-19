package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constant.SqlConstants.REQUEST_HEADER_SHARER_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @GetMapping
    public List<ItemRequestDtoWithAnswers> getSelfRequests(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        // От более новых к более старым
        return service.getSelfRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId) {
        /*
         * От более новых к более старым
         * получить список запросов, созданных другими пользователями.
         * С помощью этого эндпоинта пользователи смогут просматривать
         * существующие запросы, на которые они могли бы ответить.
         * Запросы сортируются по дате создания от более новых к более старым.
         */
        return service.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswers getOne(@PathVariable("requestId") Long requestId) {
        /*
         * вместе с данными об ответах на него в том же формате, что и в эндпоинте GET /requests
         * Посмотреть данные об отдельном запросе может любой пользователь.
         */
        return service.getRequestById(requestId);
    }

    @PostMapping
    public ItemRequestDto add(@RequestHeader(REQUEST_HEADER_SHARER_USER_ID) Long userId,
                              @RequestBody ItemRequestDto req) {
        /*
         * Основная часть запроса — текст запроса, в котором пользователь описывает, какая именно вещь ему нужна.
         */
        return service.createRequest(userId, req);
    }
}
