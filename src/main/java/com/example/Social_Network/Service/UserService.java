package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Request.UpdateUserRequest;
import com.example.Social_Network.DTO.Response.PostProfileResponse;
import com.example.Social_Network.DTO.Response.ProfileResponse;
import com.example.Social_Network.DTO.Response.UserResponse;
import com.example.Social_Network.DTO.Response.UserSearchResponse;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

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
    UploadImageService uploadImageService;

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
        String currentUserId = currentId();
        UserFollowingId id = new UserFollowingId(currentUserId, userId);

        userFollowingRepository.deleteById(id);
    }

    public void updateUser(UpdateUserRequest request) throws AppRuntimeException, GeneralSecurityException, IOException {
        String userId = currentId();
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppRuntimeException(ErrorCode.USER_EXISTED);
        }
        User user = userRepository.findById(userId).orElseThrow(()-> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        String avtUrl = uploadImageService.getUrlAfterUploaded(request.getAvatar());
        user.setAvatar(avtUrl);
        user.setBio(request.getBio());
        user.setGender(request.getGender());
        user.setFullname(user.getFullname());
        user.setUsername(request.getUsername());

        userRepository.save(user);
    }

    public List<UserSearchResponse> searchUser(String username) {
        List<User> users = userRepository.findByUsername(username);
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
                .fullname(user.getFullname())
                .avt(user.getAvatar())
                .numberOfFollower(numberOfFollower)
                .numberOfFollowing(numberOfFollowing)
                .numberOfPost(numberOfPost)
                .postProfileResponses(postProfileResponses)
                .build();
    }
}
