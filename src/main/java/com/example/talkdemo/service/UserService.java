package com.example.talkdemo.service;

import com.example.talkdemo.model.Post;
import com.example.talkdemo.model.User;
import com.example.talkdemo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static class CodeInfo {
        String code;
        LocalDateTime timestamp;

        CodeInfo(String code, LocalDateTime timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }

    private final Map<String, CodeInfo> verificationCodes = new HashMap<>();

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean sendVerificationCode(String email) {
        try {
            String code = generateVerificationCode();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ccchhh3152@gmail.com");
            message.setTo(email);
            message.setSubject("회원가입 인증 코드");
            message.setText("당신의 인증 코드는: " + code);

            mailSender.send(message);
            logger.info("✅ 이메일 전송 완료 → {} / 코드: {}", email, code);

            verificationCodes.put(email, new CodeInfo(code, LocalDateTime.now()));
            return true;
        } catch (Exception e) {
            logger.error("❌ 이메일 전송 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        CodeInfo info = verificationCodes.get(email);
        if (info == null) return false;

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(info.timestamp.plusMinutes(3))) {
            verificationCodes.remove(email);
            logger.warn("⏰ 인증 코드 만료: {}", email);
            return false;
        }

        return info.code.equals(inputCode);
    }

    public boolean registerUser(User user) {
        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            logger.error("❌ 회원가입 저장 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
        public boolean login(String id, String password) {
            User user = userRepository.findByUsername(id);
            if (user == null) return false;

            // 해시된 비밀번호와 비교
            return BCrypt.checkpw(password, user.getPassword());
        }
    
    public User getUserIfValid(String id, String password) {
        User user = userRepository.findByUsername(id);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    public Map<String, Object> getMyPageInfo(String nickname) {
        User user = userRepository.findByNicname(nickname); // 오타: nicname → nickname 주의
        if (user == null) {
            throw new RuntimeException("유저 없음");
        }
    
        Query query = new Query(Criteria.where("nickname").is(nickname));
        List<Post> myPosts = mongoTemplate.find(query, Post.class);
    
        int postCount = myPosts.size();
        int likeCount = myPosts.stream()
            .mapToInt(p -> p.getLikedUsers().size())
            .sum();
        int commentCount = myPosts.stream()
            .mapToInt(p -> p.getComments().size())
            .sum();
    
        Map<String, Object> result = new HashMap<>();
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("phoneNumber", user.getPhoneNumber());
        result.put("name", user.getName());
        result.put("nickname", user.getNicname()); // 오타: nicname 필드 그대로라면 여기도
        result.put("birthDate", user.getBirthDate());
        result.put("createdAt", user.getCreatedAt());

        result.put("postCount", postCount);
        result.put("likeCount", likeCount);
        result.put("commentCount", commentCount);
    
        return result;
    }
};