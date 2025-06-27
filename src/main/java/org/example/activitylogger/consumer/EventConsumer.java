package org.example.activitylogger.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.activitylogger.model.UserActivityEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EventConsumer {

    private final ConcurrentLinkedQueue<UserActivityEvent> loggedEvents = new ConcurrentLinkedQueue<>();
    private static final int MAX_EVENTS_IN_MEMORY = 50;

    @Bean
    public Consumer<UserActivityEvent> userActivity() {
        return event -> {
            log.info("Received event: {}", event);
            loggedEvents.add(event);
            if (loggedEvents.size() > MAX_EVENTS_IN_MEMORY) {
                loggedEvents.poll();
            }
        };
    }

    public List<UserActivityEvent> getLoggedEvents() {
        List<UserActivityEvent> eventsList = new ArrayList<>(loggedEvents);
        Collections.reverse(eventsList);
        return eventsList;
    }

    public List<UserActivityEvent> getEventsByUserId(String userIdString) {
        if (userIdString == null || userIdString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        UUID searchUuid;
        try {
            searchUuid = UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format: {}", userIdString);
            return Collections.emptyList();
        }

        List<UserActivityEvent> filteredEvents = loggedEvents.stream()
                .filter(event -> searchUuid.equals(event.getUserId()))
                .collect(Collectors.toList());
        Collections.reverse(filteredEvents);
        return filteredEvents;
    }
}
