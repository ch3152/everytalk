import React, { useEffect, useState } from "react";
import "./Group.css";
import ChatBox from "../chatbox/ChatBox";

function Group() {
  const [groupRooms, setGroupRooms] = useState([]);
  const [myRooms, setMyRooms] = useState([]);
  const [selectedRoomId, setSelectedRoomId] = useState(null);

  const fetchGroupRooms = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch("/api/group/all", {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();
    setGroupRooms(data);
  };

  const fetchMyRooms = async () => {
    const token = localStorage.getItem("token");
    const res = await fetch("/api/group/my", {
      headers: { Authorization: `Bearer ${token}` },
    });
    const data = await res.json();
    setMyRooms(data);
  };

  const handleCreateRoom = async () => {
    const title = prompt("방 제목을 입력하세요:");
    const token = localStorage.getItem("token");
    if (!title || !token) return;

    const res = await fetch("/api/group/create", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ title }),
    });

    const newRoom = await res.json();
    await fetchGroupRooms();
    await fetchMyRooms();
    setSelectedRoomId(newRoom.id);
  };

  const handleRoomClick = async (roomId) => {
    const token = localStorage.getItem("token");
    await fetch(`/api/group/enter/${roomId}`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
    setSelectedRoomId(roomId);
    await fetchMyRooms();
  };

  useEffect(() => {
    fetchGroupRooms();
    fetchMyRooms();
  }, []);

  return (
    <div className="group-page">
      <div className="group-left">
        <div className="group-title-box">
          <h2 className="group-title">단톡방 리스트</h2>
          <button className="create-button" onClick={handleCreateRoom}>
            + 방 생성하기
          </button>
        </div>
        <div className="room-list-container">
          {groupRooms.map((room) => (
            <div
              className="room-card"
              key={room.id}
              onClick={() => handleRoomClick(room.id)}
            >
              <h3>{room.title}</h3>
              <p>방장: {room.host}</p>
              <p>인원: {room.members.length}명</p>
              <p>생성일: {new Date(room.createdAt).toLocaleDateString("ko-KR")}</p>
            </div>
          ))}
        </div>
      </div>

      <div className="group-right">
        <div className="room-box">
          <h3>내가 참여중인 방</h3>
          <ul className="room-list">
            {myRooms.map((room) => (
              <li key={room.id} onClick={() => handleRoomClick(room.id)}>
                <div className="chat-summary">
                  <div className="chat-nickname">{room.title}</div>
                  <div className="last-time">
                    {new Date(room.createdAt).toLocaleString("ko-KR", {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </div>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>

      {selectedRoomId && (
        <div className="floating-chat">
          <ChatBox roomId={selectedRoomId} isGroup={true} />
        </div>
      )}
    </div>
  );
}

export default Group;
