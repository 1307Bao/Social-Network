package com.example.Social_Network.Embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFollowingId {
    @Column(name = "user_id")
    String userId;
    @Column(name = "following_id")
    String followingId;
}
