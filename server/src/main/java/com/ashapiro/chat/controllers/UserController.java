package com.ashapiro.chat.controllers;

import com.ashapiro.chat.dtos.user.UserRequestDto;
import com.ashapiro.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody UserRequestDto userRequest) {
        log.info("SAVE USER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.save(userRequest));
    }

}
