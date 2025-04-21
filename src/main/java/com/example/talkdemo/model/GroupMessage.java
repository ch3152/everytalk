package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

// 단체 채팅 송수신용 메시지 객체
@Data
public class GroupMessage {
    private String roomId;
    private String sender;
    private String content;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private long timestamp;
}
