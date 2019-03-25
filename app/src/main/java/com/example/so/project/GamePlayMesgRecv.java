package com.example.so.project;

import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GamePlayMesgRecv extends Thread{
    private GamePlay gamePlay;
    private Socket sock;
    private BufferedReader br;
    private String [] parsedStr;

    public GamePlayMesgRecv(GamePlay gp){
        gamePlay = gp;
        sock = SocketSingleton.getSocket();
        try {
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        while(true){
            //메시지 받기
            String msg = null;
            try {
                msg = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            parsedStr = msg.split(" ");

            if(parsedStr[0].compareTo("S2P_RECV_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_ANSWER;
                sendmsg.obj = parsedStr[1];

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_RECV_HINT_READY") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_HINT_READY;

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_RECV_HINT_LIST") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_HINT_LIST;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                String hintList = parsedStr[2] + " " + parsedStr[3] +
                            " " + parsedStr[4];
                sendmsg.obj = hintList;
                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_RECV_GUESS_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_GUESS_ANSWER;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.obj = parsedStr[2];

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_CORRECT_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_CORRECT_ANSWER;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.arg2 = Integer.parseInt(parsedStr[2]);

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_NEW_ROUND") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_NEW_ROUND;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);

                gamePlay.getHandler().sendMessage(sendmsg);
            }









            /*
            if(parsedStr[0].compareTo("S2P_CLIENT_NUMBER") == 0){
                Message sendmsg = gameReady.getHandler().obtainMessage();
                sendmsg.what = GameReady.S2P_CLIENT_NUMBER;                                                     //상수는 class 이름으로 일반적으로 한다
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.obj = parsedStr[2];

                gameReady.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_START_GAME")==0){
                Message sendmsg = gameReady.getHandler().obtainMessage();
                sendmsg.what = GameReady.S2P_START_GAME;
                gameReady.getHandler().sendMessage(sendmsg);
                break;
            }
            */
        }
    }


}
