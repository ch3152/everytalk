package com.example.talkdemo.service;

import com.example.talkdemo.model.GroupRoom;
import com.example.talkdemo.model.GroupMessage;
import com.example.talkdemo.model.GroupChatDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupTalkService {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, GroupMessage> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public GroupRoom createRoom(String title, String host) {
        GroupRoom room = new GroupRoom();
        room.setId(UUID.randomUUID().toString());
        room.setTitle(host + "님의 " + title + " 방입니다");
        room.setHost(host);
        room.setMembers(new ArrayList<>(List.of(host)));
        room.setCreatedAt(LocalDateTime.now());

        GroupRoom saved = mongoTemplate.save(room);
        return saved;
    }

    public List<GroupRoom> getAllRooms() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, GroupRoom.class);
    }

    public List<GroupRoom> getRoomsByNickname(String nickname) {
        Query query = new Query(Criteria.where("members").in(nickname))
                        .with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, GroupRoom.class);
    }

    public void saveGroupMessageToRedis(GroupMessage message) {
        String key = "group:room:" + message.getRoomId();
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, Duration.ofMinutes(3));
    }

    /**
     * 그룹 채팅 메시지를 Redis에서 먼저 조회하고,
     * 없으면 MongoDB에서 백업된 메시지를 조회하여 반환
     */
    public List<GroupMessage> getGroupMessageHistory(String roomId) {
        String key = "group:room:" + roomId;
        List<GroupMessage> messages = redisTemplate.opsForList().range(key, 0, -1);
        if (messages == null || messages.isEmpty()) {
            log.info("[그룹조회] Redis에 메시지 없음, MongoDB에서 조회 - roomId: {}", roomId);
            Query query = new Query(Criteria.where("roomId").is(roomId));
            List<GroupChatDocument> docs = mongoTemplate.find(query, GroupChatDocument.class);
            messages = docs.stream().map(doc -> {
                GroupMessage msg = new GroupMessage();
                msg.setRoomId(doc.getRoomId());
                msg.setSender(doc.getSender());
                msg.setContent(doc.getContent());
                msg.setTimestamp(doc.getTimestamp());
                return msg;
            }).collect(Collectors.toList());
            log.info("[그룹조회] MongoDB에서 복원된 메시지 개수: {}", messages.size());
        } else {
            log.info("[그룹조회] Redis에서 메시지 개수: {}", messages.size());
        }
        return messages;
    }

    public GroupRoom enterRoom(String roomId, String nickname) {
        GroupRoom room = mongoTemplate.findById(roomId, GroupRoom.class);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 방입니다: " + roomId);
        }

        if (!room.getMembers().contains(nickname)) {
            room.getMembers().add(nickname);
            mongoTemplate.save(room);
            messagingTemplate.convertAndSend("/topic/group/" + roomId, room);
        }

        return room;
    }
}
