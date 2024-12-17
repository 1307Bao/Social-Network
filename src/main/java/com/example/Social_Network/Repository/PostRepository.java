package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.Post;
import com.example.Social_Network.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    @Query(value = """
            SELECT P.*
            FROM POST P
            JOIN USER_FOLLOWING UF ON P.USER_ID = UF.FOLLOWING_ID
            WHERE UF.USER_ID = :CURRENT_ID
              AND (P.create_at < :LAST_CREATED_DATE
                   OR(:LAST_CREATED_DATE IS NULL))
            ORDER BY P.create_at DESC
            LIMIT 10;
            """, nativeQuery = true)
    List<Post> getAllPost(@Param("CURRENT_ID") String userId, @Param("LAST_CREATED_DATE") Date lastCreateDate);

    @Query(value = "SELECT * FROM POST P WHERE USER_ID = :user_id ORDER BY create_at desc", nativeQuery = true)
    List<Post> getAllPostOfUser(@Param("user_id") String user_id);

    @Query(value = """
    SELECT * FROM post\s
    WHERE user_id NOT IN (
        SELECT uf.user_id\s
        FROM user_following uf\s
        WHERE user_id = :currentUserId
    ) AND user_id <> :currentUserId 
    ORDER BY RAND(:seed)
    LIMIT :limit OFFSET :offset
   \s""", nativeQuery = true)
    List<Post> getExplorePosts(@Param("currentUserId") String currentUserId,
                               @Param("seed") long seed,
                               @Param("limit") int limit,
                               @Param("offset") int offset);

    @Query(value = """
            SELECT u.* FROM post p
            JOIN post_like pl ON p.post_id = pl.post_id
            JOIN user u on u.user_id = pl.user_id
            where u.user_id <> :userId and p.post_id = :postId
            """, nativeQuery = true)
    List<User> getUsersLikePost(@Param("postId") String postId, @Param("userId") String userId);
}
