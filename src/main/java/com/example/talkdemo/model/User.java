package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

// 회원가입 및 유저 정보 도큐먼트
@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String nicname;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private String birthDate;
    private LocalDateTime createdAt = LocalDateTime.now();
}
