package com.example.talkdemo.repository;

import com.example.talkdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    User findByNicname(String nicname);

    
}