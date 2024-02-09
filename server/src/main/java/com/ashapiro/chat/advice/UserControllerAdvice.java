package com.ashapiro.chat.advice;

import com.ashapiro.chat.exceptions.DuplicateUserException;
import com.ashapiro.chat.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<?> handleDuplicateUserExceptions() {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "USER ALREADY EXISTS WITH THIS USERNAME"),
                HttpStatus.BAD_REQUEST);
    }

}
