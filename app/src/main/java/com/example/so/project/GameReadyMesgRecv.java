package com.example.so.project;

import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class GameReadyMesgRecv extends Thread{
    private GameReady gameReady;
    private Socket sock;
    private BufferedReader br;
    private String [] parsedStr;

    public GameReadyMesgRecv(GameReady gr){
        gameReady = gr;
        sock = gr.getSocket();
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
                msg = br.readLine();                /////////////////////////////////////////
            } catch (IOException e) {
                e.printStackTrace();
            }

            parsedStr = msg.split("####");

            if(parsedStr[0].compareTo("S2P_CLIENT_NUMBER") == 0){
                Message sendmsg = gameReady.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = GameReady.S2P_CLIENT_NUMBER;                                                     //상수는 class 이름으로 일반적으로 한다
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.arg2 = Integer.parseInt(parsedStr[3]);
                sendmsg.obj = parsedStr[2];

                gameReady.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_SEND_GAME_READY_CHAT")==0){
                Message sendmsg = gameReady.getHandler().obtainMessage();
                sendmsg.what = GameReady.S2P_SEND_GAME_READY_CHAT;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                sendmsg.obj = parsedStr[2];

                gameReady.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_START_GAME")==0){
                Message sendmsg = gameReady.getHandler().obtainMessage();
                sendmsg.what = GameReady.S2P_START_GAME;
                sendmsg.arg1 = Integer.parseInt(parsedStr[1]);
                gameReady.getHandler().sendMessage(sendmsg);
                break;
            }

        }
    }


}
