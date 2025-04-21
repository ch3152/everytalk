import React, { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./Mypage.css";

function Mypage() {
  const location = useLocation();
  const navigate = useNavigate();
  const data = location.state;

  useEffect(() => {
    if (!data) {
      // 새로고침 시 데이터 없음 → 다시 API 요청 유도 or 홈으로
      alert("잘못된 접근입니다. 다시 로그인해주세요.");
      navigate("/");
    }
  }, [data, navigate]);

  if (!data) return null;

  return (
    <div className="mypage-container">
      <h1 className="mypage-title"> 마이페이지</h1>
      <div className="mypage-info">
        <p><strong>아이디:</strong> {data.username}</p>
        <p><strong>이름:</strong> {data.name}</p>
        <p><strong>닉네임:</strong> {data.nickname}</p>
        <p><strong>이메일:</strong> {data.email}</p>
        <p><strong>전화번호:</strong> {data.phoneNumber}</p>
        <p><strong>가입일자:</strong> {data.createdAt?.split("T")[0]}</p>
      </div>
      <div className="mypage-stats">
        <p>📝 작성한 게시글 수: {data.postCount}</p>
        <p>❤️ 누른 좋아요 수: {data.likeCount}</p>
        <p>💬 작성한 댓글 수: {data.commentCount}</p>
      </div>
    </div>
  );
}

export default Mypage;
