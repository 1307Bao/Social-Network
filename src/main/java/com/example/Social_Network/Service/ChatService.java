package com.example.Social_Network.Service;

import com.example.Social_Network.DTO.Response.ChatMessageResponse;
import com.example.Social_Network.DTO.Response.ConversationResponse;
import com.example.Social_Network.Entity.Conversation;
import com.example.Social_Network.Entity.Message;
import com.example.Social_Network.Exception.AppRuntimeException;
import com.example.Social_Network.Exception.ErrorCode;
import com.example.Social_Network.Mapper.MessageMapper;
import com.example.Social_Network.Message.MessagePayload;
import com.example.Social_Network.Message.MessageType;
import com.example.Social_Network.Repository.ConversationRepository;
import com.example.Social_Network.Repository.MessageRepository;
import com.example.Social_Network.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    MessageRepository messageRepository;
    ConversationRepository conversationRepository;
    MessageMapper messageMapper;
    UserRepository userRepository;
    SimpMessagingTemplate simpMessagingTemplate;

    private String getCurrentUserId() {
        var context = SecurityContextHolder.getContext();
        return context.getAuthentication().getName();
    }

    public ChatMessageResponse sendMessage(String recipientId, String content) {
        String senderId = getCurrentUserId();

        Conversation conversation = conversationRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .orElse(new Conversation());
        log.error("USERID: " + senderId);
        log.error("RECIPIENTID: " + senderId);

        conversation.setLast_message(content);
        conversation.setLast_message_time(new Date());
        conversation.setRecipientId(recipientId);
        conversation.setSenderId(senderId);
        conversationRepository.save(conversation);

        Message messageResponse = Message.builder()
                .recipientId(recipientId)
                .conversationId(conversation.getConversation_id())
                .senderId(senderId)
                .text(content)
                .createAt(new Date())
                .build();
        messageRepository.save(messageResponse);

        MessagePayload payload = MessagePayload.builder()
                .sender(senderId)
                .type(MessageType.CHAT)
                .message(content)
                .build();
        simpMessagingTemplate.convertAndSendToUser(recipientId, "/queue/messages", payload);

        return messageMapper.toChatMessageResponse(messageResponse);
    }

    public List<ConversationResponse> getAllConversation() {
        String userId = getCurrentUserId();
        List<Conversation> conversations = conversationRepository.findByRecipientId(userId);

        return conversations.stream().map(
                conversation -> ConversationResponse.builder()
                        .userId(conversation.getSenderId())
                        .username(userRepository.getUsername(conversation.getRecipientId()))
                        .userAvt(userRepository.getImage(conversation.getRecipientId()))
                        .lastMessage(conversation.getLast_message())
                        .isRead(false)
                        .lastTimeMessage(calculateDateDifference(conversation.getLast_message_time(), new Date()))
                        .build()).toList();
    }

    public List<ChatMessageResponse> getMessageWithUser(String recipientId, int offset, int limit) throws AppRuntimeException {
        Pageable pageable = PageRequest.of(offset, limit);
        String userId = getCurrentUserId();
        if (!conversationRepository.existsBySenderIdAndRecipientId(userId, recipientId)) {
            return new ArrayList<>();
        }
        Conversation conversation = conversationRepository.findBySenderIdAndRecipientId(userId, recipientId)
                .orElseThrow(() -> new AppRuntimeException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        Page<Message> messages = messageRepository.findAllByConversationId(conversation.getConversation_id(), pageable);

        conversation.setRead(true);
        conversationRepository.save(conversation);

        return messages.getContent().stream().map(
                message -> ChatMessageResponse.builder()
                        .sender_id(message.getSenderId())
                        .recipient_id(message.getRecipientId())
                        .text(message.getText())
                        .createAt(message.getCreateAt())
                        .build()
        ).toList();
    }

    public long calculateDateDifference(Date startDate, Date endDate) {
        long diffInMillies = endDate.getTime() - startDate.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies);

        if (diffInDays < 7) {
            return diffInDays ;
        } else {
            long diffInWeeks = diffInDays / 7;
            return diffInWeeks;
        }
    }
}
