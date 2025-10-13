package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateItemRequestJsonTest {

    @Autowired
    private JacksonTester<UpdateItemRequest> json;

    @Test
    void testUpdateItemRequest() throws Exception {
        UpdateItemRequest request = UpdateItemRequest.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        JsonContent<UpdateItemRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Updated description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(false);
    }

    @Test
    void testUpdateItemRequestPartial() throws Exception {
        UpdateItemRequest request = UpdateItemRequest.builder()
                .name("Only Name Updated")
                .build();

        JsonContent<UpdateItemRequest> result = json.write(request);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Only Name Updated");
        assertThat(result).extractingJsonPathStringValue("$.description").isNull();
        assertThat(result).extractingJsonPathBooleanValue("$.available").isNull();
    }
}