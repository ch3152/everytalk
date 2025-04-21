package com.example.talkdemo.service;

import com.example.talkdemo.model.ChatDocument;
import com.example.talkdemo.model.ChatMessage;
import com.example.talkdemo.model.ChatRoom;
import com.example.talkdemo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TalkService {

    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final MongoTemplate mongoTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final Logger log = LoggerFactory.getLogger(TalkService.class);

    // Redis에 채팅 메시지 저장 및 TTL 설정
    public void saveToRedis(ChatMessage message) {
        String key = "chat:room:" + message.getRoomId();
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, Duration.ofMinutes(3));
    }

    // 랜덤 채팅 사용자 매칭
    public Map<String, String> matchRandomUser(String nickname) {
        stringRedisTemplate.delete("user:room:" + nickname);
        String waitingKey = "waiting:random";
        String partner = stringRedisTemplate.opsForList().leftPop(waitingKey);

        if (partner == null || partner.equals(nickname)) {
            stringRedisTemplate.opsForList().rightPush(waitingKey, nickname);
            stringRedisTemplate.expire(waitingKey, 1, TimeUnit.MINUTES);
            return Map.of("roomId", "waiting");
        }

        String roomId = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("match:room:" + roomId, nickname + "," + partner, 3, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set("user:room:" + nickname, roomId, 3, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set("user:room:" + partner, roomId, 3, TimeUnit.MINUTES);

        ChatRoom room = new ChatRoom();
        room.setRoomId(roomId);
        room.setUserA(nickname);
        room.setUserB(partner);
        room.setLastMessage("");
        room.setLastTimestamp(LocalDateTime.now());
        chatRoomRepository.save(room);

        return Map.of("roomId", roomId, "partner", partner);
    }

    // 매칭된 방 정보 조회
    public Map<String, String> checkRoomWithPartner(String nickname) {
        String roomId = stringRedisTemplate.opsForValue().get("user:room:" + nickname);
        if (roomId == null) return Map.of("roomId", "waiting");

        String matchKey = stringRedisTemplate.opsForValue().get("match:room:" + roomId);
        String[] users = matchKey != null ? matchKey.split(",") : new String[]{};
        String partner = Arrays.stream(users).filter(name -> !name.equals(nickname)).findFirst().orElse("");

        return Map.of("roomId", roomId, "partner", partner);
    }

    // 채팅방 나가기 처리
    public void leaveRoom(String nickname) {
        stringRedisTemplate.delete("user:room:" + nickname);
        stringRedisTemplate.opsForList().remove("waiting:random", 0, nickname);
    }

    // 채팅방 마지막 메시지 및 시간 업데이트
    public void updateChatRoom(ChatMessage message) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(message.getRoomId());
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            room.setLastMessage(message.getContent());
            room.setLastTimestamp(LocalDateTime.now());
            chatRoomRepository.save(room);
        }
    }

    // 최근 채팅방 목록 조회
    public List<ChatRoom> getRecentChats(String nickname) {
        return chatRoomRepository.findByUserAOrUserBOrderByLastTimestampDesc(nickname, nickname);
    }

    // Redis 또는 MongoDB에서 채팅 기록 불러오기
    public List<ChatMessage> getAndLogChatHistory(String roomId) {
        String key = "chat:room:" + roomId;
        List<ChatMessage> messages = redisTemplate.opsForList().range(key, 0, -1);

        if (messages == null || messages.isEmpty()) {
            Query query = new Query();
            query.addCriteria(Criteria.where("roomId").is(roomId));
            List<ChatDocument> docs = mongoTemplate.find(query, ChatDocument.class);

            if (docs != null && !docs.isEmpty()) {
                messages = docs.stream().map(doc -> {
                    ChatMessage msg = new ChatMessage();
                    msg.setRoomId(doc.getRoomId());
                    msg.setSender(doc.getSender());
                    msg.setContent(doc.getContent());
                    long epochMillis = doc.getCreatedAt()
                                          .atZone(ZoneId.systemDefault())
                                          .toInstant()
                                          .toEpochMilli();
                    msg.setTimestamp(epochMillis);
                    return msg;
                }).collect(Collectors.toList());
            }
        }
        return messages;
    }
}
