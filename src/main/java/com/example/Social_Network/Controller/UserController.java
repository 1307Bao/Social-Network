package com.example.Social_Network.Controller;

import com.example.Social_Network.DTO.Request.ApiResponse;
import com.example.Social_Network.DTO.Request.UpdateUserRequest;
import com.example.Social_Network.DTO.Response.ProfileResponse;
import com.example.Social_Network.DTO.Response.UserResponse;
import com.example.Social_Network.DTO.Response.UserSearchResponse;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/my-info")
    ApiResponse<ProfileResponse> getMyInfo() throws AppRuntimeException {
        ProfileResponse result = userService.getMyInfo();

        return ApiResponse.<ProfileResponse>builder().result(result).build();
    }

    @PostMapping("/follow/{userId}")
    void follow(@PathVariable String userId) {
        userService.follow(userId);
    }

    @PatchMapping("/unfollow/{userId}")
    void unfollow(@PathVariable String userId) {
        userService.unfollow(userId);
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserProfile(@PathVariable("userId") String userId) throws AppRuntimeException {
        UserResponse result = userService.getUserProfile(userId);
        return ApiResponse.<UserResponse>builder().result(result).build();
    }

    @PutMapping("/update")
    void updateCurrentUser(@ModelAttribute UpdateUserRequest request)
            throws AppRuntimeException, GeneralSecurityException, IOException {
        userService.updateUser(request);
    }

    @GetMapping("/search")
    ApiResponse<List<UserSearchResponse>> searchUser(@RequestParam String username) {
        List<UserSearchResponse> result = userService.searchUser(username);
        return ApiResponse.<List<UserSearchResponse>>builder().result(result).build();
    }
}
