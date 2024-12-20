package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Message;
import com.example.Social_Network.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findAllBySenderIdAndRecipientId(String userId, String recipientId, Pageable pageable);

    Page<Message> findAllByConversationId(String conversationId, Pageable pageable);
}
