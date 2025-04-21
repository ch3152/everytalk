import React, { useEffect, useState } from "react";
import "./Layout.css";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

function Layout() {
  const location = useLocation();
  const navigate = useNavigate();
  const hideHeaderOnRoutes = ["/", "/signup"];
  const shouldHideHeader = hideHeaderOnRoutes.includes(location.pathname);

  const [nickname, setNickname] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setNickname(decoded.nickname || "익명유저");
      } catch (e) {
        console.error("토큰 파싱 실패", e);
      }
    }
  }, []);

  const goToMypage = async () => {
    try {
      const res = await fetch("/api/users/mypage", {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      const data = await res.json();
      navigate("/mypage", { state: data });
    } catch (e) {
      console.error("마이페이지 불러오기 실패", e);
      alert("마이페이지 정보를 불러오지 못했습니다.");
    }
  };

  return (
    <div className="layout-container">
      {!shouldHideHeader && (
        <header className="home-header">
          <div className="nav-left">
            <h1 className="logo-text">BABATALK</h1>
            <button className="menu-button" onClick={() => navigate("/home")}>랜덤톡</button>
            <button className="menu-button" onClick={() => navigate("/group")}>단톡</button>
            <button className="menu-button" onClick={() => navigate("/sns")}>SNS게시판</button>
          </div>

          <div className="nav-center">
            <input
              type="text"
              placeholder=" 검색어를 입력하세요"
              className="search-box"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  navigate(`/sns?keyword=${encodeURIComponent(searchTerm)}`);
                }
              }}
            />
          </div>

          <div className="nav-right">
            <span className="nickname">{nickname}</span>
            <button className="nav-button" onClick={() => navigate("/")}>로그아웃</button>
            <button className="nav-button" onClick={goToMypage}>마이페이지</button>
          </div>
        </header>
      )}

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
