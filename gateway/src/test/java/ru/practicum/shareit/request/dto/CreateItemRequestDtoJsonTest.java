package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CreateItemRequestDto> json;

    @Test
    void testCreateItemRequestDto() throws Exception {
        CreateItemRequestDto request = CreateItemRequestDto.builder()
                .description("Need a power drill for home repairs")
                .build();

        JsonContent<CreateItemRequestDto> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Need a power drill for home repairs");
    }
}