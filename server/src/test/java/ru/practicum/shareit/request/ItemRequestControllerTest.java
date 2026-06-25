package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.answer.AnswerDto;
import ru.practicum.shareit.dto.request.ItemRequestDto;
import ru.practicum.shareit.dto.request.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.AnswerMapper;
import ru.practicum.shareit.request.model.Answer;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.SqlConstants.REQUEST_HEADER_SHARER_USER_ID;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnswerMapper answerMapper;

    @MockBean
    private ItemRequestService requestService;

    private static final User user1 = new User();
    private static final User user2 = new User();
    private static final Item item = new Item();
    private static final ItemRequest itemRequest = new ItemRequest();
    private static final LocalDateTime now = LocalDateTime.now();

    private AnswerDto answerDto;

    @BeforeAll
    static void init() {
        user1.setId(1L);
        user1.setName("Test Name1");
        user1.setEmail("Test email1");

        user2.setId(2L);
        user2.setName("Test Name2");
        user2.setEmail("Test email2");

        item.setId(1L);
        item.setOwnerId(user1.getId());
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setName("Test Title");

        itemRequest.setId(1L);
        itemRequest.setOwnerId(user1.getId());
        itemRequest.setDescription("Test Description");
        itemRequest.setCreated(now);
    }

    @BeforeEach
    void setUp() {
        // Создаем AnswerDto для мока
        answerDto = new AnswerDto();
        answerDto.setId(1L);
        answerDto.setRequestId(1L);
        answerDto.setItemId(1L);
        answerDto.setName("Test Item");
        answerDto.setOwnerId(2L);

        // Настраиваем мок AnswerMapper
        when(answerMapper.toDto(any(Answer.class))).thenReturn(answerDto);
        when(answerMapper.toEntity(any(AnswerDto.class))).thenReturn(new Answer());
    }

    @Test
    void addRequest_ShouldReturnCreatedRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Test Description");
        dto.setCreated(LocalDateTime.now());

        ItemRequestDto savedRequest = new ItemRequestDto();
        savedRequest.setId(1L);
        savedRequest.setDescription("Test Description");
        savedRequest.setCreated(LocalDateTime.now());

        when(requestService.createRequest(eq(1L), any(ItemRequestDto.class))).thenReturn(savedRequest);

        mvc.perform(post("/requests")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void getSelfRequest_ShouldReturnRequestsWithAnswers() throws Exception {
        ItemRequestDtoWithAnswers requestWithAnswers = ItemRequestDtoWithAnswers.of(
                itemRequest.getId(),
                "Test Description",
                now,
                List.of(answerDto)
        );

        when(requestService.getSelfRequests(eq(user1.getId()))).thenReturn(List.of(requestWithAnswers));

        mvc.perform(get("/requests")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].items[0].id").value(1L))
                .andExpect(jsonPath("$[0].items[0].name").value("Test Item"));

        verify(requestService, times(1)).getSelfRequests(anyLong());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Test Description1");
        dto.setCreated(now);

        when(requestService.getAllRequests(eq(user1.getId()))).thenReturn(List.of(dto));

        mvc.perform(get("/requests/all")
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Test Description1"));
    }

    @Test
    void getOneRequest_ShouldReturnRequestWithAnswers() throws Exception {
        ItemRequestDtoWithAnswers requestWithAnswers = ItemRequestDtoWithAnswers.of(
                itemRequest.getId(),
                "Test Description",
                now,
                List.of(answerDto)
        );

        when(requestService.getRequestById(eq(item.getId()))).thenReturn(requestWithAnswers);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(REQUEST_HEADER_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.items[0].id").value(1L))
                .andExpect(jsonPath("$.items[0].name").value("Test Item"));
    }
}
