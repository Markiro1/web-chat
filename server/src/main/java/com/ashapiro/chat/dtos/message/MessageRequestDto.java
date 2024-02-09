package com.ashapiro.chat.dtos.message;

import com.ashapiro.chat.enums.TypeOfMessage;
import lombok.Data;

@Data
public class MessageRequestDto {
    private Long userId;
    private Long chatId;
    private Long recipientId;
    private TypeOfMessage type;
    private String message;
}
