package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto req);

    List<ItemRequestDtoWithAnswers> getSelfRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId);

    ItemRequestDtoWithAnswers getRequestById(Long requestId);
}
