package com.ashapiro.client.service;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;

public interface RegisterService {

    ResponseDto save(UserRequestDto userRequestDto);
}
