package org.example.activitylogger.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.activitylogger.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityEvent {
    private UUID id;
    private UUID userId;
    private ActivityType activityType;
    private String details;
    private LocalDateTime timestamp;

    public UserActivityEvent(UUID userId, ActivityType activityType, String details) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.activityType = activityType;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}