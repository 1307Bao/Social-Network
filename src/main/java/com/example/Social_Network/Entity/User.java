package com.example.Social_Network.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String user_id;
    String username;
    String password;
    String email;
    @Column(name = "gender")
    String gender;
    @Column(name = "fullname")
    String fullname;
    @Column(name = "birthday")
    Date birthday;
    @Column(name = "create_at")
    Date createAt;
    @Column(name = "avatar")
    String avatar;
    @Column(name = "bio")
    String bio;
}
