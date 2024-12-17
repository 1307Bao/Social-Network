package com.example.Social_Network.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// Watch post in home page
public class PostOverviewResponse {
    String postId;
    String postOwnerId;
    String username;
    String postOwnerAvt;
    String content;
    String postImg;
    int numberOfLike;
    int numberOfComment;
    boolean isLike;
    Date createAt;
}
