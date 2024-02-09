package com.ashapiro.chat.services.impl;

import com.ashapiro.chat.dtos.message.ChatMessageDTO;
import com.ashapiro.chat.entities.Chat;
import com.ashapiro.chat.entities.ChatType;
import com.ashapiro.chat.entities.User;
import com.ashapiro.chat.enums.TypeOfChat;
import com.ashapiro.chat.repository.ChatRepository;
import com.ashapiro.chat.repository.ChatTypeRepository;
import com.ashapiro.chat.services.ChatService;
import com.ashapiro.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ChatTypeRepository chatTypeRepository;
    private final UserService userService;

    @Override
    public List<ChatMessageDTO> getMessagesFromChat(Long userId) {
        List<ChatMessageDTO> messages = chatRepository.getMessagesByUserId(userId);
        return messages;
    }

    @Override
    @Transactional
    public Long getChatIdBetweenUsers(Long senderId, Long recipientId) {
        Optional<Chat> chat = findPrivateChatBetweenUsers(senderId, recipientId);
        if (!chat.isPresent()) {
            chat = Optional.of(createPrivateChat(senderId, recipientId));
        }
        return chat.get().getId();
    }

    @Override
    public Optional<Chat> findChatById(Long id) {
        return chatRepository.findChatById(id);
    }

    @Override
    public Optional<Chat> findPrivateChatBetweenUsers(Long userId, Long recipientId) {
        return chatRepository.findPrivateChatBetweenUsers(userId, recipientId);
    }

    @Transactional
    protected Chat createPrivateChat(Long senderId, Long recipientId) {
        Chat chat = new Chat();
        User sender = getUserById(senderId);
        User recipient = getUserById(recipientId);
        ChatType chatType = getPrivateChatType();
        chat.getUsers().add(sender);
        chat.getUsers().add(recipient);
        chat.setChatType(chatType);
        return chatRepository.save(chat);
    }

    private User getUserById(Long userId) {
        return userService.findById(userId).orElseThrow(() -> new IllegalArgumentException());
    }

    private ChatType getPrivateChatType() {
        return chatTypeRepository.findChatTypeByType(TypeOfChat.PRIVATE);
    }
}
