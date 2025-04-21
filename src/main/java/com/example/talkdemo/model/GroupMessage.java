package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class GroupMessage {
    private String roomId;
    private String sender;
    private String content;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600) // 10분 TTL
    private long timestamp;
}
