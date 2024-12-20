package com.example.Social_Network.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String userId;
    String username;
    String userAvt;
    String lastMessage;
    long lastTimeMessage;
    boolean isRead;
}
