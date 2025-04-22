// src/chatbox/ChatBox.js
import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { jwtDecode } from "jwt-decode";
import "./ChatBox.css";

function ChatBox({ roomId, partnerNickname, isGroup = false }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [roomTitle, setRoomTitle] = useState("");
  const myNicknameRef = useRef("익명유저");
  const stompClientRef = useRef(null);
  const subscriptionRef = useRef(null);

  // 내 닉네임 추출
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        myNicknameRef.current = decoded.nickname || decoded.sub || "익명유저";
      } catch (e) {
        console.error("❌ 토큰 파싱 오류:", e);
      }
    }
  }, []);

  // 채팅 기록 + 방 제목 불러오기
  useEffect(() => {
    const fetchHistory = async () => {
      const token = localStorage.getItem("token");
      const url = isGroup
        ? `/api/group/history/${roomId}`
        : `/api/chat/redis-history/${roomId}`;
      try {
        const res = await fetch(url, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const history = await res.json();
          setMessages(history);
        }
      } catch (e) {
        console.error("❌ 기록 불러오기 실패", e);
      }
    };

    const fetchTitle = async () => {
      if (!isGroup) return;
      const token = localStorage.getItem("token");
      try {
        const res = await fetch("/api/group/all", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const allRooms = await res.json();
        const matchedRoom = allRooms.find((r) => r.id === roomId);
        if (matchedRoom) setRoomTitle(matchedRoom.title);
      } catch (e) {
        console.error("❌ 방 제목 불러오기 실패", e);
      }
    };

    fetchHistory();
    fetchTitle();
  }, [roomId, isGroup]);

  // WebSocket 연결
  useEffect(() => {
    const socket = new SockJS("http://13.124.207.177:8080/ws"); // EC2 퍼블릭 IP:포트
    const client = Stomp.over(socket);
    stompClientRef.current = client;

    client.connect({}, () => {
      const destination = isGroup
        ? `/topic/group/${roomId}`
        : `/topic/room/${roomId}`;

      if (subscriptionRef.current) subscriptionRef.current.unsubscribe();

      subscriptionRef.current = client.subscribe(destination, (msg) => {
        try {
          const body = JSON.parse(msg.body);
          setMessages((prev) => [...prev, body]);
        } catch (e) {
          console.error("❌ 메시지 파싱 오류", e);
        }
      });
    });

    return () => {
      if (subscriptionRef.current) subscriptionRef.current.unsubscribe();
      if (stompClientRef.current?.connected) stompClientRef.current.disconnect();
    };
  }, [roomId, isGroup]);

  // 메시지 전송
  const sendMessage = () => {
    if (!input.trim()) return;

    const message = {
      roomId,
      sender: myNicknameRef.current,
      content: input,
      timestamp: Date.now(),
    };

    const endpoint = isGroup ? "/app/group.send" : "/app/chat.send";

    if (stompClientRef.current?.connected) {
      stompClientRef.current.send(endpoint, {}, JSON.stringify(message));
      setInput("");
    }
  };

  // 엔터 눌렀을 때 메시지 전송
  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();   // 기본 줄바꿈 막기
      sendMessage();
    }
  };

  return (
    <div className="chat-box">
      <div className="chat-header">
        <div className="chat-title">
          {isGroup
            ? roomTitle || "단체 채팅방"
            : `${partnerNickname || "상대방"}님과 대화 중`}
        </div>
        <button onClick={() => window.location.reload()}>✖</button>
      </div>

      <div className="chat-messages">
        {messages.map((msg, i) => (
          <div key={i} className={`chat-msg ${msg.sender === myNicknameRef.current ? "self" : "other"}`}>
            <div className="nickname">{msg.sender}</div>
            <div>{msg.content}</div>
            <div className="time">
              {new Date(msg.timestamp).toLocaleTimeString("ko-KR", {
                hour: "2-digit",
                minute: "2-digit",
              })}
            </div>
          </div>
        ))}
      </div>

      <div className="chat-input">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={handleKeyDown} // 여기!
          placeholder="메시지를 입력하세요"
        />
        <button onClick={sendMessage}>전송</button>
      </div>
    </div>
  );
}

export default ChatBox;
