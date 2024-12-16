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
@Table(name = "notify")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notify_id")
    String id;
    String content;
    @Column(name = "user_id")
    String userId;
    @Column(name = "recipient_id")
    String senderId;
    String postId;
    Date createAt;
    boolean isRead;
    String type;
}
