// src/components/LoadingSpinner.js
import React from "react";
import "./LoadingSpinner.css";

function LoadingSpinner({ text = "상대방을 찾고 있어요...", onCancel }) {
  return (
    <div className="loading-wrapper">
      <div className="spinner" />
      <div className="loading-text">{text}</div>
      {onCancel && (
        <button className="cancel-button" onClick={onCancel}>
          ❌ 취소하고 돌아가기
        </button>
      )}
    </div>
  );
}

export default LoadingSpinner;
