// src/main/java/com/example/talkdemo/model/ChatRoom.java
package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// 일대일 채팅방 정보 도큐먼트
@Data
@Document(collection = "chatRooms")
public class ChatRoom {
    @Id
    private String roomId;
    private String userA;
    private String userB;
    private String lastMessage;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 600)
    private LocalDateTime lastTimestamp;
}
