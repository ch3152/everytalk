# 개인 프로젝트
# 🗨️ 바바톡 (Everytalk-Sns-Service)  
### WebSocket 기반 실시간 익명 채팅 & SNS 서비스

모든 사용자가 익명으로 참여 가능한 실시간 웹 커뮤니케이션 서비스입니다.  
1:1 랜덤 대화, 단체 채팅방, SNS 게시판을 통해 감정이나 일상을 공유할 수 있도록 설계되었습니다.


- 실시간 메세지: STOMP + WebSocket 기반
- 인증: JWT (토큰 기반 인증 시스템)
- 저장소: Redis(캐싱, 채팅 로그 임시 저장), MongoDB(영구 데이터 저장)
- 배포: AWS EC2 기반 Linux 환경, Atlas MongoDB 연동
- url 주소 : http://13.124.207.177:3000/  (실시간 배포 운영중!!)
-    ⬇️ **아래를 눌러 실제 구현 사진을 확인하세요!**
- <details>
    <summary>🔽 [더보기 클릭!] 👉(바바톡 페이지 실제 구현 사진)</summary>
    
  ### 1 로그인
    ![image](https://github.com/user-attachments/assets/c2d6e2cf-d58a-4299-b3dc-3007ec3aa529)
  ### 2회원가입
    ![image](https://github.com/user-attachments/assets/fbd7622e-7d3e-442e-b787-fae5ddde6a45)
  ### 3메인 화면
  ![image](https://github.com/user-attachments/assets/80d83445-7b7b-4c69-9a35-6192507d1d87)
  ### 4 일 대일 채팅 상대 찾는중
  ![image](https://github.com/user-attachments/assets/0d53ad0c-5cc5-4591-aea9-478585c4de48)
  ### 5 일 대일 매칭 후 대화창
  ![image](https://github.com/user-attachments/assets/77dde228-f31b-4c7f-bfc2-f97d6776a71b)
  ### 6 단톡방 리스트
  ![image](https://github.com/user-attachments/assets/7928e9d5-8dad-40c0-8bd7-327beddb1c66)
  ### 7 단톡방 대화창
  ![image](https://github.com/user-attachments/assets/052484db-468e-4538-b90f-523b9e0b86a8)
  ### 8 Sns 게시물 페이지
  ![image](https://github.com/user-attachments/assets/fb81654e-152b-47b2-afdf-f82db02a4722)
  ### 9 마이페이지
  ![image](https://github.com/user-attachments/assets/c249ec9e-3528-4170-a77a-556d7e9f8d06)
  </details>

  

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


![image](https://github.com/user-attachments/assets/6be2da40-1863-4cda-8627-8163ba752331)



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
| `_class`       | String       | 자바 모델 클래스 정보 (`User`)   |


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
| `_class`       | String      | 자바 모델 클래스 정보 (`ChatRoom`)         |


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
| `_class`     | String      | 자바 모델 클래스 정보 (`ChatDocument`)    |



## 4 단체톡 방 테이블


![image](https://github.com/user-attachments/assets/48c26f0a-fda0-4f33-bfa0-e57f849702b7)

단체톡방을 만들시 방 id랑  유저들이 참여시 리스트가 저장됩니다.

| 키           | 데이터 타입    | 설명                                              |
|--------------|----------------|---------------------------------------------------|
| `_id`        | ObjectId       | 그룹 방 고유 ID                                   |
| `title`      | String         | 그룹 방 제목                                      |
| `host`       | String         | 방장 닉네임                                       |
| `members`    | Array[String]  | 참가자 닉네임 리스트  (배열로 저장됨)               |
| `createdAt`  | Date           | 생성 일시                                         |
| `_class`     | String         | 자바 모델 클래스 정보 (`GroupRoom`)            |


## 5 단체톡 대화 테이블

![image](https://github.com/user-attachments/assets/b638631f-b328-4932-8c3e-6f52384fa6a1)

단체톡 방에서 유저들의 대화내용이 저장됩니다.

| 키           | 데이터 타입 | 설명                                               |
|--------------|-------------|----------------------------------------------------|
| `_id`        | ObjectId    | 메시지 고유 ID                                     |
| `roomId`     | String      | 그룹 채팅방 ID                                     |
| `sender`     | String      | 보낸 사람 닉네임                                   |
| `content`    | String      | 메시지 내용                                        |
| `timestamp`  | Long        | 메시지 보낼 때 현재 시간                          |
| `savedAt`    | Date        | 메시지 저장 시간                                   |
| `_class`     | String      | 자바 모델 클래스 정보 (`GroupChatDocument`)     |


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
| `likedUsers`  | Array[Object]  | 좋아요 누른 유저 정보 (`nickname`, `likedAt` 포함) (배열로 저장됨)  |
| `comments`    | Array[Object]  | 댓글 정보 (`nickname`, `content`, `createdAt` 포함) (배열로 저장됨) |
| `_class`      | String         |자바 모델 클래스 정보 (`Post`)                   |


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
| `_class`   | String      | 자바 모델 클래스 정보 (`ViewRecord`)         |
</details>


## 핵심 기능 

### 1. 회원가입 & 로그인

1 DB 조회를 통한 아이디 중복 방지
2 bcrypt 알고리즘으로 비밀번호 암호화

3 이메일 인증 코드 입력 완료 시 회원가입 가능

4 JWT 토큰 기반 로그인 → 동기 처리 + 보안 강화

### 2. 유저 매칭
1 WebSocket을 통해 실시간 연결

2 최소 2명이 매칭 버튼을 눌러야 연결 가능

### 3 1:1 랜덤 대화 
1 메시지 전송 시 Redis에 5분 TTL의 채팅방 생성 → 서버 부하 방지 

2 대화 종료 시 스케줄러 기능을 통해 30초마다 자동으로 조회 TTL이 1분 이하이면 스케줄러가 MongoDB로 백업 → Write-back 캐시 전략

3 과거 대화는 Redis 우선 조회, 없으면 MongoDB 복원

### 4. 단체 채팅방
1 방 생성 시 Redis에 룸키 자동 생성

2 채팅 발생 시 TTL 5분으로 Redis 유지

3 스케줄러 기능을 통해 30초마다 자동으로 조회 TTL 1분 이하가 되면 MongoDB로 자동 백업 → Write-back 캐시 전략

4 과거 대화는 Redis → MongoDB 순서로 복원

### 5. SNS 게시판
1 인기글은 Redis에만 캐싱 → 트래픽 분산

2 조회수는 IP 기반 중복 제한 + TTL 24시간 적용

3 조회수 + 하트 + 댓글 합산이 기준 이상일 경우 핫 게시판에 노출

4 핫 게시글은 모든 유저에게 게시판 상단 고정

### 6. 게시물 검색
1 닉네임, 게시글, 댓글 기준으로 통합 검색

2 Criteria 기반 + 트리 구조 적용 → O(log n²) 수준 성능 추정

### 7. 마이페이지
1 MongoDB를 통해 회원정보 조회

2 유저의 게시글 수, 하트 수, 댓글 수 등 통계 데이터 반환


## 트러블슈팅

### Redis TTL 기반 채팅 백업 로직 실패 

#### 문제 상황

처음에는 1:1 채팅이나 단체 채팅에서 Redis에 저장된 채팅룸 키에 TTL을 설정하고
TTL이 30초 이하로 남았을 때 DB에 채팅 기록을 저장하고 키가 자동 삭제되도록 처리함

하지만 실제 운영 중 확인해보니
TTL이 줄어드는 것은 확인되었으나 DB 저장 로직이 동작하지 않음
로그 확인 및 로직 점검을 진행했지만 명확한 원인을 찾지 못했음

#### 해결 방법

구조 자체를 변경하기로 결정하고
스케줄러를 도입하여 30초마다 Redis 키들을 직접 조회하고
TTL이 1분 미만으로 남은 채팅방에 대해 강제적으로 MongoDB에 저장되도록 수정함

## 회고록

jwt 인증 방식을 한번 도입해 봤는데 초기 설정 방식이 복잡해 어려웠다. 그리고 aws 클라우드에서 인스턴스 생성하고 직접 배포를 해봤는데 이게 조금 복잡한게 아니라 직접 하나하나 콘솔창에서 명령어로 라이브러리 설치, 컴파일, 실행까지 전부 명령어로 수작업으로 처리하다 보니 vscode나 이클립스 같은 IDE프로그램의 편리성을 뼈저리게 느꼈다.
