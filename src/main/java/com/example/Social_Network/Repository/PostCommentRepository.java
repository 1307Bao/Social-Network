package com.example.Social_Network.Repository;

import com.example.Social_Network.Embeddable.PostCommentId;
import com.example.Social_Network.Entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, PostCommentId> {
    @Query(value = "SELECT COUNT(*) FROM post_comment WHERE post_id = :post_id", nativeQuery = true)
    int getNumberOfComment(@Param("post_id") String post_id);

    @Query(value = "SELECT * FROM post_comment WHERE post_id = :post_id", nativeQuery = true)
    List<PostComment> getCommentInPost(@Param("post_id") String post_id);
}
