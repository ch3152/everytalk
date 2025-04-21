package com.example.talkdemo.repository;

import com.example.talkdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// 유저 정보 조회/검증용 리포지토리
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByNicname(String nicname);
}
