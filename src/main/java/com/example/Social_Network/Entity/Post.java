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
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "post_id")
    String post_id;
    @Column(name = "content")
    String content;
    @Column(name = "user_id")
    String user_id;
    Date createAt;
    @Column(name = "image")
    String image;
}
