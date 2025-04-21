package com.example.talkdemo.model;


import lombok.Data;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class ChatMessage {
     // TTL 인덱스: 10분) 후 자동 삭제
    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;
}
