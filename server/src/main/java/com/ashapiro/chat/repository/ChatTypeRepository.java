package com.ashapiro.chat.repository;

import com.ashapiro.chat.entities.ChatType;
import com.ashapiro.chat.enums.TypeOfChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatTypeRepository extends JpaRepository<ChatType, Long> {

    ChatType findChatTypeByType(TypeOfChat type);
}
