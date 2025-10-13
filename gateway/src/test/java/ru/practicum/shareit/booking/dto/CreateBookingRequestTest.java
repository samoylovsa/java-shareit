package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingRequestTest {
    @Autowired
    private JacksonTester<CreateBookingRequest> json;

    @Test
    void testCreateBookingRequest() throws Exception {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.of(2023, 12, 31, 10, 0))
                .end(LocalDateTime.of(2023, 12, 31, 12, 0))
                .build();

        JsonContent<CreateBookingRequest> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-12-31T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-12-31T12:00:00");
    }
}