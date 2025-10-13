package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        ItemResponse mockResponse = ItemResponse.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .build();

        when(itemService.createItem(eq(123), any(CreateItemRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"Powerful drill\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemResponse mockResponse = ItemResponse.builder()
                .id(1)
                .name("Updated Drill")
                .description("Updated description")
                .build();

        when(itemService.updateItem(eq(123), eq(1), any(UpdateItemRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Drill\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        GetItemResponse mockResponse = GetItemResponse.builder()
                .id(1)
                .name("Hammer")
                .available(true)
                .build();

        when(itemService.getItemById(1, 123)).thenReturn(mockResponse);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hammer"));
    }

    @Test
    void getAllItemsByOwner_shouldReturnItemsList() throws Exception {
        GetItemResponse item1 = GetItemResponse.builder().id(1).name("Item1").build();
        GetItemResponse item2 = GetItemResponse.builder().id(2).name("Item2").build();

        when(itemService.getAllItemsByOwner(123)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void searchAvailableItems_shouldReturnSearchResults() throws Exception {
        ItemResponse item1 = ItemResponse.builder().id(1).name("Drill").build();
        ItemResponse item2 = ItemResponse.builder().id(2).name("Hammer").build();

        when(itemService.searchItems("drill")).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void searchAvailableItems_withBlankText_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentResponse mockResponse = CommentResponse.builder()
                .id(1)
                .text("Great item!")
                .authorName("John")
                .build();

        when(itemService.addComment(eq(1), eq(123), any(CreateCommentRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Great item!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John"));
    }
}