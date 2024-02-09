package com.ashapiro.client.service.impl;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;
import com.ashapiro.client.service.LoginService;
import com.ashapiro.client.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final HttpUtils httpUtils;

    @Value("${local.server.address.auth}")
    private String url;

    @Override
    public ResponseDto authenticateUser(UserRequestDto userRequestDto) {
        return httpUtils.sendRequest(url, userRequestDto);
    }

}
