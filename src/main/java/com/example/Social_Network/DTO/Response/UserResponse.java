package com.example.Social_Network.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String userId;
    String bio;
    String avt;
    boolean isFollow;
    int numberOfPost;
    int numberOfFollower;
    int numberOfFollowing;
    String username;
    String fullname;
    List<PostProfileResponse> postProfileResponses;
}
