package com.example.so.project;

import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadyRoomMesgRecv extends Thread{
    private ReadyRoom readyRoom;
    private Socket sock;
    private BufferedReader br;
    private String [] parsedStr;

    public ReadyRoomMesgRecv(ReadyRoom rr){
        readyRoom = rr;
        sock = rr.getSocket();
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

            parsedStr = msg.split(" ");


            if(parsedStr[0].compareTo("S2P_SEND_ROOM_LIST") == 0){
                Message sendmsg = readyRoom.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoom.S2P_SEND_ROOM_LIST;                                                     //상수는 class 이름으로 일반적으로 한다
                sendmsg.obj = msg;
                readyRoom.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_ENTER_ROOM_OK") == 0){
                Message sendmsg = readyRoom.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoom.S2P_ENTER_ROOM_OK;                                                     //상수는 class 이름으로 일반적으로 한다
                readyRoom.getHandler().sendMessage(sendmsg);
                break;
            }

/*
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
                gameReady.getHandler().sendMessage(sendmsg);
                break;
            }

*/




        }
    }


}
