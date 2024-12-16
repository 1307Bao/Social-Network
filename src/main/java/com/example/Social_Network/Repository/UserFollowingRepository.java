package com.example.Social_Network.Repository;

import com.example.Social_Network.Embeddable.UserFollowingId;
import com.example.Social_Network.Entity.UserFollowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFollowingRepository extends JpaRepository<UserFollowing, UserFollowingId> {
    @Query(value = "SELECT COUNT(*) FROM user_following WHERE user_id = :user_id", nativeQuery = true)
    int numberOfFollowing(@Param("user_id") String user_id);

    @Query(value = "SELECT COUNT(*) FROM user_following WHERE following_id = :user_id", nativeQuery = true)
    int numberOfFollower(@Param("user_id") String user_id);

}
