package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private CreateItemRequest createItemRequest;
    private UpdateItemRequest updateItemRequest;
    private CreateCommentRequest createCommentRequest;

    @BeforeEach
    void setUp() {
        createItemRequest = CreateItemRequest.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        updateItemRequest = UpdateItemRequest.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        createCommentRequest = CreateCommentRequest.builder()
                .text("Comment text")
                .build();
    }

    @Test
    void createItem_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long ownerId = 1L;
        when(itemClient.createItem(anyLong(), any(CreateItemRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest)))
                .andExpect(status().isOk());

        verify(itemClient).createItem(eq(ownerId), any(CreateItemRequest.class));
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenInvalidOwnerId() throws Exception {
        Long invalidOwnerId = -1L;

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", invalidOwnerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createItemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).createItem(anyLong(), any());
    }

    @Test
    void updateItem_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long ownerId = 1L;
        Long itemId = 1L;
        when(itemClient.updateItem(anyLong(), anyLong(), any(UpdateItemRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(ownerId), eq(itemId), any(UpdateItemRequest.class));
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenInvalidItemId() throws Exception {
        Long ownerId = 1L;
        Long invalidItemId = 0L;

        mockMvc.perform(patch("/items/{itemId}", invalidItemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any());
    }

    @Test
    void getItemById_ShouldReturnSuccessAndCallClient_WhenValidIds() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(eq(itemId), eq(userId));
    }

    @Test
    void getItemById_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = -1L;
        Long itemId = 1L;

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    void getAllItemsByOwner_ShouldReturnSuccessAndCallClient_WhenValidOwnerId() throws Exception {
        Long ownerId = 1L;
        when(itemClient.getAllItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(itemClient).getAllItemsByOwner(eq(ownerId));
    }

    @Test
    void getAllItemsByOwner_ShouldReturnBadRequest_WhenInvalidOwnerId() throws Exception {
        Long invalidOwnerId = 0L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", invalidOwnerId))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllItemsByOwner(anyLong());
    }

    @Test
    void searchAvailableItems_ShouldReturnSuccessAndCallClient_WhenValidText() throws Exception {
        String searchText = "test";
        when(itemClient.searchItems(anyString()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk());

        verify(itemClient).searchItems(eq(searchText));
    }

    @Test
    void searchAvailableItems_ShouldReturnEmptyList_WhenTextIsBlank() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        verify(itemClient, never()).searchItems(anyString());
    }

    @Test
    void addComment_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.addComment(anyLong(), anyLong(), any(CreateCommentRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(itemId), eq(userId), any(CreateCommentRequest.class));
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = -1L;
        Long itemId = 1L;

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentRequest)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any());
    }
}