package com.example.talkdemo.scheduler;

import com.example.talkdemo.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotPostBackupScheduler {

    private final RedisTemplate<String, Post> postRedisTemplate;
    private final MongoTemplate mongoTemplate;

    @Scheduled(fixedRate = 30000)
    public void backupHotPosts() {
        Set<String> keys = postRedisTemplate.keys("hot:post:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long ttl = postRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.info("[HotPost 스케줄러] 키: {} TTL: {}", key, ttl);

            if (ttl == null || ttl <= 90) {
                Post post = postRedisTemplate.opsForValue().get(key);
                if (post != null) {
                    mongoTemplate.save(post);
                    log.info("[핫 게시글 백업] DB 저장 완료 - key: {}", key);
                }
                postRedisTemplate.delete(key);
                log.info("[핫 게시글 캐시 삭제] TTL 만료로 Redis에서 제거 - key: {}", key);
            }
        }
    }
}  