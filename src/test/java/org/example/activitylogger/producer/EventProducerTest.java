package org.example.activitylogger.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.activitylogger.enums.ActivityType;
import org.example.activitylogger.model.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.stream.function.StreamBridge;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventProducerTest {

    private StreamBridge streamBridge;
    private EventProducer eventProducer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        streamBridge = mock(StreamBridge.class);
        eventProducer = new EventProducer(streamBridge);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void publishUserActivity_shouldSendEventToStreamBridge() {
        UUID userId = UUID.randomUUID();
        UserActivityEvent event = new UserActivityEvent(userId, ActivityType.LOGIN, "User logged in");

        eventProducer.publishUserActivity(event);

        verify(streamBridge).send(eq("userActivity-out-0"), any(UserActivityEvent.class));
    }
}
