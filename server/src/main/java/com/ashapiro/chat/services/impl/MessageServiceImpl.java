package com.ashapiro.chat.services.impl;

import com.ashapiro.chat.dtos.message.MessageRequestDto;
import com.ashapiro.chat.entities.Chat;
import com.ashapiro.chat.entities.Message;
import com.ashapiro.chat.entities.User;
import com.ashapiro.chat.enums.TypeOfMessage;
import com.ashapiro.chat.repository.MessageRepository;
import com.ashapiro.chat.services.ChatService;
import com.ashapiro.chat.services.MessageService;
import com.ashapiro.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    private final ChatService chatService;

    private final UserService userService;


    public void saveMessage(MessageRequestDto message) {
        Optional<User> user = userService.findById(message.getUserId());
        Long recipientId = message.getRecipientId();
        Long chatId = message.getChatId();

        Optional<Chat> chat;
        if (message.getType() == TypeOfMessage.SEND_TO_ALL) {
            chat = chatService.findChatById(chatId);
        } else {
            chat = chatService.findPrivateChatBetweenUsers(user.get().getId(), recipientId);
        }
        createMessage(user.get(), chat.get(), message.getMessage());
    }

    private void createMessage(User user, Chat chat, String text) {
        Message msg = new Message();
        msg.setUser(user);
        msg.setChat(chat);
        msg.setText(text);
        messageRepository.save(msg);
    }
}
