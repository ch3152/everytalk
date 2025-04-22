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

## 💻 개발 스택 선택 및 이유
`Java 17` `Spring Boot` `MongoDB` `Redis` `HTML` `CSS` `React` `JWT` `WebSocket (STOMP)` `AWS EC2` `MongoDB Atlas`

#### Java + Spring Boot:
→ 객체지향 기반의 안정적인 구조와 다양한 라이브러리 지원 및 대규모 서비스 운영에 적합하기 때문에 사용했습니다.

#### MVC 패턴:
→ 역할별로 기능을 분리하여 유지보수와 테스트가 용이하도록 구성하였습니다.

#### JWT (JSON Web Token):
→ 로그인 이후에도 서버가 세션 상태를 저장하지 않고도 사용자를 인증할 수 있도록 하기 위해 사용했습니다.

#### STOMP + WebSocket:
→ 실시간 양방향 통신을 위해 WebSocket을 사용하고  STOMP 프로토콜을 통해 메세지를 효율적으로 관리하고 구독할 수 있도록 구성했습니다.

#### Redis:
→ 캐싱과 실시간 채팅 로그 임시 저장을 통해 DB 부하를 줄이고 빠른 응답을 제공하기 위해 사용했습니다.

#### MongoDB:
→ 다양한 형태의 데이터를 유연하게 저장하고  채팅/게시글처럼 구조가 자주 변하는 데이터 구조를 효율적으로 관리하기 위해  테이블 구조가 유연한 NO-SQL인 MongoDB를 선택했습니다.

#### AWS EC2 :
→ 한글을 지원 및 무료버전이 있으며 실제 서비스를 배포하고 외부에서도 접근 가능한 테스트 환경을 제공하기 위해 클라우드 기반 인프라로 구성했습니다.


## 제작기간

2025 2.25~2025 4.22

## 아키텍처



## 테이블구성
<details>
    <summary>🔽 [더보기 클릭!] 👉 테이블 구성 정보 (테이블 설계도 및 칼럼 설명)</summary>

  
![image](https://github.com/user-attachments/assets/1e7a9dd8-fb6d-4059-9a42-01292fe467f5)



## 1 유저테이블

![image](https://github.com/user-attachments/assets/8823b63d-df8a-4412-ba30-9117e3779e6e)



회원가입 시 유저의 기본 정보가 저장됩니다.


| 키            | 데이터 타입 | 설명                                |
|----------------|--------------|-------------------------------------|
| `_id`          | ObjectId     | 유저 고유 ID                        |
| `username`     | String       | 로그인용 아이디                     |
| `nickname`     | String       | 유저 닉네임                         |
| `password`     | String       | 암호화된 비밀번호 (BCrypt 적용)    |
| `name`         | String       | 실명                                |
| `phoneNumber`  | String       | 전화번호                            |
| `email`        | String       | 이메일 주소                         |
| `birthDate`    | String       | 생년월일 (yyyy-mm-dd 형식)         |
| `createdAt`    | Date         | 가입일                              |
| `_class`       | String       | 매핑된 자바 클래스 정보 (`User`)   |


## 2 일 대일 방 테이블


![image](https://github.com/user-attachments/assets/c726b491-43a0-40b8-b4d7-1badded46d64)


일 대일 채팅이 매칭 될 시 방 번호랑 유저 정보가 저장됩니다.

| 키             | 데이터 타입 | 설명                                        |
|----------------|-------------|---------------------------------------------|
| `_id`          | ObjectId    | 1:1 채팅방 고유 ID                           |
| `userA`        | String      | 유저 A 닉네임                                |
| `userB`        | String      | 유저 B 닉네임                                |
| `lastMessage`  | String      | 마지막 메시지 내용                           |
| `lastTimestamp`| Date        | 마지막 메시지 시간                           |
| `_class`       | String      | 매핑된 자바 클래스 정보 (`ChatRoom`)         |


## 3 일 대일 대화 테이블

![image](https://github.com/user-attachments/assets/6854fcb4-3b14-48d7-8b90-c7e333ad924f)

일 대일 방에서 대화 시 채팅 내용과 기록이 저장됩니다.

| 키           | 데이터 타입 | 설명                                        |
|--------------|-------------|---------------------------------------------|
| `_id`        | ObjectId    | 메시지 고유 ID                              |
| `roomId`     | String      | 채팅방 ID                                   |
| `sender`     | String      | 보낸 사람 닉네임                            |
| `content`    | String      | 메시지 내용                                 |
| `createdAt`  | Date        | 메시지 작성 시간                            |
| `_class`     | String      | 매핑된 자바 클래스 정보 (`ChatDocument`)    |



## 4 단체톡 방 테이블


![image](https://github.com/user-attachments/assets/48c26f0a-fda0-4f33-bfa0-e57f849702b7)

단체톡방을 만들시 방 id랑  유저들이 참여시 리스트가 저장됩니다.

| 키           | 데이터 타입    | 설명                                              |
|--------------|----------------|---------------------------------------------------|
| `_id`        | ObjectId       | 그룹 방 고유 ID                                   |
| `title`      | String         | 그룹 방 제목                                      |
| `host`       | String         | 방장 닉네임                                       |
| `members`    | Array[String]  | 참가자 닉네임 리스트                             |
| `createdAt`  | Date           | 생성 일시                                         |
| `_class`     | String         | 매핑된 자바 클래스 정보 (`GroupRoom`)            |


## 5 단체톡 대화 테이블

![image](https://github.com/user-attachments/assets/b638631f-b328-4932-8c3e-6f52384fa6a1)
단체톡 방에서 유저들의 대화내용이 저장됩니다.

| 키           | 데이터 타입 | 설명                                               |
|--------------|-------------|----------------------------------------------------|
| `_id`        | ObjectId    | 메시지 고유 ID                                     |
| `roomId`     | String      | 그룹 채팅방 ID                                     |
| `sender`     | String      | 보낸 사람 닉네임                                   |
| `content`    | String      | 메시지 내용                                        |
| `timestamp`  | Long        | 메시지 유닉스 타임스탬프                           |
| `savedAt`    | Date        | 메시지 저장 시간                                   |
| `_class`     | String      | 매핑된 자바 클래스 정보 (`GroupChatDocument`)     |


## 6 Sns게시물 테이블


![image](https://github.com/user-attachments/assets/842aaed3-c49c-43e5-9651-7c12cc9e31b4)

Sns 게시물을 올리고 유저들의 댓글이나 좋아요의 정보가 저장됩니다.

| 키            | 데이터 타입    | 설명                                               |
|---------------|----------------|----------------------------------------------------|
| `_id`         | ObjectId       | 게시글 고유 ID                                      |
| `nickname`    | String         | 게시글 작성자 닉네임                                |
| `content`     | String         | 게시글 내용                                         |
| `createdAt`   | Date           | 게시글 작성 시간                                    |
| `viewCount`   | Int            | 게시글 조회 수                                      |
| `likedUsers`  | Array[Object]  | 좋아요 누른 유저 배열 (`nickname`, `likedAt` 포함) |
| `comments`    | Array[Object]  | 댓글 배열 (`nickname`, `content`, `createdAt` 포함)|
| `_class`      | String         | 매핑된 자바 클래스 정보 (`Post`)                   |


## 7 Sns게시물 조회수 테이블


![image](https://github.com/user-attachments/assets/36c5a3e2-a187-4795-a163-6c43439cbe61)
Sns 게시물을 누가 조회 했는지의 정보가 저장됩니다.

| 키         | 데이터 타입 | 설명                                           |
|------------|-------------|------------------------------------------------|
| `_id`      | ObjectId    | 조회 기록 고유 ID                              |
| `postId`   | String      | 게시물 ID                                      |
| `nickname` | String      | 조회한 유저 닉네임                             |
| `ip`       | String      | 유저 IP 주소                                   |
| `date`     | Date        | 조회 일자                                      |
| `_class`   | String      | 매핑된 자바 클래스 정보 (`ViewRecord`)         |




## 핵심 기능 & 

## 트러블슈팅

## 회고록

