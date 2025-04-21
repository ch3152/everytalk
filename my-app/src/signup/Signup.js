import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Signup.css";

function Signup() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState("");
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [isVerified, setIsVerified] = useState(false);
  const [nicname, setnicname] = useState("");
  const [showEmailCodeInput, setShowEmailCodeInput] = useState(false);
  const [name, setName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [birthYear, setBirthYear] = useState("");
  const [birthMonth, setBirthMonth] = useState("");
  const [birthDay, setBirthDay] = useState("");
  const [usernameCheckMessage, setUsernameCheckMessage] = useState("");
  const [isUsernameAvailable, setIsUsernameAvailable] = useState(false);
  const navigate = useNavigate();

  const [errors, setErrors] = useState({
    username: "",
    password: "",
    passwordCheck: "",
  });

  const handleSendCode = async () => {
    try {
      const res = await fetch("/api/users/send-email", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });
      const msg = await res.text();
      alert(msg);
      if (res.ok) setShowEmailCodeInput(true);
    } catch (err) {
      console.error("이메일 전송 실패:", err);
      alert("이메일 전송 실패");
    }
  };

  const handleVerifyCode = async () => {
    try {
      const res = await fetch("/api/users/verify-code", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, code }),
      });
      const msg = await res.text();
      alert(msg);
      if (res.ok) setIsVerified(true);
    } catch (err) {
      console.error("인증 실패:", err);
      alert("인증 실패");
    }
  };

  const handleUsernameChange = (e) => {
    const value = e.target.value;
    setUsername(value);
    setIsUsernameAvailable(false);
    setUsernameCheckMessage("");
    const isValid = /^[a-zA-Z0-9]{8,20}$/.test(value);
    setErrors((prev) => ({
      ...prev,
      username: isValid ? "" : "아이디는 영문/숫자 8~20자여야 합니다.",
    }));
  };

  const checkUsernameDuplicate = async () => {
    try {
      const res = await fetch(`/api/users/check-username?username=${username}`);
      const msg = await res.text();
      setUsernameCheckMessage(msg);
      setIsUsernameAvailable(res.ok);
    } catch (err) {
      console.error("아이디 중복 확인 실패:", err);
      setUsernameCheckMessage("중복 확인 실패");
    }
  };

  const handlePasswordChange = (e) => {
    const value = e.target.value;
    setPassword(value);
    const isValid = /^[a-zA-Z0-9]{8,20}$/.test(value);
    setErrors((prev) => ({
      ...prev,
      password: isValid ? "" : "비밀번호는 영문/숫자 8~20자여야 합니다.",
    }));
    if (passwordCheck && value !== passwordCheck) {
      setErrors((prev) => ({ ...prev, passwordCheck: "비밀번호가 일치하지 않습니다." }));
    } else {
      setErrors((prev) => ({ ...prev, passwordCheck: "" }));
    }
  };

  const handlePasswordCheckChange = (e) => {
    const value = e.target.value;
    setPasswordCheck(value);
    setErrors((prev) => ({
      ...prev,
      passwordCheck: value === password ? "" : "비밀번호가 일치하지 않습니다.",
    }));
  };


  const handleSignup = async () => {
    if (!username || !password || !nicname|| !passwordCheck || !email || !name || !phoneNumber || !birthYear || !birthMonth || !birthDay) {
      alert("모든 필드를 입력해주세요.");
      return;
    }
    if (!isVerified) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }
    if (!isUsernameAvailable) {
      alert("아이디 중복 확인을 완료해주세요.");
      return;
    }
    if (errors.username || errors.password || errors.passwordCheck) {
      alert("회원가입에 실패했습니다. 다시한번 확인해주세요.");
      return;
    }
    const birthDate = `${birthYear}-${birthMonth.padStart(2, '0')}-${birthDay.padStart(2, '0')}`;
    try {
      const res = await fetch("/api/users/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password,nicname, email, name, phoneNumber, birthDate }),
      });
      if (res.ok) {
        alert("회원가입 성공!");
        navigate("/", { replace: true });
      } else {
        const msg = await res.text();
        alert("회원가입 실패: " + msg);
      }
    } catch (err) {
      console.error("회원가입 요청 실패:", err);
      alert("회원가입 요청 실패");
    }
  };

  return (
    <div className="home-container">
      <header className="home-header">
      <h1 className="logo-text">BABATALK</h1>
      </header>

      <div className="signup-background">
        <div className="signup-container">
          <h3 className="signup-title">회원가입</h3>

          <div className="form-group">
            <label htmlFor="username">아이디</label>
            {errors.username && <p className="error-text">{errors.username}</p>}
            <div className="input-wrapper">
              <input type="text" id="username" value={username} onChange={handleUsernameChange} placeholder="아이디 입력 (8~20자, 영문/숫자)" className="input-with-button" maxLength={20} />
              <button type="button" className="idcheck-button" onClick={checkUsernameDuplicate}>중복 확인</button>
            </div>
            {usernameCheckMessage && <p className="error-text" style={{ color: isUsernameAvailable ? 'green' : 'red' }}>{usernameCheckMessage}</p>}
          </div>


          <div className="form-group">
            <label>비밀번호</label>
            {errors.password && <p className="error-text">{errors.password}</p>}
            <input type="password" value={password} onChange={handlePasswordChange} placeholder="비밀번호 입력 (영문/숫자 8~20자)" className="input-with-button" maxLength={20} />
          </div>

          <div className="form-group">
            <label>비밀번호 확인</label>
            {errors.passwordCheck && <p className="error-text">{errors.passwordCheck}</p>}
            <input type="password" value={passwordCheck} onChange={handlePasswordCheckChange} placeholder="비밀번호 재확인" className="input-with-button" maxLength={20} />
          </div>

          <div className="form-group">
            <label>닉네임</label>
            <input type="text" placeholder="닉네임 입력" className="input-with-button" maxLength={20} value={nicname} onChange={(e) => setnicname(e.target.value)} />
            </div>

          <div className="form-group">
            <label>이름</label>
            <input type="text" placeholder="이름 입력" className="input-with-button" value={name} onChange={(e) => setName(e.target.value)} />
          </div>

          <div className="form-group">
            <label>전화번호</label>
            <input type="text" maxLength={11} placeholder="전화번호 입력(-제외 11자리 입력)" className="input-with-button" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} />
          </div>

          <div className="form-group">
            <label>이메일</label>
            <div className="input-wrapper">
              <input type="text" placeholder="이메일 입력" className="input-with-button" value={email} onChange={(e) => setEmail(e.target.value)} />
              <button type="button" className="idcheck-button" onClick={handleSendCode}>이메일 코드 발송</button>
            </div>
          </div>

          {showEmailCodeInput && (
            <div className="form-group">
              <label>이메일 인증 코드</label>
              <div className="input-wrapper">
                <input type="text" placeholder="인증 코드 입력" className="short-input" maxLength={6} value={code} onChange={(e) => setCode(e.target.value)} />
                {isVerified ? (
                  <span className="verify-success">인증 성공!!</span>
                ) : (
                  <button type="button" className="verify-button" onClick={handleVerifyCode}>확인</button>
                )}
              </div>
            </div>
          )}

          <div className="form-group">
            <label>생년월일</label>
            <div className="birth-row">
              <select className="birth-select" value={birthYear} onChange={(e) => setBirthYear(e.target.value)}>
                <option value="">년도</option>
                {Array.from({ length: 100 }, (_, i) => {
                  const year = new Date().getFullYear() - i;
                  return <option key={year} value={year}>{year}년</option>;
                })}
              </select>
              <select className="birth-select" value={birthMonth} onChange={(e) => setBirthMonth(e.target.value)}>
                <option value="">월</option>
                {Array.from({ length: 12 }, (_, i) => (
                  <option key={i + 1} value={i + 1}>{i + 1}월</option>
                ))}
              </select>
              <select className="birth-select" value={birthDay} onChange={(e) => setBirthDay(e.target.value)}>
                <option value="">일</option>
                {Array.from({ length: 31 }, (_, i) => (
                  <option key={i + 1} value={i + 1}>{i + 1}일</option>
                ))}
              </select>
            </div>
          </div>

          <div className="button-row">
            <button className="submit-button" onClick={handleSignup}>가입하기</button>
            <button className="cancel-button" onClick={() => navigate(-1)}>가입취소</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Signup;
