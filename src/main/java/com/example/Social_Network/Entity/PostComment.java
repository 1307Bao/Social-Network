package com.example.Social_Network.Entity;

import com.example.Social_Network.Embeddable.PostCommentId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "post_comment")
public class PostComment {
    @EmbeddedId
    PostCommentId postCommentId;
    String content;
    Date createAt;
}
