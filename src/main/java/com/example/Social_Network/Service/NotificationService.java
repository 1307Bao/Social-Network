package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Response.NotifyResponse;
import com.example.Social_Network.Entity.Notify;
import com.example.Social_Network.Repository.NotifyRepository;
import com.example.Social_Network.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotifyRepository notifyRepository;
    UserRepository userRepository;

    public List<NotifyResponse> getAllNotification(int limit, int offset) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(limit, offset);
        Page<Notify> notifies = notifyRepository.findAllByUserId(pageable, userId);

        return notifies.getContent().stream().map(
                notify -> NotifyResponse.builder()
                        .isRead(notify.isRead())
                        .avtUrl(userRepository.getImage(notify.getSenderId()))
                        .destinationId((notify.getType().equals("follow")) ? notify.getSenderId() : notify.getPostId())
                        .message(notify.getContent())
                        .build()
        ).toList();
    }
}
