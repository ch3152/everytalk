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

// 단체 채팅 Redis → MongoDB 백업 스케줄러
@Component
@RequiredArgsConstructor
@Slf4j
public class GroupChatBackupScheduler {

    private final RedisTemplate<String, GroupMessage> redisTemplate;
    private final MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 30000)
    public void backupGroupMessages() {
        Set<String> keys = redisTemplate.keys("group:room:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl == null || ttl <= 63) {
                List<GroupMessage> messages = redisTemplate.opsForList().range(key, 0, -1);
                if (messages != null && !messages.isEmpty()) {
                    List<GroupChatDocument> docs = messages.stream()
                            .map(GroupChatDocument::fromMessage)
                            .collect(Collectors.toList());
                    mongoTemplate.insertAll(docs);
                    log.info("[GroupBackup] MongoDB 저장 완료 - {}", key);
                }
                redisTemplate.delete(key);
                log.info("[GroupBackup] Redis 키 삭제 완료 - {}", key);
            }
        }
    }
}
