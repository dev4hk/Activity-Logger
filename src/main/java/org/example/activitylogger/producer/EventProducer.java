package org.example.activitylogger.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.activitylogger.model.UserActivityEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducer {

    private final StreamBridge streamBridge;

    public void publishUserActivity(UserActivityEvent event) {
        streamBridge.send("userActivity-out-0", event);
        log.info("Published event: {}", event);
    }
}