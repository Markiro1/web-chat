package com.ashapiro.chat.services;

import com.ashapiro.chat.dtos.message.ChatMessageDTO;
import com.ashapiro.chat.entities.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatService {


    List<ChatMessageDTO> getMessagesFromChat(Long userId);

    Long getChatIdBetweenUsers(Long senderId, Long recipientId);

    Optional<Chat> findChatById(Long id);

    Optional<Chat> findPrivateChatBetweenUsers(Long userId, Long recipientId);
}
