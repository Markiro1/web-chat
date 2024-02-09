package com.ashapiro.chat.controllers;

import com.ashapiro.chat.dtos.jwt.JwtRequest;
import com.ashapiro.chat.dtos.jwt.JwtResponse;
import com.ashapiro.chat.exceptions.ErrorResponse;
import com.ashapiro.chat.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/auth")
    public ResponseEntity<?> identification(@RequestBody JwtRequest authRequest) {
        log.info("USER IDENTIFICATION");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                            authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity(new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                    "INCORRECT LOGIN OR PASSWORD"), HttpStatus.BAD_REQUEST);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
