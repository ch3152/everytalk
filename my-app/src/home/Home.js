import React, { useEffect, useRef, useState } from "react";
import "./Home.css";
import ChatBox from "../chatbox/ChatBox";
import LoadingSpinner from "../components/LoadingSpinner";

function Home() {
  const [roomId, setRoomId] = useState(null);
  const [partner, setPartner] = useState(null);
  const [showChat, setShowChat] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [recentChats, setRecentChats] = useState([]);
  const intervalRef = useRef(null);
  const localNickname = localStorage.getItem("nickname") || "익명유저";

  const handleCancel = async () => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
    const token = localStorage.getItem("token");
    try {
      await fetch("/api/chat/leave", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });
    } catch (err) {
      console.error("퇴장 API 실패:", err);
    }
    setRoomId(null);
    setPartner(null);
    setShowChat(false);
    setIsLoading(false);
  };

  const handleStartChat = async () => {
    const token = localStorage.getItem("token");
    if (!token) return alert("로그인이 필요합니다");

    await fetch("/api/chat/leave", {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });

    setIsLoading(true);
    try {
      const res = await fetch("/api/chat/enter-random", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      const data = await res.json();
      if (data.roomId === "waiting") {
        intervalRef.current = setInterval(async () => {
          try {
            const checkRes = await fetch("/api/chat/check-room", {
              headers: { Authorization: `Bearer ${token}` },
            });
            const checkData = await checkRes.json();
            if (checkData.roomId !== "waiting") {
              clearInterval(intervalRef.current);
              intervalRef.current = null;
              setRoomId(checkData.roomId);
              setPartner(checkData.partner);
              setShowChat(true);
              setIsLoading(false);
            }
          } catch (err) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
            setIsLoading(false);
          }
        }, 1000);
      } else {
        setRoomId(data.roomId);
        setPartner(data.partner);
        setShowChat(true);
        setIsLoading(false);
      }
    } catch (err) {
      alert("랜덤방 생성 실패: " + err.message);
      console.error("❌ 랜덤방 요청 실패", err);
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const fetchRecentChats = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await fetch("/api/chat/recent", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const data = await res.json();
          setRecentChats(data);
        }
      } catch (error) {
        console.error("최근 채팅 fetch 에러:", error);
      }
    };
    fetchRecentChats();
  }, [showChat]);

  return (
    <div className="home-container">
      <div className="main-layout">
        <div className="main-left">
          <div className="main-content">
            <h1 className="main-title">
              애인, 친구, 새로운 도파민이 필요하다면? <br />지금 바로 대화를 시작해보세요.
            </h1>
            <button className="start-button" onClick={handleStartChat}>
              일 대 일 일상 대화 랜덤톡 시작하기
            </button>
          </div>
        </div>

        <div className="main-right">
          <div className="room-box">
            <h3>내가 참여중인 방</h3>
            <ul className="room-list">
              {recentChats.length === 0 ? (
                <li>최근 대화 기록이 없습니다.</li>
              ) : (
                recentChats.map((room) => {
                  const partnerNickname = room.userA === localNickname ? room.userB : room.userA;
                  return (
                    <li
                      key={room.roomId}
                      onClick={() => {
                        setRoomId(room.roomId);
                        setPartner(partnerNickname);
                        setShowChat(true);
                      }}
                    >
                      <div>{partnerNickname}님과의 대화</div>
                      <div className="last-message">{room.lastMessage}</div>
                      <div className="last-time">
                        {new Date(room.lastTimestamp).toLocaleString("ko-KR", {
                          year: "numeric",
                          month: "2-digit",
                          day: "2-digit",
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </div>
                    </li>
                  );
                })
              )}
            </ul>
          </div>
        </div>
      </div>

      {isLoading && <LoadingSpinner text="상대방을 찾고 있어요..." onCancel={handleCancel} />}

      {showChat && roomId && <ChatBox roomId={roomId} partnerNickname={partner} />}
    </div>
  );
}

export default Home;
