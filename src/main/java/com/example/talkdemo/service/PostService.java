package com.example.talkdemo.service;

import com.example.talkdemo.model.Post;
import com.example.talkdemo.model.ViewRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Post> postRedisTemplate;

    private static final String HOT_KEY_PREFIX = "hot:post:";
    private static final int HOT_THRESHOLD = 5;
    private static final long HOT_TTL_SECONDS = 180;

    // 게시글 생성
    public Post createPost(String nickname, String content) {
        Post post = new Post();
        post.setNickname(nickname);
        post.setContent(content);
        return mongoTemplate.save(post);
    }

    // 전체 게시글 조회
    public List<Post> getAllPosts() {
        return mongoTemplate.find(
            new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")),
            Post.class
        );
    }

    // Redis에서 핫 게시글만 조회
    public List<Post> getHotPostsFromRedis() {
        Set<String> keys = postRedisTemplate.keys(HOT_KEY_PREFIX + "*");
        if (keys == null) return new ArrayList<>();

        return keys.stream()
            .map(key -> postRedisTemplate.opsForValue().get(key))
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    // DB/Redis 병합 게시글 조회
    public List<Post> getMergedPosts() {
        List<Post> allDbPosts = getAllPosts();
        List<Post> finalList = new ArrayList<>();
        for (Post dbPost : allDbPosts) {
            String key = HOT_KEY_PREFIX + dbPost.getId();
            Post cached = postRedisTemplate.opsForValue().get(key);
            finalList.add(cached != null ? cached : dbPost);
        }
        return finalList;
    }

    // 조회수 증가 및 중복 방지
    public void increaseViewCount(String postId, String nickname, String ip) {
        LocalDate today = LocalDate.now();

        Query checkQuery = new Query(new Criteria().andOperator(
            Criteria.where("postId").is(postId),
            Criteria.where("date").is(today),
            new Criteria().orOperator(
                Criteria.where("nickname").is(nickname),
                Criteria.where("ip").is(ip)
            )
        ));

        boolean alreadyViewed = mongoTemplate.exists(checkQuery, ViewRecord.class);

        if (!alreadyViewed) {
            Post post = mongoTemplate.findById(postId, Post.class);
            if (post != null) {
                post.setViewCount(post.getViewCount() + 1);
                saveOrCacheHot(post);

                ViewRecord record = new ViewRecord();
                record.setPostId(postId);
                record.setNickname(nickname);
                record.setIp(ip);
                record.setDate(today);
                mongoTemplate.save(record);
            }
        }
    }

    // 게시글 좋아요 처리
    public Post likePost(String postId, String nickname) {
        Post post = mongoTemplate.findById(postId, Post.class);
        if (post == null) throw new RuntimeException("게시글 없음");

        boolean alreadyLiked = post.getLikedUsers().stream()
            .anyMatch(like -> like.getNickname().equals(nickname));

        if (!alreadyLiked) {
            Post.LikeInfo likeInfo = new Post.LikeInfo();
            likeInfo.setNickname(nickname);
            post.getLikedUsers().add(likeInfo);
            saveOrCacheHot(post);
        }
        return post;
    }

    // 게시글 댓글 추가 처리
    public Post addComment(String postId, String nickname, String commentContent) {
        Post post = mongoTemplate.findById(postId, Post.class);
        if (post == null) throw new RuntimeException("게시글 없음");

        Post.Comment comment = new Post.Comment();
        comment.setNickname(nickname);
        comment.setContent(commentContent);
        post.getComments().add(comment);
        saveOrCacheHot(post);
        return post;
    }

    // 핫 게시글 조건 판단 후 Redis 저장 또는 DB 저장
    private void saveOrCacheHot(Post post) {
        String key = HOT_KEY_PREFIX + post.getId();
        String trafficKey = "traffic:" + post.getId();

        Long trafficCount = postRedisTemplate.opsForValue().increment(trafficKey);

        if (trafficCount != null && trafficCount == 1L) {
            postRedisTemplate.expire(trafficKey, 3, TimeUnit.MINUTES);
        }

        if (trafficCount != null && trafficCount >= HOT_THRESHOLD) {
            Post merged = post;

            if (postRedisTemplate.hasKey(key)) {
                Post cached = postRedisTemplate.opsForValue().get(key);
                if (cached != null) {
                    Set<Post.Comment> mergedComments = new LinkedHashSet<>(cached.getComments());
                    mergedComments.addAll(post.getComments());

                    Set<Post.LikeInfo> mergedLikes = new LinkedHashSet<>(cached.getLikedUsers());
                    mergedLikes.addAll(post.getLikedUsers());

                    cached.setComments(new ArrayList<>(mergedComments));
                    cached.setLikedUsers(new ArrayList<>(mergedLikes));
                    cached.setViewCount(post.getViewCount());
                    cached.setContent(post.getContent());
                    merged = cached;
                }
            }

            postRedisTemplate.opsForValue().set(key, merged, HOT_TTL_SECONDS, TimeUnit.SECONDS);
        } else {
            mongoTemplate.save(post);
        }
    }

    // Redis에 존재하는 핫 게시글 ID 조회
    public Set<String> getHotPostIdsFromRedis() {
        Set<String> keys = postRedisTemplate.keys("hot:post:*");
        if (keys == null) return Collections.emptySet();

        return keys.stream()
            .map(k -> k.replace("hot:post:", ""))
            .collect(Collectors.toSet());
    }

    // 게시글 검색 (본문, 닉네임, 댓글 내용 기준)
    public List<Post> searchPosts(String keyword) {
        Criteria contentCriteria = Criteria.where("content").regex(".*" + keyword + ".*", "i");
        Criteria nicknameCriteria = Criteria.where("nickname").regex(".*" + keyword + ".*", "i");
        Criteria commentCriteria = Criteria.where("comments.content").regex(".*" + keyword + ".*", "i");

        Query query = new Query(new Criteria().orOperator(
            contentCriteria, nicknameCriteria, commentCriteria
        )).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        return mongoTemplate.find(query, Post.class);
    }
}