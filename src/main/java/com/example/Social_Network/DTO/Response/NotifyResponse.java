package com.example.Social_Network.DTO.Response;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotifyResponse {
    String avtUrl;
    String destinationId;
    String type;
    String message;
    boolean isRead;
}
