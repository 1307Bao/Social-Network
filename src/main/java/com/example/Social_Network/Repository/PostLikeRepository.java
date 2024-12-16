package com.example.Social_Network.Repository;

import com.example.Social_Network.Embeddable.PostLikeId;
import com.example.Social_Network.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByPostLikeId_PostIdAndPostLikeId_UserId(String postId, String userId);

    @Query(value = "SELECT COUNT(*) FROM post_like WHERE post_id = :post_id", nativeQuery = true)
    int getNumberOfLike(@Param("post_id") String post_id);
}
