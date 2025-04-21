package com.example.talkdemo.scheduler;

import com.example.talkdemo.model.ChatDocument;
import com.example.talkdemo.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// 일대일 채팅 Redis → MongoDB 백업 스케줄러
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatBackupScheduler {

    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 30000)
    public void backupExpiredMessages() {
        Set<String> keys = redisTemplate.keys("chat:room:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl == null || ttl <= 61) {
                List<ChatMessage> messages = redisTemplate.opsForList().range(key, 0, -1);
                if (messages != null && !messages.isEmpty()) {
                    List<ChatDocument> docs = messages.stream()
                            .map(ChatDocument::fromMessage)
                            .collect(Collectors.toList());
                    mongoTemplate.insertAll(docs);
                    log.info("[ChatBackup] MongoDB 저장 완료 - {}", key);
                }
                redisTemplate.delete(key);
                log.info("[ChatBackup] Redis 키 삭제 완료 - {}", key);
            }
        }
    }
}
