package com.example.Social_Network.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String conversation_id;
    @Column(name = "user_id")
    String senderId;
    @Column(name = "recipient_id")
    String recipientId;
    String last_message;
    Date last_message_time;
    boolean isRead;
}
