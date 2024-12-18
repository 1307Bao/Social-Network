package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, String> {
    @Query(value = """
            SELECT * FROM conversation where (user_id = :userId and recipient_id = :recipientId)
                OR  (user_id = :recipientId and recipient_id = :userId)
            """, nativeQuery = true)
    Conversation findBySenderIdAndRecipientId(@Param("userId") String senderId, @Param("recipientId") String recipientId);

    List<Conversation> findBySenderId(String userId);

    @Query(value = """
            SELECT COUNT(*) FROM conversation where (user_id = :userId and recipient_id = :recipientId)
                OR  (user_id = :recipientId and recipient_id = :userId)
            """, nativeQuery = true)
    int numberOfMessage(@Param("userId") String userId, @Param("recipientId") String recipientId);

    List<Conversation> findByRecipientId(String userId);

    @Query(value = """
           SELECT * FROM conversation where user_id = :userId or recipient_id = :userId
           order by last_message_time desc
            """, nativeQuery = true)
    List<Conversation> findByRecipientIdOrSenderId(@Param("userId") String userId);
}
