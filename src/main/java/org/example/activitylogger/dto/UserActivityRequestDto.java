package org.example.activitylogger.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.activitylogger.enums.ActivityType;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityRequestDto {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Activity type cannot be null")
    private ActivityType activityType;

    @NotNull(message = "Details cannot be null")
    @Size(min = 1, max = 255, message = "Details must be between 1 and 255 characters")
    private String details;
}