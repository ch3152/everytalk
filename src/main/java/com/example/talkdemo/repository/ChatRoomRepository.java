package com.example.talkdemo.repository;

import com.example.talkdemo.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    // 사용자가 참여한 채팅방 목록을 최근 업데이트 순으로 조회
    List<ChatRoom> findByUserAOrUserBOrderByLastTimestampDesc(String userA, String userB);
}
