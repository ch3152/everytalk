# 🗨️ 바바톡 (Everytalk-Sns-Service)  
### WebSocket 기반 실시간 익명 채팅 & SNS 서비스

모든 사용자가 익명으로 참여 가능한 실시간 웹 커뮤니케이션 서비스입니다.  
1:1 랜덤 대화, 단체 채팅방, SNS 게시판을 통해 감정이나 일상을 공유할 수 있도록 설계되었습니다.

- 실시간 메세지: STOMP + WebSocket 기반
- 인증: JWT (토큰 기반 인증 시스템)
- 저장소: Redis(캐싱, 채팅 로그 임시 저장), MongoDB(영구 데이터 저장)
- 배포: AWS EC2 기반 Linux 환경, Atlas MongoDB 연동
- url 주소 : http://13.124.207.177:3000/  (실시간 배포 운영중!!)

<br>
<br>

## 💻 개발 스택
`Java 17` `Spring Boot` `MongoDB` `Redis` `HTML` `CSS` `React` `JWT` `WebSocket (STOMP)` `AWS EC2` `MongoDB Atlas`


## 제작기간

2025 2.25~2025 4.22

## 아키텍처



## 테이블구성
<details>
  <summary> 테이블 구성 정보(테이블 설계도 및 칼럼 설명)</summary>
  
![image](https://github.com/user-attachments/assets/1e7a9dd8-fb6d-4059-9a42-01292fe467f5)



1 유저테이블

![image](https://github.com/user-attachments/assets/e320909a-a020-4c60-b244-e41085ca5edf)



2 일 대일 방 테이블


![image](https://github.com/user-attachments/assets/c726b491-43a0-40b8-b4d7-1badded46d64)

3 일 대일 대화 테이블

![image](https://github.com/user-attachments/assets/6854fcb4-3b14-48d7-8b90-c7e333ad924f)

4 단체톡 방 테이블


![image](https://github.com/user-attachments/assets/48c26f0a-fda0-4f33-bfa0-e57f849702b7)

5 단체톡 대화 테이블

![image](https://github.com/user-attachments/assets/b638631f-b328-4932-8c3e-6f52384fa6a1)

6 Sns게시판 테이블


![image](https://github.com/user-attachments/assets/842aaed3-c49c-43e5-9651-7c12cc9e31b4)

7 Sns게시판 조회수 테이블


![image](https://github.com/user-attachments/assets/36c5a3e2-a187-4795-a163-6c43439cbe61)





## 핵심 기능 & 

## 트러블슈팅

## 회고록

