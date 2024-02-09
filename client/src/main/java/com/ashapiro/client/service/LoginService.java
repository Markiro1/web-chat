package com.ashapiro.client.service;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;

public interface LoginService {
    ResponseDto authenticateUser(UserRequestDto userRequestDto);
}
