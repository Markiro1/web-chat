package com.ashapiro.client.util;

import com.ashapiro.client.dto.ResponseDto;
import com.ashapiro.client.dto.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class HttpUtils {

    private final RestTemplate restTemplate;

    public ResponseDto sendRequest(String url, UserRequestDto request) {
        HttpEntity<UserRequestDto> generatedRequest = generateRequest(request);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url, generatedRequest, String.class);
        } catch (HttpClientErrorException e) {
            responseEntity = ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
        return parseResponse(responseEntity.getBody());
    }

    private HttpEntity<UserRequestDto> generateRequest(UserRequestDto userRequestDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(userRequestDto, httpHeaders);
    }

    private ResponseDto parseResponse(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(responseBody, ResponseDto.class);
        } catch (Exception e) {
            return new ResponseDto();
        }
    }
}
