package com.example.talkdemo.controller;

import com.example.talkdemo.model.GroupMessage;
import com.example.talkdemo.service.GroupTalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// 단체 채팅 메시지 소켓 처리 컨트롤러
@Controller
@RequiredArgsConstructor
@Slf4j
public class GroupSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroupTalkService groupTalkService;

    // 단체 채팅 메시지 전송 처리
    @MessageMapping("/group.send")
    public void sendGroupMessage(GroupMessage message) {
        groupTalkService.saveGroupMessageToRedis(message);
        messagingTemplate.convertAndSend("/topic/group/" + message.getRoomId(), message);
    }
}
