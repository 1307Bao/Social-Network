package com.example.Social_Network.Embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostLikeId implements Serializable {
    @Column(name = "post_id")
    String postId;
    @Column(name = "user_id")
    String userId;
}
