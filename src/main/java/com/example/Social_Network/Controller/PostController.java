package com.example.Social_Network.Controller;

import com.example.Social_Network.DTO.Request.ApiResponse;
import com.example.Social_Network.DTO.Request.CreatePostRequest;
import com.example.Social_Network.DTO.Response.PostOverviewResponse;
import com.example.Social_Network.DTO.Response.PostProfileResponse;
import com.example.Social_Network.DTO.Response.PostResponse;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/posts")
public class PostController {
    PostService postService;

    @PostMapping("/create")
    ApiResponse<Void> uploadPost(@ModelAttribute CreatePostRequest request) throws GeneralSecurityException, IOException {
        postService.uploadPost(request);
        return ApiResponse.<Void>builder().build();
    }

    @PatchMapping("/delete")
    ApiResponse<Void> deletePost(@RequestParam String postId) throws AppRuntimeException, GeneralSecurityException, IOException {
        postService.deletePost(postId);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<Void> likePost(@PathVariable String postId) throws AppRuntimeException {
        postService.likePost(postId);

        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/{postId}/comment")
    public ApiResponse<Void> commentPost(@PathVariable String postId, @RequestBody String content) throws AppRuntimeException {
        postService.commentPost(postId, content);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/")
    public ApiResponse<List<PostOverviewResponse>> getPosts(@RequestParam int offset,
                                                            @RequestParam int limit) {
        List<PostOverviewResponse> result = postService.getPosts(offset, limit);
        return ApiResponse.<List<PostOverviewResponse>>builder().result(result).build();
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable String postId) throws AppRuntimeException {
        PostResponse result = postService.getPost(postId);
        return ApiResponse.<PostResponse>builder().result(result).build();
    }

    @GetMapping("/explore")
    public ApiResponse<List<PostProfileResponse>> getExplorePosts(@RequestParam int limit, @RequestParam int offset) {
        List<PostProfileResponse> result = postService.getExplorePosts(limit, offset);
        return ApiResponse.<List<PostProfileResponse>>builder().result(result).build();
    }
}
