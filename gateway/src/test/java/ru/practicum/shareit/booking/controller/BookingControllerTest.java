package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private CreateBookingRequest createBookingRequest;

    @BeforeEach
    void setUp() {
        createBookingRequest = CreateBookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void createBooking_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long bookerId = 1L;
        when(bookingClient.createBooking(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isOk());

        verify(bookingClient).createBooking(eq(bookerId), any(CreateBookingRequest.class));
    }

    @Test
    void createBooking_ShouldReturnBadRequest_WhenInvalidBookerId() throws Exception {
        Long invalidBookerId = -1L;

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", invalidBookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).createBooking(anyLong(), any());
    }

    @Test
    void approveBooking_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(eq(ownerId), eq(bookingId), eq(approved));
    }

    @Test
    void approveBooking_ShouldReturnBadRequest_WhenInvalidOwnerId() throws Exception {
        Long invalidOwnerId = 0L;
        Long bookingId = 1L;
        Boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", invalidOwnerId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBooking_ShouldReturnSuccessAndCallClient_WhenValidIds() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    void getBooking_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = -1L;
        Long bookingId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    void getUserBookings_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.toString()))
                .andExpect(status().isOk());

        verify(bookingClient).getUserBookings(eq(userId), eq(state));
    }

    @Test
    void getUserBookings_ShouldReturnBadRequest_WhenInvalidUserId() throws Exception {
        Long invalidUserId = 0L;
        BookingState state = BookingState.ALL;

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", invalidUserId)
                        .param("state", state.toString()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any());
    }

    @Test
    void getUserBookings_ShouldUseDefaultState_WhenStateNotProvided() throws Exception {
        Long userId = 1L;
        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(bookingClient).getUserBookings(eq(userId), eq(BookingState.ALL));
    }

    @Test
    void getOwnerBookings_ShouldReturnSuccessAndCallClient_WhenValidRequest() throws Exception {
        Long ownerId = 1L;
        BookingState state = BookingState.CURRENT;
        when(bookingClient.getOwnerBookings(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state.toString()))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(eq(ownerId), eq(state));
    }

    @Test
    void getOwnerBookings_ShouldReturnBadRequest_WhenInvalidOwnerId() throws Exception {
        Long invalidOwnerId = -1L;
        BookingState state = BookingState.FUTURE;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", invalidOwnerId)
                        .param("state", state.toString()))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getOwnerBookings(anyLong(), any());
    }

    @Test
    void getOwnerBookings_ShouldUseDefaultState_WhenStateNotProvided() throws Exception {
        Long ownerId = 1L;
        when(bookingClient.getOwnerBookings(anyLong(), any(BookingState.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(eq(ownerId), eq(BookingState.ALL));
    }
}