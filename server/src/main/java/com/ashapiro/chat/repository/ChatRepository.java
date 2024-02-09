    package com.ashapiro.chat.repository;

    import com.ashapiro.chat.dtos.message.ChatMessageDTO;
    import com.ashapiro.chat.entities.Chat;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.util.List;
    import java.util.Optional;

    public interface ChatRepository extends JpaRepository<Chat, Long> {

        @Query("SELECT c " +
                "FROM Chat c " +
                "JOIN c.users u1 " +
                "JOIN c.users u2 " +
                "WHERE u1.id = :senderId " +
                "AND u2.id = :recipientId " +
                "AND c.chatType.type = 'PRIVATE'")
        Optional<Chat> findPrivateChatBetweenUsers(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);

        @Query("SELECT new com.ashapiro.chat.dtos.message.ChatMessageDTO(m.id, m.chat.id, m.user.id, u.username, m.text) " +
                "FROM Message m " +
                "JOIN m.chat.users cu " +
                "JOIN m.user u " +
                "WHERE cu.id = :userId")
        List<ChatMessageDTO> getMessagesByUserId(@Param("userId") Long userId);

        Optional<Chat> findChatById(Long id);

    }
