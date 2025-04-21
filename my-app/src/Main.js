import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Main.css";

function Main() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        const errorText = await res.text();
        alert("로그인 실패: " + errorText);
        return;
      }

      const data = await res.json();
      localStorage.setItem("token", data.token);      // ✅ JWT 저장
      localStorage.setItem("nickname", data.nickname); // ✅ 닉네임 저장

      alert("로그인 성공");
      navigate("/home");
    } catch (err) {
      console.error("로그인 요청 실패:", err);
      alert("서버 연결 오류 또는 예외 발생");
    }
  };

  return (
    <div
      className="main-container"
      style={{
        backgroundImage: `url(${process.env.PUBLIC_URL + "/background1.png"})`,
      }}
    >
      <div className="login-box">
        <h1>BABATALK</h1>

        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="아이디"
            className="input-box"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="비밀번호"
            className="input-box"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <div className="button-group">
            <button type="submit" className="login-btn">로그인</button>
            <button
              type="button"
              className="signup-btn"
              onClick={() => navigate("/signup")}
            >
              회원가입
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Main;
