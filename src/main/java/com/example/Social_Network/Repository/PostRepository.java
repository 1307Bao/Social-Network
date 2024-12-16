package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    @Query(value = "" +
            "SELECT P.* " +
            "FROM POST P " +
            "JOIN USER_FOLLOWING UF ON P.USER_ID = UF.FOLLOWING_ID " +
            "WHERE UF.USER_ID = :CURRENT_ID", nativeQuery = true)
    Page<Post> getAllPost(@Param("CURRENT_ID") String userId, Pageable pageable);

    @Query(value = "SELECT * FROM POST P WHERE USER_ID = :user_id", nativeQuery = true)
    List<Post> getAllPostOfUser(@Param("user_id") String user_id);

    @Query(value = """
    SELECT * FROM post 
    WHERE user_id NOT IN (
        SELECT following_id 
        FROM user_following 
        WHERE user_id = :currentUserId
    )
    ORDER BY RAND()
    LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Post> getExplorePosts(@Param("currentUserId") String currentUserId,
                               @Param("limit") int limit,
                               @Param("offset") int offset);

}
