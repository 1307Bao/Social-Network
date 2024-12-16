package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    Optional<Conversation> findBySenderIdAndRecipientId(String senderId, String recipientId);

    List<Conversation> findBySenderId(String userId);
}
