package com.example.talkdemo.controller;

import com.example.talkdemo.model.Post;
import com.example.talkdemo.service.PostService;
import com.example.talkdemo.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// SNS 게시판 컨트롤러
@RestController
@RequestMapping("/api/sns")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    // 게시글 작성
    @PostMapping("/post")
    public Post createPost(@RequestHeader("Authorization") String authHeader,
                           @RequestBody Map<String, String> request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.createPost(nickname, request.get("content"));
    }

    // 전체 게시글 조회 (DB)
    @GetMapping("/all")
    public List<Post> getAllFromDb() {
        return postService.getAllPosts();
    }

    // 캐시+DB 병합 게시글 조회 (isHot 포함)
    @GetMapping("/cache-or-db")
    public List<Map<String, Object>> getMergedPostsWithHotFlag() {
        List<Post> posts = postService.getMergedPosts();
        Set<String> hotIds = postService.getHotPostIdsFromRedis();

        return posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("nickname", post.getNickname());
            map.put("content", post.getContent());
            map.put("createdAt", post.getCreatedAt());
            map.put("viewCount", post.getViewCount());
            map.put("likedUsers", post.getLikedUsers());
            map.put("comments", post.getComments());
            if (hotIds.contains(post.getId())) {
                map.put("isHot", true);
            }
            return map;
        }).collect(Collectors.toList());
    }

    // 게시글 좋아요
    @PostMapping("/like/{postId}")
    public Post likePost(@RequestHeader("Authorization") String authHeader,
                         @PathVariable String postId) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.likePost(postId, nickname);
    }

    // 게시글 댓글 작성
    @PostMapping("/comment/{postId}")
    public Post addComment(@RequestHeader("Authorization") String authHeader,
                           @PathVariable String postId,
                           @RequestBody Map<String, String> request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.addComment(postId, nickname, request.get("comment"));
    }

    // 게시글 조회수 증가
    @PostMapping("/view/{postId}")
    public void increaseView(@RequestHeader("Authorization") String authHeader,
                             @PathVariable String postId,
                             HttpServletRequest request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        String ip = request.getRemoteAddr();
        postService.increaseViewCount(postId, nickname, ip);
    }

    // 핫 게시글과 일반 게시글 분리 조회
    @GetMapping("/separated")
    public ResponseEntity<?> getSeparatedPosts() {
        try {
            List<Post> allPosts = postService.getAllPosts();
            List<Post> hotPosts = postService.getHotPostsFromRedis();

            if (allPosts == null) allPosts = new ArrayList<>();
            if (hotPosts == null) hotPosts = new ArrayList<>();

            Set<String> hotIds = hotPosts.stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

            List<Map<String, Object>> hotPostDtos = hotPosts.stream().map(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", post.getId());
                map.put("nickname", post.getNickname());
                map.put("content", post.getContent());
                map.put("createdAt", post.getCreatedAt());
                map.put("viewCount", post.getViewCount());
                map.put("likedUsers", post.getLikedUsers());
                map.put("comments", post.getComments());
                map.put("isHot", true);
                return map;
            }).toList();

            List<Map<String, Object>> normalPostDtos = allPosts.stream()
                .filter(p -> !hotIds.contains(p.getId()))
                .map(post -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", post.getId());
                    map.put("nickname", post.getNickname());
                    map.put("content", post.getContent());
                    map.put("createdAt", post.getCreatedAt());
                    map.put("viewCount", post.getViewCount());
                    map.put("likedUsers", post.getLikedUsers());
                    map.put("comments", post.getComments());
                    return map;
                }).toList();

            return ResponseEntity.ok(Map.of(
                "hotPosts", hotPostDtos,
                "normalPosts", normalPostDtos
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 에러: " + e.getMessage());
        }
    }

    // 게시글 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }
}
