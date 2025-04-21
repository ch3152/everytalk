package com.example.talkdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

// 단체 채팅 메시지 백업용 도큐먼트
@Data
@Document(collection = "group_chat")
public class GroupChatDocument {
    @Id
    private String id;
    private String roomId;
    private String sender;
    private String content;
    private long timestamp;
    private Date savedAt;

    public static GroupChatDocument fromMessage(GroupMessage msg) {
        GroupChatDocument doc = new GroupChatDocument();
        doc.setRoomId(msg.getRoomId());
        doc.setSender(msg.getSender());
        doc.setContent(msg.getContent());
        doc.setTimestamp(msg.getTimestamp());
        doc.setSavedAt(new Date());
        return doc;
    }
}
