package com.ashapiro.chat.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTokenDto {
    private Long id;
    private String username;
}
