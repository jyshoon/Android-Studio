# WordGuessApp-Android

## 액티버티 종료 후 뒤로 가기 구현
1. xxxRecvThread 에서 해당 메시지를 받으면 break 실행하여 쓰레드 종료
2. xxxActivity 에서 finish () 로 Activity 종료
3. 생성한 메인 Activity에서 onRestart () 재구현
   * recvThread = new ReadyRoomMesgRecv(this);
   * recvThread.start();       //쓰레드 재실행.
