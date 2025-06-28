package org.example.activitylogger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.activitylogger.enums.ActivityType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityRequestDto {
    private UUID userId;
    private ActivityType activityType;
    private String details;
}
