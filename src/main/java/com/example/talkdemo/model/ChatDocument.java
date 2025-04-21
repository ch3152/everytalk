package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chatMessages")
public class ChatDocument {

    @Id
    private String id;
    private String roomId;
    private String sender;
    private String content;

    // TTL 인덱스: 10분) 후 자동 삭제
    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static ChatDocument fromMessage(ChatMessage message) {
        ChatDocument doc = new ChatDocument();
        doc.setRoomId(message.getRoomId());
        doc.setSender(message.getSender());
        doc.setContent(message.getContent());
        // createdAt은 기본값 또는 저장 시점의 시간이 자동 설정됨
        return doc;
    }
}
