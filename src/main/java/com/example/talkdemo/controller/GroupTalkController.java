package com.example.talkdemo.controller;

import com.example.talkdemo.model.GroupRoom;
import com.example.talkdemo.model.GroupMessage;
import com.example.talkdemo.service.GroupTalkService;
import com.example.talkdemo.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 단체 채팅방 API 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
@Slf4j
public class GroupTalkController {

    private final GroupTalkService groupTalkService;
    private final JwtUtil jwtUtil;

    // 단체 채팅방 생성
    @PostMapping("/create")
    public GroupRoom createRoom(@RequestHeader("Authorization") String authHeader,
                                @RequestBody Map<String, String> request) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        return groupTalkService.createRoom(request.get("title"), nickname);
    }

    // 전체 단체 채팅방 조회
    @GetMapping("/all")
    public List<GroupRoom> allRooms() {
        return groupTalkService.getAllRooms();
    }

    // 내가 참여한 단체 채팅방 조회
    @GetMapping("/my")
    public List<GroupRoom> myRooms(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        return groupTalkService.getRoomsByNickname(nickname);
    }

    // 채팅방 메시지 히스토리 조회
    @GetMapping("/history/{roomId}")
    public List<GroupMessage> getHistory(@PathVariable String roomId) {
        return groupTalkService.getGroupMessageHistory(roomId);
    }

    // 채팅방 입장 처리
    @PostMapping("/enter/{roomId}")
    public GroupRoom enterRoom(@RequestHeader("Authorization") String authHeader,
                               @PathVariable String roomId) {
        String token = authHeader.substring(7);
        String nickname = jwtUtil.validateAndGetUsername(token);
        return groupTalkService.enterRoom(roomId, nickname);
    }
}
