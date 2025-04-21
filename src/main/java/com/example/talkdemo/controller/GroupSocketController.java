package com.example.talkdemo.controller;

import com.example.talkdemo.model.GroupMessage;
import com.example.talkdemo.service.GroupTalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GroupSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GroupTalkService groupTalkService;

    @MessageMapping("/group.send")
    public void sendGroupMessage(GroupMessage message) {
        log.info("📨 단톡 메시지 수신: {}", message);
        groupTalkService.saveGroupMessageToRedis(message);
        messagingTemplate.convertAndSend("/topic/group/" + message.getRoomId(), message);
    }
}
