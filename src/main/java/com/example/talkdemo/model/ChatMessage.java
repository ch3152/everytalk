package com.example.talkdemo.model;


import lombok.Data;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;

// 실시간 채팅 메시지 구조
@Data
public class ChatMessage {
    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;
}
