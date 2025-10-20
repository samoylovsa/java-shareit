package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateItemRequestJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequest> json;

    @Test
    void testCreateItemRequest() throws Exception {
        CreateItemRequest request = CreateItemRequest.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(10)
                .build();

        JsonContent<CreateItemRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Powerful drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
    }
}