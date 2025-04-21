package com.example.talkdemo.scheduler;

import com.example.talkdemo.model.GroupChatDocument;
import com.example.talkdemo.model.GroupMessage;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupChatBackupScheduler {

    private final RedisTemplate<String, GroupMessage> redisTemplate;
    private final MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    public void backupGroupMessages() {
        Set<String> keys = redisTemplate.keys("group:room:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.info("[그룹백업] 키: {} 의 TTL: {}초", key, ttl);

            if (ttl == null || ttl <= 63) {
                List<GroupMessage> messages = redisTemplate.opsForList().range(key, 0, -1);
                if (messages != null && !messages.isEmpty()) {
                    List<GroupChatDocument> docs = messages.stream()
                        .map(GroupChatDocument::fromMessage)
                        .peek(doc -> log.info("[그룹백업] 백업 문서: {}", doc))
                        .collect(Collectors.toList());
                    mongoTemplate.insertAll(docs);
                    log.info("[그룹백업] MongoDB 저장 완료 - 키: {}", key);
                } else {
                    log.info("[그룹백업] 키: {} 에 메시지 없음", key);
                }
                redisTemplate.delete(key);
                log.info("[그룹백업] Redis 키 삭제 완료 - 키: {}", key);
            }
        }
    }
}
