package com.ashapiro.client.service.impl;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;
import com.ashapiro.client.service.RegisterService;
import com.ashapiro.client.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final HttpUtils httpUtils;

    @Value("${local.server.address.create}")
    private String url;

    @Override
    public ResponseDto save(UserRequestDto userRequestDto) {
        return httpUtils.sendRequest(url, userRequestDto);
    }

}
