package com.example.Social_Network.Repository;

import com.example.Social_Network.Embeddable.UserFollowingId;
import com.example.Social_Network.Entity.User;
import com.example.Social_Network.Entity.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFollowingRepository extends JpaRepository<UserFollowing, UserFollowingId> {
    @Query(value = "SELECT COUNT(*) FROM user_following WHERE user_id = :user_id", nativeQuery = true)
    int numberOfFollowing(@Param("user_id") String user_id);

    @Query(value = "SELECT COUNT(*) FROM user_following WHERE following_id = :user_id", nativeQuery = true)
    int numberOfFollower(@Param("user_id") String user_id);

    @Query(value = """
            SELECT u.user_id, u.avatar, u.username, u.fullname
            FROM user_following UF 
            JOIN user u ON UF.user_id = u.user_id 
            WHERE UF.following_id = :userId
            """, nativeQuery = true)
    List<Object[]> myFollowers(@Param("userId") String userId);

    @Query(value = """
            SELECT UF.following_id, u.avatar, u.username, u.fullname
            FROM user_following UF 
            JOIN user u ON UF.user_id = u.user_id 
            WHERE UF.user_id = :userId
            """, nativeQuery = true)
    List<Object[]> myFollowing(@Param("userId") String userId);
}
