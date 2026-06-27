package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.answer.AnswerDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.mapper.AnswerMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.AnswerRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final AnswerRepository answerRepository;
    private final ItemRequestMapper mapper;
    private final AnswerMapper answerMapper;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto req) {
        if (requestRepository.existsByDescription(req.getDescription())) {
            throw new DuplicatedDataException("Запрос с таким описанием уже существует description=" + req.getDescription());
        }
        ItemRequest itemRequest = mapper.mapToItemRequest(req);
        itemRequest.setOwnerId(userId);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = requestRepository.save(itemRequest);
        return mapper.mapToItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getSelfRequests(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = requestRepository.findAllByOwnerId(userId, sort);
        return requests.stream()
                .map(this::mapToItemRequestDtoWithAnswers)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = requestRepository.findByOwnerIdNot(userId, sort);
        return requests.stream()
                .map(mapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDtoWithAnswers getRequestById(Long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id=" + requestId + " не найдено"));
        return mapToItemRequestDtoWithAnswers(itemRequest);
    }

    private ItemRequestDtoWithAnswers mapToItemRequestDtoWithAnswers(ItemRequest itemRequests) {
        List<AnswerDto> answers = answerRepository.findByRequestId(itemRequests.getId()).stream()
                .map(answerMapper::toDto)
                .collect(Collectors.toList());
        return ItemRequestDtoWithAnswers.of(
                itemRequests.getId(),
                itemRequests.getDescription(),
                itemRequests.getCreated(),
                answers
        );
    }
}
