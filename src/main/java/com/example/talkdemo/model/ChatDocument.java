package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// 일반 채팅 메시지 저장용 도큐먼트
@Data
@Document(collection = "chatMessages")
public class ChatDocument {
    @Id
    private String id;
    private String roomId;
    private String sender;
    private String content;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ChatDocument fromMessage(ChatMessage message) {
        ChatDocument doc = new ChatDocument();
        doc.setRoomId(message.getRoomId());
        doc.setSender(message.getSender());
        doc.setContent(message.getContent());
        return doc;
    }
}
