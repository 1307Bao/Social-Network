package com.example.Social_Network.Entity;

import com.example.Social_Network.Embeddable.UserFollowingId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_following")
public class UserFollowing {
    @EmbeddedId
    UserFollowingId userFollowingId;
}
