package com.ashapiro.chat.services;

import com.ashapiro.chat.dtos.message.MessageRequestDto;

public interface MessageService {

    void saveMessage(MessageRequestDto message);
}
