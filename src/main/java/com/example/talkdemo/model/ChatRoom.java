// src/main/java/com/example/talkdemo/model/ChatRoom.java
package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "chatRooms")
public class ChatRoom {
    @Id
    private String roomId;
    private String userA;
    private String userB;
    private String lastMessage;
    // TTL 인덱스: 10분) 후 자동 삭제
    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private LocalDateTime lastTimestamp;
}
