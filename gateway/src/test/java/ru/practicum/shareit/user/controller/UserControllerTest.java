package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        createUserRequest = CreateUserRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();
    }

    @Test
    void createUser_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        when(userClient.createUser(any(CreateUserRequest.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk());

        verify(userClient).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .name("")
                .email("invalid-email")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any());
    }

    @Test
    void updateUser_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long userId = 1L;
        when(userClient.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(eq(userId), any(UpdateUserRequest.class));
    }

    @Test
    void updateUser_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = -1L;

        mockMvc.perform(patch("/users/{userId}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any());
    }

    @Test
    void getUser_ShouldReturnSuccessAndCallClient_WhenValidUserId() throws Exception {
        Long userId = 1L;
        when(userClient.getUser(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(eq(userId));
    }

    @Test
    void getUser_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = 0L;

        mockMvc.perform(get("/users/{userId}", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).getUser(anyLong());
    }

    @Test
    void deleteUser_ShouldReturnNoContentAndCallClient_WhenValidUserId() throws Exception {
        Long userId = 1L;
        when(userClient.deleteUser(anyLong())).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userClient).deleteUser(eq(userId));
    }

    @Test
    void deleteUser_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = -1L;

        mockMvc.perform(delete("/users/{userId}", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).deleteUser(anyLong());
    }
}
