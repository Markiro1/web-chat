package com.ashapiro.chat.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long chatId;
    private Long userId;
    private String username;
    private String text;
}
