package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private CreateItemRequestDto createItemRequestDto;

    @BeforeEach
    void setUp() {
        createItemRequestDto = CreateItemRequestDto.builder()
                .description("Need a drill for home repairs")
                .build();
    }

    @Test
    void createItemRequest_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Integer userId = 1;
        when(itemRequestClient.createItemRequest(any(CreateItemRequestDto.class), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isOk());

        verify(itemRequestClient).createItemRequest(any(CreateItemRequestDto.class), eq(userId));
    }

    @Test
    void createItemRequest_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Integer invalidUserId = -1;

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(any(), anyInt());
    }

    @Test
    void createItemRequest_ShouldReturnBadRequest_WhenInvalidRequestBody() throws Exception {
        Integer userId = 1;
        CreateItemRequestDto invalidRequest = CreateItemRequestDto.builder()
                .description("") // пустое описание
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).createItemRequest(any(), anyInt());
    }

    @Test
    void getUserItemRequests_ShouldReturnSuccessAndCallClient_WhenValidUserId() throws Exception {
        Integer userId = 1;
        when(itemRequestClient.getUserItemRequests(anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getUserItemRequests(eq(userId));
    }

    @Test
    void getUserItemRequests_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Integer invalidUserId = 0;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getUserItemRequests(anyInt());
    }

    @Test
    void getAllItemRequests_ShouldReturnSuccessAndCallClient_WhenValidUserId() throws Exception {
        Integer userId = 1;
        when(itemRequestClient.getAllItemRequests(anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllItemRequests(eq(userId));
    }

    @Test
    void getAllItemRequests_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Integer invalidUserId = -1;

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllItemRequests(anyInt());
    }

    @Test
    void getItemRequestById_ShouldReturnSuccessAndCallClient_WhenValidIds() throws Exception {
        Integer requestId = 1;
        Integer userId = 1;
        when(itemRequestClient.getItemRequestById(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getItemRequestById(eq(requestId), eq(userId));
    }

    @Test
    void getItemRequestById_ShouldReturnBadRequest_WhenInvalidRequestId() throws Exception {
        Integer invalidRequestId = 0;
        Integer userId = 1;

        mockMvc.perform(get("/requests/{requestId}", invalidRequestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getItemRequestById(anyInt(), anyInt());
    }

    @Test
    void getItemRequestById_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Integer requestId = 1;
        Integer invalidUserId = -1;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getItemRequestById(anyInt(), anyInt());
    }
}