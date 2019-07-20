package gnu.education.so.project;

import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadyRoomMesgRecvAgain extends Thread{
    private ReadyRoomAgain readyRoomAgain;
    private Socket sock;
    private BufferedReader br;
    private String [] parsedStr;

    public ReadyRoomMesgRecvAgain(ReadyRoomAgain rr){
        readyRoomAgain = rr;
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

            parsedStr = msg.split("####");


            if(parsedStr[0].compareTo("S2P_SEND_ROOM_LIST") == 0){
                Message sendmsg = readyRoomAgain.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoomAgain.S2P_SEND_ROOM_LIST;                                                     //상수는 class 이름으로 일반적으로 한다
                sendmsg.obj = msg;
                readyRoomAgain.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_ENTER_ROOM_OK") == 0){
                Message sendmsg = readyRoomAgain.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoomAgain.S2P_ENTER_ROOM_OK;                                                     //상수는 class 이름으로 일반적으로 한다
                readyRoomAgain.getHandler().sendMessage(sendmsg);
                break;
            }

            if(parsedStr[0].compareTo("S2P_ENTER_ROOM_FAIL") == 0){
                Message sendmsg = readyRoomAgain.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoomAgain.S2P_ENTER_ROOM_FAIL;                                                     //상수는 class 이름으로 일반적으로 한다
                readyRoomAgain.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_CREATE_ROOM_FAIL") == 0){
                Message sendmsg = readyRoomAgain.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoomAgain.S2P_CREATE_ROOM_FAIL;                                                     //상수는 class 이름으로 일반적으로 한다
                readyRoomAgain.getHandler().sendMessage(sendmsg);
            }

            if(parsedStr[0].compareTo("S2P_CREATE_ROOM_OK") == 0){
                Message sendmsg = readyRoomAgain.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = ReadyRoomAgain.S2P_CREATE_ROOM_OK;                                                     //상수는 class 이름으로 일반적으로 한다
                readyRoomAgain.getHandler().sendMessage(sendmsg);
                break;
            }



        }
    }


}
