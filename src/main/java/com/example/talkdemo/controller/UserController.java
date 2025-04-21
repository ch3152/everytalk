package com.example.talkdemo.controller;

import com.example.talkdemo.model.User;
import com.example.talkdemo.service.UserService;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;


import com.example.talkdemo.jwt.JwtUtil; // ✅ 수정된 부분

import java.util.Map;

// 사용자 관련 API 컨트롤러
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    // 아이디 중복 체크
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameTaken(username);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
        }
        return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }

    // 인증 이메일 전송
    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean sent = userService.sendVerificationCode(email);
        return sent ? ResponseEntity.ok("이메일 전송 완료") : ResponseEntity.status(500).body("이메일 전송 실패");
    }

    // 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String inputCode = body.get("code");
        boolean valid = userService.verifyCode(email, inputCode);
        return valid ? ResponseEntity.ok("인증 성공") : ResponseEntity.status(400).body("인증 실패");
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        boolean saved = userService.registerUser(user);
        return saved ? ResponseEntity.ok("회원가입 성공") : ResponseEntity.status(400).body("회원가입 실패");
    }

    // 로그인 처리 및 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        User user = userService.getUserIfValid(username, password);
        if (user != null) {
            String token = jwtUtil.generateToken(user.getNicname());

            return ResponseEntity.ok(Map.of(
                "token", token,
                "nickname", user.getNicname()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    // 마이페이지 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);

        Map<String, Object> result = userService.getMyPageInfo(nickname);
        return ResponseEntity.ok(result);
    }
}
