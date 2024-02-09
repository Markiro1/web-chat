package com.ashapiro.chat.entities;

import com.ashapiro.chat.enums.TypeOfChat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_types")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "chat")
public class ChatType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeOfChat type;

    @OneToMany(mappedBy = "chatType")
    private List<Chat> chat = new ArrayList<>();
}
