package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldReturnUser() throws Exception {
        UserResponse mockResponse = UserResponse.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(mockResponse);

        String requestBody = """
            {
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserResponse mockResponse = UserResponse.builder()
                .id(1)
                .name("Test User")
                .email("test@example.com")
                .build();

        when(userService.getUser(1)).thenReturn(mockResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserResponse mockResponse = UserResponse.builder()
                .id(1)
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userService.updateUser(any(Integer.class), any(UpdateUserRequest.class)))
                .thenReturn(mockResponse);

        String requestBody = """
            {
                "name": "Updated Name",
                "email": "updated@example.com"
            }
            """;

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}