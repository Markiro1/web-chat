package com.ashapiro.chat.handlers;

import com.ashapiro.chat.dtos.message.MessageRequestDto;
import com.ashapiro.chat.entities.User;
import com.ashapiro.chat.enums.TypeOfMessage;
import com.ashapiro.chat.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private Map<Long, List<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        MessageRequestDto requestMessage = new ObjectMapper().readValue(payload, MessageRequestDto.class);
        TypeOfMessage typeOfMessage = requestMessage.getType();

        switch (typeOfMessage) {
            case ON_CONNECTION -> addNewConnection(session, requestMessage);
            case REQUEST_USER_LIST -> getCurrentUserList();
            case ON_CLOSE -> closeConnection(requestMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    public void sendMessageToSessions(MessageRequestDto messageRequest) {
        String messageJson = "NEW_MESSAGE:" + convertToJson(messageRequest);
        if (messageRequest.getType() == TypeOfMessage.SEND_TO_ALL) {
            sendMessage(messageJson);
        } else if (messageRequest.getType() == TypeOfMessage.SEND_TO_ONE_PERSON) {
            sendMessageToLocalSession(messageRequest.getUserId(), messageJson);
            sendMessageToLocalSession(messageRequest.getRecipientId(), messageJson);
        }
    }

    private void addNewConnection(WebSocketSession session, MessageRequestDto message) throws JsonProcessingException {
        Long userId = message.getUserId();
        log.info("NEW CONNECTION");
        userService.findById(userId).ifPresent(user -> {
            addUserToSession(session, userId);
            broadcastUserCount();
        });
    }

    private void addUserToSession(WebSocketSession session, Long userId) {
        List<WebSocketSession> userSessionList = userSessions.computeIfAbsent(userId, k -> new ArrayList<>());
        userSessionList.add(session);
    }


    private void closeConnection(MessageRequestDto message) throws JsonProcessingException {
        Long userId = message.getUserId();
        Optional<User> user = userService.findById(userId);
        user.ifPresent(u -> {
            userSessions.remove(userId);
            log.info("USER DISCONNECTED");
            broadcastUserCount();
            try {
                getCurrentUserList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void broadcastUserCount() {
        log.info("GET USER COUNT");
        String userCountMessage = "USER_COUNT:" + userSessions.size();
        sendMessage(userCountMessage);
    }

    private void getCurrentUserList() throws JsonProcessingException {
        log.info("GET CURRENT USER LIST");
        Map<Long, String> usersMap = userService.findAllById(userSessions.keySet())
                .stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        String userMap = "USER_LIST:" + objectMapper.writeValueAsString(usersMap);
        sendMessage(userMap);
    }

    private void sendMessage(String messageSend) {
        for (List<WebSocketSession> userSessionList : userSessions.values()) {
            for (WebSocketSession userSession : userSessionList) {
                if (userSession.isOpen()) {
                    try {
                        userSession.sendMessage(new TextMessage(messageSend));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void sendMessageToLocalSession(Long userId, String message) {
        List<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            for (WebSocketSession userSession : sessions) {
                if (userSession.isOpen()) {
                    try {
                        userSession.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String convertToJson(MessageRequestDto messageRequestDto) {
        try {
            return objectMapper.writeValueAsString(messageRequestDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
