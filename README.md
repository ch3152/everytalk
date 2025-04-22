<h2> everytalk-Sns-service (WebSocket 기반 실시간 채팅 서비스) </h2>

모든 사용자가 익명으로 참여 가능한 실시간 웹 커뮤니케이션 서비스입니다.  
1:1 랜덤 대화, 단체 채팅방, SNS 게시판을 통해 감정이나 일상을 공유할 수 있도록 설계되었습니다.

- 실시간 메세지: STOMP + WebSocket 기반
- 인증: JWT (토큰 기반 인증 시스템)
- 저장소: Redis(캐싱, 채팅 로그 임시 저장), MongoDB(영구 데이터 저장)
- 배포: AWS EC2 기반 Linux 환경, Atlas MongoDB 연동
- url 주소 : http://13.124.207.177:3000/  (실시간 배포 운영중!!)

<h2>줄
