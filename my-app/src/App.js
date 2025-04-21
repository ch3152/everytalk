// App.js
import React from "react";
import { Routes, Route } from "react-router-dom";
import Main from "./Main";
import Signup from "./signup/Signup";
import Home from "./home/Home";
import Group from "./grouptalk/Group";
import Layout from "./components/Layout";
import Sns from "./sns/Sns";
import Mypage from "./mypage/Mypage";


function App() {
  return (
    <Routes>
      <Route path="/" element={<Main />} />
      <Route path="/signup" element={<Signup />} />


      <Route element={<Layout />}>
       <Route path="/home" element={<Home />} />
      </Route>

      <Route element={<Layout />}>
        <Route path="/group" element={<Group />} />
      </Route>

      <Route element={<Layout />}>
        <Route path="/sns" element={<Sns />} />
      </Route>

      <Route element={<Layout />}>
      <Route path="/mypage" element={<Mypage />} />
    </Route>


   


    </Routes>
  );
}

export default App;
