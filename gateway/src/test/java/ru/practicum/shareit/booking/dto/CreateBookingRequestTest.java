package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingRequestTest {

    @Autowired
    private JacksonTester<CreateBookingRequest> json;

    @Test
    void shouldSerializeCorrectly() throws Exception {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(1)
                .start(LocalDateTime.of(2023, 12, 31, 10, 0))
                .end(LocalDateTime.of(2023, 12, 31, 12, 0))
                .build();

        String expectedJson = """
            {
                "itemId": 1,
                "start": "2023-12-31T10:00:00",
                "end": "2023-12-31T12:00:00"
            }
            """;

        assertThat(json.write(request)).isEqualToJson(expectedJson);
    }

    @Test
    void shouldDeserializeCorrectly() throws Exception {
        String content = """
            {
                "itemId": 1,
                "start": "2023-12-31T10:00:00",
                "end": "2023-12-31T12:00:00"
            }
            """;

        CreateBookingRequest request = json.parseObject(content);

        assertThat(request.getItemId()).isEqualTo(1);
        assertThat(request.getStart()).isEqualTo(LocalDateTime.of(2023, 12, 31, 10, 0));
        assertThat(request.getEnd()).isEqualTo(LocalDateTime.of(2023, 12, 31, 12, 0));
    }

    @Test
    void shouldDeserializeWithDifferentDateTimeFormats() throws Exception {
        String content = """
            {
                "itemId": 1,
                "start": "2023-12-31T10:00:00.000",
                "end": "2023-12-31T12:00:00.000"
            }
            """;

        CreateBookingRequest request = json.parseObject(content);

        assertThat(request.getItemId()).isEqualTo(1);
        assertThat(request.getStart()).isNotNull();
        assertThat(request.getEnd()).isNotNull();
    }
}