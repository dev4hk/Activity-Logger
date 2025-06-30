package org.example.activitylogger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.activitylogger.consumer.EventConsumer;
import org.example.activitylogger.dto.UserActivityRequestDto;
import org.example.activitylogger.enums.ActivityType;
import org.example.activitylogger.model.UserActivityEvent;
import org.example.activitylogger.producer.EventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventProducer eventProducer;

    @MockitoBean
    private EventConsumer eventConsumer;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void publishEvent_shouldReturnOkAndPublishEvent_whenValidRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserActivityRequestDto requestDto = new UserActivityRequestDto(userId, ActivityType.LOGIN, "User logged in via API");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        doNothing().when(eventProducer).publishUserActivity(any(UserActivityEvent.class));

        mockMvc.perform(post("/api/events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Event published: LOGIN by " + userId));

        verify(eventProducer, times(1)).publishUserActivity(any(UserActivityEvent.class));
    }

    @Test
    void publishEvent_shouldReturnBadRequest_whenInvalidUserId() throws Exception {
        UserActivityRequestDto requestDto = new UserActivityRequestDto(null, ActivityType.LOGIN, "User logged in");
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for input data."))
                .andExpect(jsonPath("$.errors.userId").value("User ID cannot be null"));

        verify(eventProducer, times(0)).publishUserActivity(any(UserActivityEvent.class));
    }

    @Test
    void publishEvent_shouldReturnBadRequest_whenDetailsTooLong() throws Exception {
        UUID userId = UUID.randomUUID();
        String longDetails = "a".repeat(300);
        UserActivityRequestDto requestDto = new UserActivityRequestDto(userId, ActivityType.VIEW_PAGE, longDetails);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/events/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed for input data."))
                .andExpect(jsonPath("$.errors.details").value("Details must be between 1 and 255 characters"));

        verify(eventProducer, times(0)).publishUserActivity(any(UserActivityEvent.class));
    }

    @Test
    void getEvents_shouldReturnAllLoggedEvents() throws Exception {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UserActivityEvent event1 = new UserActivityEvent(userId1, ActivityType.LOGIN, "Event 1");
        UserActivityEvent event2 = new UserActivityEvent(userId2, ActivityType.LOGOUT, "Event 2");
        List<UserActivityEvent> mockEvents = Arrays.asList(event1, event2);

        when(eventConsumer.getLoggedEvents()).thenReturn(mockEvents);

        mockMvc.perform(get("/api/events/dashboard")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId1.toString()))
                .andExpect(jsonPath("$[0].activityType").value(ActivityType.LOGIN.toString()))
                .andExpect(jsonPath("$[1].userId").value(userId2.toString()))
                .andExpect(jsonPath("$[1].activityType").value(ActivityType.LOGOUT.toString()))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventConsumer, times(1)).getLoggedEvents();
    }

    @Test
    void getEvents_shouldReturnEmptyList_whenNoEvents() throws Exception {
        when(eventConsumer.getLoggedEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/dashboard")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(eventConsumer, times(1)).getLoggedEvents();
    }

    @Test
    void getEventsByUserId_shouldReturnFilteredEvents() throws Exception {
        UUID targetUserId = UUID.randomUUID();
        UserActivityEvent event1 = new UserActivityEvent(targetUserId, ActivityType.VIEW_PAGE, "Page A");
        UserActivityEvent event2 = new UserActivityEvent(targetUserId, ActivityType.ADD_TO_CART, "Item X");
        List<UserActivityEvent> filteredMockEvents = Arrays.asList(event1, event2);

        when(eventConsumer.getEventsByUserId(eq(targetUserId.toString()))).thenReturn(filteredMockEvents);

        mockMvc.perform(get("/api/events/by-user")
                        .param("userId", targetUserId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(targetUserId.toString()))
                .andExpect(jsonPath("$[1].userId").value(targetUserId.toString()))
                .andExpect(jsonPath("$.length()").value(2));

        verify(eventConsumer, times(1)).getEventsByUserId(eq(targetUserId.toString()));
    }

    @Test
    void getEventsByUserId_shouldReturnEmptyList_whenNoEventsForUser() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        when(eventConsumer.getEventsByUserId(eq(nonExistentUserId.toString()))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/by-user")
                        .param("userId", nonExistentUserId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(eventConsumer, times(1)).getEventsByUserId(eq(nonExistentUserId.toString()));
    }

    @Test
    void getEventsByUserId_shouldReturnBadRequest_whenInvalidUuidFormat() throws Exception {
        String invalidUuid = "not-a-valid-uuid";
        when(eventConsumer.getEventsByUserId(eq(invalidUuid))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/events/by-user")
                        .param("userId", invalidUuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(eventConsumer, times(1)).getEventsByUserId(eq(invalidUuid));
    }
}