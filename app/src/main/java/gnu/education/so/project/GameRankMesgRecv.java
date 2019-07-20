package gnu.education.so.project;

import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GameRankMesgRecv extends Thread{
    private GameRank gameRank;
    private Socket sock;
    private BufferedReader br;
    private String [] parsedStr;

    public GameRankMesgRecv(GameRank rk){
        gameRank = rk;
        sock = rk.getSocket();
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

            if(parsedStr[0].compareTo("S2P_EXIT_GAME") == 0){
                Message sendmsg = gameRank.getHandler().obtainMessage();/////////////////////////////////////////////////
                sendmsg.what = GameRank.S2P_EXIT_GAME;                                                     //상수는 class 이름으로 일반적으로 한다
                gameRank.getHandler().sendMessage(sendmsg);
                break;
            }
        }
    }


}
