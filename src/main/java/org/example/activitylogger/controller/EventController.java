package org.example.activitylogger.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.activitylogger.consumer.EventConsumer;
import org.example.activitylogger.dto.UserActivityRequestDto;
import org.example.activitylogger.model.UserActivityEvent;
import org.example.activitylogger.producer.EventProducer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventProducer eventProducer;
    private final EventConsumer eventConsumer;

    @PostMapping("/publish")
    public String publishEvent(@Valid @RequestBody UserActivityRequestDto requestDto) {
        UserActivityEvent event = new UserActivityEvent(
                requestDto.getUserId(),
                requestDto.getActivityType(),
                requestDto.getDetails()
        );

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
    public List<UserActivityEvent> getEventsByUserId(@NotNull(message = "User ID cannot be null") @RequestParam String userId) {
        log.info("Fetching events for user: {}", userId);
        return eventConsumer.getEventsByUserId(userId);
    }
}