package com.example.talkdemo.controller;

import com.example.talkdemo.model.ChatMessage;
import com.example.talkdemo.model.ChatRoom;
import com.example.talkdemo.service.TalkService;
import com.example.talkdemo.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TalkController {

    private final SimpMessagingTemplate messagingTemplate;
    private final TalkService talkService;
    private final JwtUtil jwtUtil;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        log.info("🗨 메시지 수신: {}", message);
        talkService.saveToRedis(message); // 메시지 저장 및 필요시 백업 실행
        talkService.updateChatRoom(message); // 채팅방 마지막 메시지 업데이트
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }

    @PostMapping("/api/chat/enter-random")
    public Map<String, String> enterRandom(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        log.info("🔑 JWT 닉네임: {}", nickname);

        Map<String, String> result = talkService.matchRandomUser(nickname);
        log.info("🤝 매칭 결과: {}", result);
        return result;
    }

    @GetMapping("/api/chat/check-room")
    public Map<String, String> checkRoom(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        return talkService.checkRoomWithPartner(nickname);
    }

    @PostMapping("/api/chat/leave")
    public void leaveRoom(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        talkService.leaveRoom(nickname);
    }

    // 최근 채팅방 목록 조회
    @GetMapping("/api/chat/recent")
    public List<ChatRoom> getRecentChats(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        return talkService.getRecentChats(nickname);
    }

    // Redis에 저장된 채팅 기록 조회
    @GetMapping("/api/chat/redis-history/{roomId}")
    public List<ChatMessage> getRedisChatHistory(@PathVariable String roomId,
                                                 @RequestHeader("Authorization") String authHeader) {
        // 필요시 토큰 검증 추가
        return talkService.getAndLogChatHistory(roomId);
    }
}
