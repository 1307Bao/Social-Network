package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Notify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotifyRepository extends JpaRepository<Notify, String> {
    Notify findByUserIdAndSenderId(String userId, String senderId);
    Page<Notify> findAllByUserId(Pageable pageable, String userId);

    @Query(value = """
            SELECT * from notify 
            where user_id = :userId and postId = :postId
            and type = 'like'
            """, nativeQuery = true)
    Notify getNotificationsAboutLikePost(@Param("userId") String userId, @Param("postId") String postId);
    @Query(value = """
            SELECT * from notify 
            where user_id = :userId and postId = :postId
            and type = 'comment'
            """, nativeQuery = true)
    Notify getNotificationsAboutCommentPost(@Param("userId") String userId, @Param("postId") String postId);


}
