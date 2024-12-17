package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Request.UpdateUserRequest;
import com.example.Social_Network.DTO.Response.*;
import com.example.Social_Network.Embeddable.UserFollowingId;
import com.example.Social_Network.Entity.Notify;
import com.example.Social_Network.Entity.User;
import com.example.Social_Network.Entity.UserFollowing;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Exception.ErrorCode;
import com.example.Social_Network.Mapper.UserMapper;
import com.example.Social_Network.Message.MessagePayload;
import com.example.Social_Network.Message.MessageType;
import com.example.Social_Network.Repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserFollowingRepository userFollowingRepository;
    PostService postService;
    UserMapper userMapper;
    SimpMessagingTemplate simpMessagingTemplate;
    NotifyRepository notifyRepository;
    HandleUploadImageService handleUploadImageService;

    private String currentId() {
        var context = SecurityContextHolder.getContext();
        return context.getAuthentication().getName();
    }

    @PreAuthorize("hasRole('USER')")
    public ProfileResponse getMyInfo() throws AppRuntimeException {
        User user = userRepository.findById(currentId()).orElseThrow(
                () -> new AppRuntimeException(ErrorCode.USER_NOT_EXIST)
        );

        return userMapper.toUserResponse(user);
    }

    public void follow(String userId) {
        String currentUserId = currentId();
        UserFollowing userFollowing = UserFollowing.builder()
                .userFollowingId(new UserFollowingId(currentUserId, userId)).build();

        String username = userRepository.getUsername(currentUserId);
        String message = username + " followed you";

        Notify notify = Notify.builder().content(message).userId(userId).senderId(currentUserId).createAt(new Date())
                        .isRead(false).type("Follow").build();

        notifyRepository.save(notify);
        userFollowingRepository.save(userFollowing);

        MessagePayload payload = MessagePayload.builder().message(message).sender(currentUserId)
                        .type(MessageType.FOLLOW).build();

        simpMessagingTemplate.convertAndSendToUser(userId
                , "/queue/messages", payload);
    }

    public void unfollow(String userId) {
        log.error("UNFOLLOW");
        String currentUserId = currentId();
        UserFollowingId id = new UserFollowingId(currentUserId, userId);

        userFollowingRepository.deleteById(id);
    }

    public void updateUser(UpdateUserRequest request) throws AppRuntimeException, GeneralSecurityException, IOException {
        String userId = currentId();
        if (userRepository.existsByUsername(request.getUsername()) && !userRepository.getUsername(userId).equals(request.getUsername())) {
            throw new AppRuntimeException(ErrorCode.USER_EXISTED);
        }
        User user = userRepository.findById(userId).orElseThrow(()-> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        if (request.getAvatar() != null) {
            String avtUrl = handleUploadImageService.uploadFile(request.getAvatar());
            user.setAvatar(avtUrl);
        }
        user.setBio(request.getBio());
        user.setGender(request.getGender());
        user.setFullname(user.getFullname());
        user.setUsername(request.getUsername());

        userRepository.save(user);
    }

    public List<UserSearchResponse> searchUser(String username) {
        String currentId = currentId();
        List<User> users = userRepository.findByUsername(username, currentId);
        return users.stream().map(
                user -> UserSearchResponse.builder()
                        .userId(user.getUser_id())
                        .avt(user.getAvatar())
                        .username(user.getUsername())
                        .fullname(user.getFullname())
                        .build()
        ).toList();
    }

    public UserResponse getUserProfile(String userId) throws AppRuntimeException {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        String currentUserId = currentId();

        boolean isFollow = userFollowingRepository.existsById(new UserFollowingId(currentUserId, userId));
        List<PostProfileResponse> postProfileResponses = postService.getAllPostOfUser(userId);

        int numberOfFollower = userFollowingRepository.numberOfFollower(userId);
        int numberOfFollowing = userFollowingRepository.numberOfFollowing(userId);
        int numberOfPost = postProfileResponses.size();

        return UserResponse.builder()
                .userId(userId)
                .bio(user.getBio())
                .isFollow(isFollow)
                .username(user.getUsername())
                .fullname(user.getFullname())
                .avt(user.getAvatar())
                .numberOfFollower(numberOfFollower)
                .numberOfFollowing(numberOfFollowing)
                .numberOfPost(numberOfPost)
                .postProfileResponses(postProfileResponses)
                .bio(user.getBio())
                .build();
    }

    public List<FollowResponse> myFollowers() {
        String userId = currentId();
        if (userFollowingRepository.numberOfFollower(userId) == 0) {
            return new ArrayList<>();
        }
        List<Object[]> userFollowers = userFollowingRepository.myFollowers(userId);
        List<FollowResponse> responses = new ArrayList<>();
        for (Object[] object : userFollowers) {
            FollowResponse response = FollowResponse.builder()
                    .userId((String) object[0])
                    .avatar((String) object[1])
                    .username((String) object[2])
                    .fullname((String) object[3])
                    .build();

            responses.add(response);
        }

        return responses;
    }

    public List<FollowResponse> myFollowing() {
        String userId = currentId();
        if (userFollowingRepository.numberOfFollowing(userId) == 0) {
            return new ArrayList<>();
        }
        List<Object[]> userFollowers = userFollowingRepository.myFollowing(userId);
        List<FollowResponse> responses = new ArrayList<>();
        for (Object[] object : userFollowers) {
            FollowResponse response = FollowResponse.builder()
                    .userId((String) object[0])
                    .avatar((String) object[1])
                    .username((String) object[2])
                    .fullname((String) object[3])
                    .build();

            responses.add(response);
        }

        return responses;
    }
}
