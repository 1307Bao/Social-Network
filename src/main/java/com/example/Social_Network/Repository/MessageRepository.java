package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    Page<Message> findAllBySenderIdAndRecipientId(String userId, String recipientId, Pageable pageable);
}
