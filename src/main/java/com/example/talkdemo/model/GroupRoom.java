package com.example.talkdemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

// 단체 채팅방 정보 도큐먼트
@Data
@Document(collection = "group_room")
public class GroupRoom {
    @Id
    private String id;
    private String title;
    private String host;
    private List<String> members;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 120)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public void setCreatedAt(LocalDateTime localDateTime) {
        this.createdAt = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getCreatedAtLocalDateTime() {
        return this.createdAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
