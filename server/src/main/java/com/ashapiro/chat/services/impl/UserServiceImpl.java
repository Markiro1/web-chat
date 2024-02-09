package com.ashapiro.chat.services.impl;

import com.ashapiro.chat.dtos.user.UserRequestDto;
import com.ashapiro.chat.entities.User;
import com.ashapiro.chat.exceptions.DuplicateUserException;
import com.ashapiro.chat.repository.UserRepository;
import com.ashapiro.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User save(UserRequestDto userRequest) {
        validateUsername(userRequest.getUsername());
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);
        User user = convertToUserFromRequest(userRequest);
        userRepository.save(user);
        userRepository.addUserToGlobalChat(user.getId());
        return user;
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("User with username:" + username + " already exists");
        }
    }

    private User convertToUserFromRequest(UserRequestDto userRequestDto) {
        return modelMapper.map(userRequestDto, User.class);
    }
}
