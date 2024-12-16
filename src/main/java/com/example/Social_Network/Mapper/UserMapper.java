package com.example.Social_Network.Mapper;

import com.example.Social_Network.DTO.Response.ProfileResponse;
import com.example.Social_Network.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    ProfileResponse toUserResponse(User user);
}
