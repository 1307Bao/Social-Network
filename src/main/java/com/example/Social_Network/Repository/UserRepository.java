package com.example.Social_Network.Repository;

import com.example.Social_Network.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);

    @Query(value = "SELECT username FROM user WHERE user_id = :user_id", nativeQuery = true)
    String getUsername(@Param("user_id") String user_id);

    @Query(value = "SELECT avatar FROM user WHERE user_id = :user_id", nativeQuery = true)
    String getImage(@Param("user_id") String user_id);

    @Query(value = "SELECT * FROM user WHERE username like :username%" +
            " AND user_id <> :currentId" +
            " LIMIT 5", nativeQuery = true)
    List<User> findByUsername(@Param("username") String username, @Param("currentId") String currentId);

    @Query(value = """
                SELECT u.* 
                from user u where user_id in (
                    SELECT UF.following_id
                    FROM user_following UF 
                    WHERE UF.user_id = :userId
                )
            """, nativeQuery = true)
    List<User> myFollowing(@Param("userId") String userId);

    @Query(value = """
                SELECT u.* 
                from user u where user_id in (
                    SELECT UF.user_id
                    FROM user_following UF 
                    WHERE UF.following_id = :userId
                )
            """, nativeQuery = true)
    List<User> myFollowers(@Param("userId") String userId);
}
