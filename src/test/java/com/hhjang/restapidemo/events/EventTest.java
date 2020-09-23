package com.hhjang.restapidemo.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("hh-jang")
                .description("me")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "hh-jang";
        String description = "me";

        // When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}