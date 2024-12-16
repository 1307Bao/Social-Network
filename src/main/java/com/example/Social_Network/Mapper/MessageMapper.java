package com.example.Social_Network.Mapper;

import com.example.Social_Network.DTO.Response.ChatMessageResponse;
import com.example.Social_Network.Entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface MessageMapper {
    ChatMessageResponse toChatMessageResponse(Message message);
}
