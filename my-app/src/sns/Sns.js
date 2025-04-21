import React, { useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import "./Sns.css";

function Sns() {
  const [hotPosts, setHotPosts] = useState([]);
  const [normalPosts, setNormalPosts] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [newPost, setNewPost] = useState("");
  const [newComments, setNewComments] = useState({});
  const [expandedPosts, setExpandedPosts] = useState({});
  const observer = useRef(null);
  const observedSet = useRef(new Set());

  const [params] = useSearchParams();
  const keyword = params.get("keyword");

  useEffect(() => {
    const fetchSearchResults = async () => {
      const res = await fetch(`/api/sns/search?keyword=${encodeURIComponent(keyword)}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      const data = await res.json();
      setSearchResults(data);
    };
  
    if (keyword) fetchSearchResults();
    else fetchPosts();
  }, [keyword]);
  

  useEffect(() => {
    if (observer.current) observer.current.disconnect();

    observer.current = new IntersectionObserver((entries) => {
      entries.forEach(async (entry) => {
        const postId = entry.target.getAttribute("data-id");
        if (entry.isIntersecting && !observedSet.current.has(postId)) {
          observedSet.current.add(postId);
          await fetch(`/api/sns/view/${postId}`, {
            method: "POST",
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          });
        }
      });
    }, { threshold: 1.0 });

    const targets = keyword ? searchResults : [...hotPosts, ...normalPosts];
    targets.forEach((post) => {
      const el = document.querySelector(`#post-${post.id}`);
      if (el) observer.current.observe(el);
    });
  }, [hotPosts, normalPosts, searchResults, keyword]);

  const fetchPosts = async () => {
    const res = await fetch("/api/sns/separated", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    const data = await res.json();
    setHotPosts(data.hotPosts || []);
    setNormalPosts(data.normalPosts || []);
  };

  const fetchSearchResults = async () => {
    const res = await fetch(`/api/sns/search?keyword=${encodeURIComponent(keyword)}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    const data = await res.json();
    setSearchResults(data);
  };

  const handlePost = async () => {
    if (!newPost.trim()) return;
    await fetch("/api/sns/post", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify({ content: newPost }),
    });
    setNewPost("");
    keyword ? fetchSearchResults() : fetchPosts();
  };

  const handleLike = async (postId) => {
    await fetch(`/api/sns/like/${postId}`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    keyword ? fetchSearchResults() : fetchPosts();
  };

  const handleCommentSubmit = async (postId) => {
    const commentText = newComments[postId]?.trim();
    if (!commentText) return;
    await fetch(`/api/sns/comment/${postId}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
      body: JSON.stringify({ comment: commentText }),
    });
    setNewComments({ ...newComments, [postId]: "" });
    keyword ? fetchSearchResults() : fetchPosts();
  };

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

  const renderPost = (post) => {
    const isLong = post.content.length > 100;
    const isExpanded = expandedPosts[post.id];
    const contentToShow = isLong && !isExpanded
      ? post.content.slice(0, 100) + "..."
      : post.content;

    return (
      <div
        key={post.id}
        id={`post-${post.id}`}
        className="sns-post"
        data-id={post.id}
      >
        <div className="post-header">
          <span className="nickname">{post.nickname}</span>
          <span className="post-time">{formatTime(post.createdAt)}</span>
          {post.isHot && <span className="hot-label">🔥 인기 게시물</span>}
        </div>

        <p>{contentToShow}</p>
        {isLong && !isExpanded && (
          <button
            className="expand-btn"
            onClick={() =>
              setExpandedPosts((prev) => ({ ...prev, [post.id]: true }))
            }
          >
            더보기
          </button>
        )}

        <div className="post-actions">
          <div className="post-buttons">
            <button onClick={() => handleLike(post.id)}>
              ❤️ {post.likedUsers?.length || 0}
            </button>
            <button
              onClick={() => {
                const toggle = (arr) =>
                  arr.map((p) =>
                    p.id === post.id
                      ? { ...p, showComments: !p.showComments }
                      : p
                  );

                if (keyword) {
                  setSearchResults(toggle(searchResults));
                } else {
                  setHotPosts(toggle(hotPosts));
                  setNormalPosts(toggle(normalPosts));
                }
              }}
            >
              💬 {post.comments?.length || 0}
            </button>
          </div>
          <div className="post-views">👁 {post.viewCount || 0}</div>
        </div>

        {post.showComments && (
          <div className="comment-section">
            <div className="comment-list">
              {post.comments?.map((cmt, i) => (
                <div key={i} className="comment-item">
                  <strong>{cmt.nickname}</strong>: {cmt.content}
                  <div className="comment-time">{formatTime(cmt.createdAt)}</div>
                </div>
              ))}
            </div>
            <div className="comment-input">
              <input
                type="text"
                placeholder="댓글을 입력하세요"
                value={newComments[post.id] || ""}
                onChange={(e) =>
                  setNewComments({
                    ...newComments,
                    [post.id]: e.target.value,
                  })
                }
              />
              <button onClick={() => handleCommentSubmit(post.id)}>등록</button>
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <div className="sns-page">
      <div className="sns-content">
        <h2 className="sns-title">
          {keyword ? `🔍 검색 결과: "${keyword}"` : "SNS 게시판"}
        </h2>

        {!keyword && (
          <div className="sns-post-input">
            <textarea
              placeholder="내용을 입력하세요..."
              value={newPost}
              onChange={(e) => setNewPost(e.target.value)}
            />
            <button onClick={handlePost}>글쓰기</button>
          </div>
        )}

        {keyword ? (
          searchResults.length > 0 ? (
            searchResults.map(renderPost)
          ) : (
            <p>검색 결과가 없습니다.</p>
          )
        ) : (
          <>
            {hotPosts.length > 0 && (
              <div className="hot-posts">
                {hotPosts.map((post) => renderPost({ ...post, isHot: true }))}
              </div>
            )}
            {normalPosts.map((post) => renderPost(post))}
          </>
        )}
      </div>
    </div>
  );
}

export default Sns;
