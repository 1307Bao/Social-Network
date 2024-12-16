package com.example.Social_Network.Message;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagePayload {
    MessageType type;
    String sender;
    String message;
    String postId;
}
