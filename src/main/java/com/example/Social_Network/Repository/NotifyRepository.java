package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Notify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, String> {
    Notify findByUserIdAndSenderId(String userId, String senderId);
    Page<Notify> findAllByUserId(Pageable pageable, String userId);
}
