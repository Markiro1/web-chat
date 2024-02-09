package com.ashapiro.chat.controllers;

import com.ashapiro.chat.dtos.message.MessageRequestDto;
import com.ashapiro.chat.handlers.WebSocketHandlerImpl;
import com.ashapiro.chat.services.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService MessageService;

    private final WebSocketHandlerImpl webSocketHandler;

    @PostMapping("/save")
    public ResponseEntity<?> saveMessage(@RequestBody MessageRequestDto message) {
        log.info("SAVE MESSAGE");
        MessageService.saveMessage(message);
        webSocketHandler.sendMessageToSessions(message);
        return ResponseEntity.ok().body("message saved");
    }
}
