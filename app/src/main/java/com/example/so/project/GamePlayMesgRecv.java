package com.example.so.project;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class GamePlayMesgRecv extends Thread{
    private GamePlay gamePlay;
    private GameRank gameRank;
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

            parsedStr = msg.split("####");

            Log.d("KHKim", msg);

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

            if(parsedStr[0].compareTo("S2P_RECV_HINT_LIST_END") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_HINT_LIST_END;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                String[] hintList = new String[3];
                hintList[0] = parsedStr[2];
                hintList[1] = parsedStr[3];
                hintList[2] = parsedStr[4];


                sendmsg.obj = hintList;
                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_RECV_HINT_LIST") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_HINT_LIST;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                String[] hintList = new String[3];
                hintList[0] = parsedStr[2];
                hintList[1] = parsedStr[3];
                hintList[2] = parsedStr[4];


                sendmsg.obj = hintList;
                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_RECV_GUESS_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_RECV_GUESS_ANSWER;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.obj = parsedStr[2];
                sendmsg.arg2 = Integer.parseInt(parsedStr[3]);

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_CORRECT_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_CORRECT_ANSWER;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);

                String[] scores = new String[gamePlay.getNumPlayer()*2];
                for (int i = 0; i < gamePlay.getNumPlayer(); i++) {

                    scores[i*2] = parsedStr[2*i + 2];
                    scores[i*2+1] = parsedStr[2*i+3];
                }
                sendmsg.obj = scores;

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_WRONG_ANSWER") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_WRONG_ANSWER;

                gamePlay.getHandler().sendMessage(sendmsg);

            }

            if(parsedStr[0].compareTo("S2P_NEW_ROUND") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_NEW_ROUND;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);

                gamePlay.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_END_GAME") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_END_GAME;

                HashMap<String, String> playerScoreMap = new HashMap<String, String>();
                for (int i = 1; i < parsedStr.length; i+=2)
                    playerScoreMap.put(parsedStr[i], parsedStr[i+1]);
                sendmsg.obj = playerScoreMap;
                gamePlay.getHandler().sendMessage(sendmsg);
                break;
            }

            if(parsedStr[0].compareTo("S2P_NEW_STAGE") == 0){
                Message sendmsg = gamePlay.getHandler().obtainMessage();
                sendmsg.what = GamePlay.S2P_NEW_STAGE;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);

                gamePlay.getHandler().sendMessage(sendmsg);
            }

        }
    }


}
