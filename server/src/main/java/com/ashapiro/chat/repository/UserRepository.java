package com.ashapiro.chat.repository;

import com.ashapiro.chat.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findById(Long id);

    @Modifying
    @Query(value = "INSERT INTO chat_users (user_id, chat_id) " +
            "SELECT :userId, c.id FROM chats c " +
            "JOIN chat_types ct ON c.chat_type_id = ct.id " +
            "WHERE ct.type = 'GLOBAL'",
            nativeQuery = true)
    void addUserToGlobalChat(@Param("userId") Long userId);
}
