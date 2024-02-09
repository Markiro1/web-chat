package com.ashapiro.client.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResponseDto {
    private int status;
    private String message;
    private long timestamp;
    private String token;
}
