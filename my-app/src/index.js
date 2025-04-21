import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';  // Main.css가 아니라 index.css (혹은 App.css)
import App from './App';  // ✅ App.js를 불러와야 함!
import { BrowserRouter } from "react-router-dom"; // ✅ Router 적용 필요
import reportWebVitals from './reportWebVitals';
import './output.css';


const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>  {/* Router 추가 */}
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

// 성능 측정용 코드
reportWebVitals();
