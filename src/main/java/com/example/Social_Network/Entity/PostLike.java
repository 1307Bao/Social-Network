package com.example.Social_Network.Entity;

import com.example.Social_Network.Embeddable.PostLikeId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_like")
public class PostLike {
    @EmbeddedId
    PostLikeId postLikeId;
}
