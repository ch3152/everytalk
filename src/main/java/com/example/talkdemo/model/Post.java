package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// SNS 게시글 도큐먼트
@Data
@Document(collection = "sns_post")
public class Post {
    @Id
    private String id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
    private int viewCount = 0;

    private List<LikeInfo> likedUsers = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    @Data
    public static class LikeInfo {
        private String nickname;
        private LocalDateTime likedAt = LocalDateTime.now();
    }

    @Data
    public static class Comment {
        private String nickname;
        private String content;
        private LocalDateTime createdAt = LocalDateTime.now();
    }
}
