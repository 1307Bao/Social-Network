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
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String message_id;
    @Column(name = "conversation_id")
    String conversationId;
    @Column(name = "sender_id")
    String senderId;
    @Column(name = "recipient_id")
    String recipientId;
    @Column(name = "text")
    String text;
    Date createAt;
}
