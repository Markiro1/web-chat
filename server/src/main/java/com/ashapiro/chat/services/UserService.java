package com.ashapiro.chat.services;

import com.ashapiro.chat.dtos.user.UserRequestDto;
import com.ashapiro.chat.entities.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    User save(UserRequestDto user);

    Optional<User> findById(Long id);

    List<User> findAllById(Iterable<Long> ids);
}
