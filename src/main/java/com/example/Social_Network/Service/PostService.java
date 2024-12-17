package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Request.CreatePostRequest;
import com.example.Social_Network.DTO.Response.CommentInPostResponse;
import com.example.Social_Network.DTO.Response.PostProfileResponse;
import com.example.Social_Network.DTO.Response.PostOverviewResponse;
import com.example.Social_Network.DTO.Response.PostResponse;
import com.example.Social_Network.Embeddable.PostCommentId;
import com.example.Social_Network.Embeddable.PostLikeId;
import com.example.Social_Network.Embeddable.UserFollowingId;
import com.example.Social_Network.Entity.*;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Exception.ErrorCode;
import com.example.Social_Network.Message.MessagePayload;
import com.example.Social_Network.Message.MessageType;
import com.example.Social_Network.Repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostLikeRepository postLikeRepository;
    PostCommentRepository postCommentRepository;
    UserRepository userRepository;
    UserFollowingRepository userFollowingRepository;
    NotifyRepository notifyRepository;
    UploadImageService uploadImageService;
    SimpMessagingTemplate simpMessagingTemplate;

    @PreAuthorize("hasRole('USER')")
    public void uploadPost(CreatePostRequest request) throws GeneralSecurityException, IOException {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        log.error("1 POST POSTED");

        String imgUrl = uploadImageService.getUrlAfterUploaded(request.getFile());
        log.error("POST POSTED");
        Post post = Post.builder().content(request.getContent())
                .image(imgUrl)
                .createAt(new Date())
                .user_id(userId).build();

        postRepository.save(post);
    }

    @PreAuthorize("hasRole('USER')")
    public void deletePost(String postId) throws AppRuntimeException, GeneralSecurityException, IOException {
        Post post = postRepository.findById(postId).orElseThrow(
                ()-> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();
        String fileId = post.getImage();

        if (post.getUser_id().equals(userId)) {
            postRepository.deleteById(postId);
            uploadImageService.deleteFile(fileId);
        } else {
            throw new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    private String getUserId() {
        var context = SecurityContextHolder.getContext();
        return context.getAuthentication().getName();
    }

    public void likePost(String postId) throws AppRuntimeException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        String userId = getUserId();

        boolean isLike = postLikeRepository.existsByPostLikeId_PostIdAndPostLikeId_UserId(postId, userId);
        if (isLike) {
            postLikeRepository.deleteById(PostLikeId.builder()
                    .postId(postId)
                    .userId(userId)
                    .build());
            User user = userRepository.findById(post.getUser_id()).orElseThrow(
                    ()-> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );
            Notify notify = notifyRepository.findByUserIdAndSenderId(user.getUser_id(), userId);
            notifyRepository.deleteById(notify.getId());
        } else {
            PostLike postLike = PostLike.builder()
                    .postLikeId(new PostLikeId(postId, userId)).build();
            postLikeRepository.save(postLike);
            String username = userRepository.getUsername(userId);
            String ownerPostId = post.getUser_id();
            String message = "User " + username + " liked your post!";

            Notify notify = Notify.builder().userId(ownerPostId)
                    .senderId(userId)
                    .content(message)
                    .createAt(new Date())
                    .type("like")
                    .postId(postId)
                    .isRead(false).build();

            notifyRepository.save(notify);

            MessagePayload payload =  MessagePayload.builder()
                    .message(message)
                    .sender(userId)
                    .postId(postId)
                    .type(MessageType.LIKE)
                    .build();

            simpMessagingTemplate.convertAndSendToUser(ownerPostId, "/queue/messages", payload);
        }
    }

    public void commentPost(String postId, String content) throws AppRuntimeException {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        String userId = getUserId();
        String ownerPostId = post.getUser_id();
        String username = userRepository.getUsername(userId);

        PostComment postComment = PostComment.builder()
                .postCommentId(new PostCommentId(postId, userId))
                .content(content)
                .createAt(new Date())
                .build();

        postCommentRepository.save(postComment);

        String message = username + "commented " + content;
        MessagePayload payload = MessagePayload.builder()
                .message(message)
                .postId(postId)
                .type(MessageType.COMMENT)
                .sender(userId)
                .build();

        Notify notify = Notify.builder().userId(ownerPostId)
                .senderId(userId)
                .content(message)
                .createAt(new Date())
                .type("comment")
                .postId(postId)
                .isRead(false).build();

        notifyRepository.save(notify);


        simpMessagingTemplate.convertAndSendToUser(ownerPostId, "/queue/messages", payload);
    }

    public List<PostOverviewResponse> getPosts(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        String userId = getUserId();
        Page<Post> page = postRepository.getAllPost(userId, pageable);

        return page.getContent().stream()
                .map(post -> PostOverviewResponse.builder()
                        .postId(post.getPost_id())
                        .postImg(post.getImage())
                        .username(userRepository.getUsername(post.getUser_id()))
                        .postOwnerAvt(userRepository.getImage(post.getUser_id()))
                        .numberOfComment(postCommentRepository.getNumberOfComment(post.getPost_id()))
                        .numberOfLike(postLikeRepository.getNumberOfLike(post.getPost_id()))
                        .postOwnerId(post.getUser_id())
                        .isLike(postLikeRepository.existsById(new PostLikeId(post.getPost_id(), userId)))
                        .build()).toList();
    }

    public List<PostProfileResponse> getAllPostOfUser(String userId) {
        List<Post> posts = postRepository.getAllPostOfUser(userId);
        return posts.stream().map(post -> PostProfileResponse.builder()
                .postId(post.getPost_id())
                .numberOfComment(postCommentRepository.getNumberOfComment(post.getPost_id()))
                .numberOfLike(postLikeRepository.getNumberOfLike(post.getPost_id()))
                .postImg(post.getImage()).build()).toList();
    }

    public PostResponse getPost(String postId) throws AppRuntimeException {
        List<PostComment> postComments = postCommentRepository.getCommentInPost(postId);
        List<CommentInPostResponse> commentInPostResponses = postComments.stream().map(
                postComment -> CommentInPostResponse.builder()
                        .avt(userRepository.getImage(postComment.getPostCommentId().getUserId()))
                        .username(userRepository.getUsername(postComment.getPostCommentId().getUserId()))
                        .userId(postComment.getPostCommentId().getUserId())
                        .content(postComment.getContent()).build()).toList();
        Post post = postRepository.findById(postId).orElseThrow(() -> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        String currentUserId = getUserId();
        String ownerPostUserId = post.getUser_id();
        int numberOfLike = postLikeRepository.getNumberOfLike(postId);
        boolean isLike = postLikeRepository.existsById(new PostLikeId(postId, currentUserId));

        boolean isFollow = userFollowingRepository.existsById(UserFollowingId.builder().userId(currentUserId)
                .followingId(ownerPostUserId).build());

        return PostResponse.builder()
                .postImg(post.getImage())
                .numberOfLike(numberOfLike)
                .caption(post.getContent())
                .username(userRepository.getUsername(post.getUser_id()))
                .isFollow(isFollow)
                .commentInPostResponseList(commentInPostResponses)
                .userId(post.getUser_id())
                .isLike(isLike)
                .userAvt(userRepository.getImage(post.getUser_id())).build();
    }

    public List<PostProfileResponse> getExplorePosts(long seed, int limit, int offset) {
        String userID = getUserId();
        List<Post> explorePosts = postRepository.getExplorePosts(userID, seed, limit, offset);

        return explorePosts.stream().map(
                    explorePost -> PostProfileResponse.builder()
                            .postImg(explorePost.getImage())
                            .postId(explorePost.getPost_id())
                            .numberOfComment(postCommentRepository.getNumberOfComment(explorePost.getPost_id()))
                            .numberOfLike(postLikeRepository.getNumberOfLike(explorePost.getPost_id()))
                            .build()
                ).toList();
    }
}
