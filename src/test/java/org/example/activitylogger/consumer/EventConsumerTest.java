package org.example.activitylogger.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.activitylogger.enums.ActivityType;
import org.example.activitylogger.model.UserActivityEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EventConsumerTest {

    private EventConsumer eventConsumer;
    private SimpMessagingTemplate messagingTemplate;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        eventConsumer = new EventConsumer(messagingTemplate);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void userActivity_shouldAddEventToLoggedEventsAndSendToWebSocket() {
        UUID userId = UUID.randomUUID();
        UserActivityEvent event = new UserActivityEvent(userId, ActivityType.LOGIN, "User logged in");
        Consumer<UserActivityEvent> consumer = eventConsumer.userActivity();

        consumer.accept(event);

        assertThat(eventConsumer.getLoggedEvents()).hasSize(1);
        assertThat(eventConsumer.getLoggedEvents().getFirst()).isEqualTo(event);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/activities"), eq(event));
    }

    @Test
    void getLoggedEvents_shouldReturnEventsInReverseOrder() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UserActivityEvent event1 = new UserActivityEvent(userId1, ActivityType.LOGIN, "Event 1");
        UserActivityEvent event2 = new UserActivityEvent(userId2, ActivityType.LOGOUT, "Event 2");
        event2.setTimestamp(LocalDateTime.now().plusSeconds(1));

        Consumer<UserActivityEvent> consumer = eventConsumer.userActivity();
        consumer.accept(event1);
        consumer.accept(event2);

        List<UserActivityEvent> events = eventConsumer.getLoggedEvents();

        assertThat(events).hasSize(2);
        assertThat(events.get(0)).isEqualTo(event2);
        assertThat(events.get(1)).isEqualTo(event1);
    }

    @Test
    void userActivity_shouldLimitNumberOfLoggedEvents() {
        int maxEvents = 50;
        for (int i = 0; i < maxEvents + 5; i++) {
            eventConsumer.userActivity().accept(new UserActivityEvent(UUID.randomUUID(), ActivityType.VIEW_PAGE, "Page " + i));
        }

        List<UserActivityEvent> events = eventConsumer.getLoggedEvents();

        assertThat(events).hasSize(maxEvents);
        assertThat(events.get(maxEvents - 1).getDetails()).contains("Page 5");
    }

    @Test
    void getEventsByUserId_shouldReturnFilteredEvents() {
        UUID targetUserId = UUID.randomUUID();
        UserActivityEvent event1 = new UserActivityEvent(targetUserId, ActivityType.LOGIN, "Event for target user 1");
        UserActivityEvent event2 = new UserActivityEvent(UUID.randomUUID(), ActivityType.LOGOUT, "Event for other user");
        UserActivityEvent event3 = new UserActivityEvent(targetUserId, ActivityType.VIEW_PAGE, "Event for target user 2");
        event3.setTimestamp(LocalDateTime.now().plusSeconds(1));

        Consumer<UserActivityEvent> consumer = eventConsumer.userActivity();
        consumer.accept(event1);
        consumer.accept(event2);
        consumer.accept(event3);

        List<UserActivityEvent> filteredEvents = eventConsumer.getEventsByUserId(targetUserId.toString());

        assertThat(filteredEvents).hasSize(2);
        assertThat(filteredEvents.get(0)).isEqualTo(event3);
        assertThat(filteredEvents.get(1)).isEqualTo(event1);
        assertThat(filteredEvents).doesNotContain(event2);
    }

    @Test
    void getEventsByUserId_shouldReturnEmptyListForNonExistentUser() {
        UUID userId = UUID.randomUUID();
        eventConsumer.userActivity().accept(new UserActivityEvent(userId, ActivityType.LOGIN, "Some event"));

        List<UserActivityEvent> filteredEvents = eventConsumer.getEventsByUserId(UUID.randomUUID().toString());

        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void getEventsByUserId_shouldReturnEmptyListForInvalidUuidFormat() {
        UUID userId = UUID.randomUUID();
        eventConsumer.userActivity().accept(new UserActivityEvent(userId, ActivityType.LOGIN, "Some event"));

        List<UserActivityEvent> filteredEvents = eventConsumer.getEventsByUserId("invalid-uuid-string");

        assertThat(filteredEvents).isEmpty();
    }

    @Test
    void getEventsByUserId_shouldReturnEmptyListForNullOrEmptyUserId() {
        UUID userId = UUID.randomUUID();
        eventConsumer.userActivity().accept(new UserActivityEvent(userId, ActivityType.LOGIN, "Some event"));

        List<UserActivityEvent> filteredEventsNull = eventConsumer.getEventsByUserId(null);
        List<UserActivityEvent> filteredEventsEmpty = eventConsumer.getEventsByUserId("");
        List<UserActivityEvent> filteredEventsBlank = eventConsumer.getEventsByUserId("   ");

        assertThat(filteredEventsNull).isEmpty();
        assertThat(filteredEventsEmpty).isEmpty();
        assertThat(filteredEventsBlank).isEmpty();
    }
}