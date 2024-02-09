package com.ashapiro.chat.repository;

import com.ashapiro.chat.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
