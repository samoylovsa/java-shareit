package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateUserRequest> json;

    @Test
    void testUpdateUserRequest() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .build();

        JsonContent<UpdateUserRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Updated");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.updated@example.com");
    }

    @Test
    void testUpdateUserRequestPartial() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("only.email@example.com")
                .build();

        JsonContent<UpdateUserRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isNull();
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("only.email@example.com");
    }
}