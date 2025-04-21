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

@RestController
@RequestMapping("/api/sns")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    @PostMapping("/post")
    public Post createPost(@RequestHeader("Authorization") String authHeader,
                           @RequestBody Map<String, String> request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.createPost(nickname, request.get("content"));
    }

    @GetMapping("/all")
    public List<Post> getAllFromDb() {
        return postService.getAllPosts();
    }

    // 🔥 핫 게시물 여부 포함해서 posts 내려줌
    @GetMapping("/cache-or-db")
    public List<Map<String, Object>> getMergedPostsWithHotFlag() {
        List<Post> posts = postService.getMergedPosts();
        Set<String> hotIds = postService.getHotPostIdsFromRedis(); // 🔥 키 목록

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
                map.put("isHot", true); // ✅ 이게 핵심
            }
            return map;
        }).collect(Collectors.toList());
    }

    @PostMapping("/like/{postId}")
    public Post likePost(@RequestHeader("Authorization") String authHeader,
                         @PathVariable String postId) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.likePost(postId, nickname);
    }

    @PostMapping("/comment/{postId}")
    public Post addComment(@RequestHeader("Authorization") String authHeader,
                           @PathVariable String postId,
                           @RequestBody Map<String, String> request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        return postService.addComment(postId, nickname, request.get("comment"));
    }

    @PostMapping("/view/{postId}")
    public void increaseView(@RequestHeader("Authorization") String authHeader,
                             @PathVariable String postId,
                             HttpServletRequest request) {
        String nickname = jwtUtil.validateAndGetUsername(authHeader.substring(7));
        String ip = request.getRemoteAddr();
        postService.increaseViewCount(postId, nickname, ip);
    }
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
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }


}
