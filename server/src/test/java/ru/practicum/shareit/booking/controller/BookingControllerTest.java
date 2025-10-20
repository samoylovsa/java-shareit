package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnBooking() throws Exception {
        BookingResponse mockResponse = BookingResponse.builder()
                .id(1)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(eq(123), any(CreateBookingRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingResponse mockResponse = BookingResponse.builder()
                .id(1)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(123, 1, true)).thenReturn(mockResponse);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 123)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        BookingResponse mockResponse = BookingResponse.builder()
                .id(1)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.getBooking(123, 1)).thenReturn(mockResponse);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getUserBookings_shouldReturnBookingsList() throws Exception {
        BookingResponse booking1 = BookingResponse.builder().id(1).status(BookingStatus.APPROVED).build();
        BookingResponse booking2 = BookingResponse.builder().id(2).status(BookingStatus.WAITING).build();

        when(bookingService.getUserBookings(123, BookingState.ALL)).thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getUserBookings_withDefaultState_shouldReturnBookings() throws Exception {
        BookingResponse booking1 = BookingResponse.builder().id(1).status(BookingStatus.APPROVED).build();

        when(bookingService.getUserBookings(123, BookingState.ALL)).thenReturn(List.of(booking1));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOwnerBookings_shouldReturnBookingsList() throws Exception {
        BookingResponse booking1 = BookingResponse.builder().id(1).status(BookingStatus.APPROVED).build();
        BookingResponse booking2 = BookingResponse.builder().id(2).status(BookingStatus.REJECTED).build();

        when(bookingService.getOwnerBookings(123, BookingState.ALL)).thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getOwnerBookings_withDefaultState_shouldReturnBookings() throws Exception {
        BookingResponse booking1 = BookingResponse.builder().id(1).status(BookingStatus.APPROVED).build();

        when(bookingService.getOwnerBookings(123, BookingState.ALL)).thenReturn(List.of(booking1));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}