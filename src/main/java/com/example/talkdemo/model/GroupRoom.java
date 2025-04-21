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

@Data
@Document(collection = "group_room")
public class GroupRoom {
    @Id
    private String id;
    private String title;
    private String host;
    private List<String> members;

    @Indexed(name = "createdAtTTL", expireAfterSeconds = 120) // 2분 후 삭제
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // JSON 직렬화 시 포맷 지정
    private Date createdAt;

    // 생성 시 LocalDateTime을 Date로 변환
    public void setCreatedAt(LocalDateTime localDateTime) {
        this.createdAt = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    // 저장 시 LocalDateTime으로 반환 (필요시 사용)
    public LocalDateTime getCreatedAtLocalDateTime() {
        return this.createdAt.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
