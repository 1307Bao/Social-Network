package com.example.Social_Network.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// Watch post when holder in post, to see like, cmt
public class PostProfileResponse {
    String postId;
    int numberOfLike;
    int numberOfComment;
    String postImg;
}
