/*
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import "./Sns.css"; // 공용 스타일 그대로 사용

function SnsSearch() {
  const [params] = useSearchParams();
  const keyword = params.get("keyword");
  const [searchResults, setSearchResults] = useState([]);

  useEffect(() => {
    if (keyword) {
      fetch(`/api/sns/search?keyword=${encodeURIComponent(keyword)}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
        .then((res) => {
          if (!res.ok) throw new Error("서버 오류 발생");
          return res.json();
        })
        .then(setSearchResults)
        .catch((err) => {
          console.error("검색 실패:", err);
          setSearchResults([]); // 에러 시 빈 배열로
        });
    }
  }, [keyword]);

  const formatTime = (timeStr) => {
    const date = new Date(timeStr);
    return date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const renderPost = (post) => (
    <div key={post.id} className="sns-post">
      <div className="post-header">
        <span className="nickname">{post.nickname}</span>
        <span className="post-time">{formatTime(post.createdAt)}</span>
      </div>
      <p>{post.content}</p>
      <div className="post-actions">
        <div className="post-buttons">
          ❤️ {post.likedUsers?.length || 0}
          💬 {post.comments?.length || 0}
        </div>
        <div className="post-views">👁 {post.viewCount}</div>
      </div>
    </div>
  );

  return (
    <div className="sns-page">
      <div className="sns-content">
        <h2>🔍 검색 결과: "{keyword}"</h2>
        {searchResults.length > 0 ? (
          searchResults.map(renderPost)
        ) : (
          <p>검색 결과가 없습니다.</p>
        )}
      </div>
    </div>
  );
}

export default SnsSearch;
*/