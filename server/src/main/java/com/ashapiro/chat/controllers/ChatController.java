package com.ashapiro.chat.controllers;

import com.ashapiro.chat.dtos.message.ChatMessageDTO;
import com.ashapiro.chat.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessageDTO>> getHistoryFromUserChats(@PathVariable Long userId) {
        log.info("GET CHAT HISTORY BY USER ID: " + userId);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(chatService.getMessagesFromChat(userId));
    }

    @GetMapping("/privateChat/{senderId}/{recipientId}")
    public ResponseEntity<Long> getPrivateChatId(@PathVariable Long senderId, @PathVariable Long recipientId) {
        log.info("GET CHAT ID BETWEEN USERS");
        return ResponseEntity.ok(chatService.getChatIdBetweenUsers(senderId, recipientId));
    }
}
