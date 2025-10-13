package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestResponse mockResponse = ItemRequestResponse.builder()
                .id(1)
                .description("Need a drill")
                .build();

        when(itemRequestService.createItemRequest(any(), eq(123))).thenReturn(mockResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Need a drill\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getUserItemRequests_shouldReturnUserRequests() throws Exception {
        ItemRequestWithAnswersResponse response1 = ItemRequestWithAnswersResponse.builder()
                .id(1)
                .description("First request")
                .build();
        ItemRequestWithAnswersResponse response2 = ItemRequestWithAnswersResponse.builder()
                .id(2)
                .description("Second request")
                .build();

        when(itemRequestService.getUserItemRequests(123)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getAllItemRequests_shouldReturnAllRequests() throws Exception {
        ItemRequestResponse response1 = ItemRequestResponse.builder()
                .id(1)
                .description("Request from user 1")
                .build();
        ItemRequestResponse response2 = ItemRequestResponse.builder()
                .id(2)
                .description("Request from user 2")
                .build();

        when(itemRequestService.getAllItemRequests(123)).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getItemRequestById_shouldReturnRequest() throws Exception {
        ItemRequestWithAnswersResponse mockResponse = ItemRequestWithAnswersResponse.builder()
                .id(10)
                .description("Specific request details")
                .build();

        when(itemRequestService.getItemRequestById(10, 123)).thenReturn(mockResponse);

        mockMvc.perform(get("/requests/10")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.description").value("Specific request details"));
    }
}