package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateUserRequestJsonTest {

    @Autowired
    private JacksonTester<CreateUserRequest> json;

    @Test
    void testCreateUserRequest() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        JsonContent<CreateUserRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }
}