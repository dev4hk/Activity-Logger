package org.example.activitylogger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.activitylogger.model.UserActivityEvent;
import org.example.activitylogger.producer.EventProducer;
import org.example.activitylogger.consumer.EventConsumer;
import org.example.activitylogger.enums.ActivityType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventProducer eventProducer;
    private final EventConsumer eventConsumer;
    private final Random random = new Random();

    private static final ActivityType[] ACTIVITY_TYPES_ENUMS = ActivityType.values();
    private static final String[] DETAILS = {"Homepage", "ProductX", "CategoryY", "Item123", "Order456", "ShippingAddress"};

    @PostMapping("/publish")
    public String publishRandomEvent() {
        UUID userId = UUID.randomUUID();
        ActivityType activityType = ACTIVITY_TYPES_ENUMS[random.nextInt(ACTIVITY_TYPES_ENUMS.length)];
        String details = DETAILS[random.nextInt(DETAILS.length)];

        UserActivityEvent event = new UserActivityEvent(userId, activityType, details);
        eventProducer.publishUserActivity(event);
        log.info("Event published: {}", event);
        return "Event published: " + event.getActivityType() + " by " + event.getUserId();
    }

    @GetMapping("/dashboard")
    public List<UserActivityEvent> getEvents() {
        log.info("Fetching all events");
        return eventConsumer.getLoggedEvents();
    }

    @GetMapping("/by-user")
    public List<UserActivityEvent> getEventsByUserId(@RequestParam String userId) {
       log.info("Fetching events for user: {}", userId);
        return eventConsumer.getEventsByUserId(userId);
    }
}