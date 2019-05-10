package com.example.so.project;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.net.Socket;
import java.util.*;

import static android.widget.Toast.LENGTH_LONG;

public class GameRank extends AppCompatActivity {

    private int numPlayer;
    private ImageView[] characterView = new ImageView[4];
    private TextView[] idTextView = new TextView[4];
    private TextView[] scoreView = new TextView[4];
    private int[] scores = new int[4];
    private int[] ranking = new int[4];
    private String myID;
    private int myNumber;
    private int myImgResId;
    private TextView[] rank = new TextView[4];
    public static final int REQUEST_CODE_READYROOMAGIAN = 404;

    private GameRankMesgRecv recvThread;
    private MessageHandler mesgHandler;

    private Socket sock;

    private String addr = null;
    private int port;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rank);
        Toast.makeText(this,  " dddddddddddddddddddd1", LENGTH_LONG).show();
        mesgHandler = new MessageHandler();

        sock = SocketSingleton.getSocket();

        idTextView[0] = (TextView)findViewById(R.id.id0);
        idTextView[1] = (TextView)findViewById(R.id.id1);
        idTextView[2] = (TextView)findViewById(R.id.id2);
        idTextView[3] = (TextView)findViewById(R.id.id3);

        Button restartButton = (Button)findViewById(R.id.restartButton);

        scoreView[0] = (TextView)findViewById(R.id.score0);
        scoreView[1] = (TextView)findViewById(R.id.score1);
        scoreView[2] = (TextView)findViewById(R.id.score2);
        scoreView[3] = (TextView)findViewById(R.id.score3);

        rank[0] = (TextView)findViewById(R.id.rank0);
        rank[1] = (TextView)findViewById(R.id.rank1);
        rank[2] = (TextView)findViewById(R.id.rank2);
        rank[3] = (TextView)findViewById(R.id.rank3);

        recvThread = new GameRankMesgRecv(this);
        recvThread.start();

        initGameRank();


        restartButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


                sendMesg("P2S_EXIT_GAME", Integer.toString(myNumber));                              //스레드 종료 시키기 위한 메시지

            }
        });

    }
    private void goToReadyRoom(){
        Intent intent = new Intent(getApplicationContext(),ReadyRoomAgain.class);

        intent.putExtra("id",myID);
        intent.putExtra("ip", addr);
        intent.putExtra("port",Integer.toString(port));
        startActivity(intent);
        //startActivityForResult(intent,REQUEST_CODE_READYROOMAGIAN);
    }







    public MessageHandler getHandler(){
        return mesgHandler;
    }


    private void sendMesg (String type) {
        GameMesgSender sendThread = new GameMesgSender(sock, type);
        sendThread.start();
    }

    private void sendMesg(String type, String data){
        GameMesgSender sendThread = new GameMesgSender(sock, type,data);
        sendThread.start();
    }

    private void sendMesg (String type, String[] args) {
        GameMesgSender sendThread = new GameMesgSender (sock, type, args);
        sendThread.start ();
    }


    public static final int S2P_EXIT_GAME = 500;

    class MessageHandler extends Handler {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String[] hintList;
            int stage;

            switch (msg.what) {

                case S2P_EXIT_GAME:
                    goToReadyRoom();
                    break;
            }
        }
    }















    private void initGameRank() {
        Intent intent = getIntent();


        myImgResId = intent.getIntExtra("myImgId", 0);
        addr = intent.getExtras().getString("ip").trim();
        port = Integer.parseInt(intent.getExtras().getString("port").trim());
        myID = intent.getExtras().getString("myID");
        myNumber = intent.getIntExtra("myNum", -1);

        numPlayer = intent.getIntExtra("numPlayer", 4);

        idTextView[0].setText( intent.getExtras().getString("player0") );                               //플레이어 ID
        idTextView[1].setText( intent.getExtras().getString("player1") );
        idTextView[2].setText( intent.getExtras().getString("player2") );
        idTextView[3].setText( intent.getExtras().getString("player3") );

        characterView[0] = (ImageView) findViewById(R.id.user1);
        characterView[1] = (ImageView) findViewById(R.id.user2);
        characterView[2] = (ImageView) findViewById(R.id.user3);
        characterView[3] = (ImageView) findViewById(R.id.user4);
        characterView[0].setTag ( intent.getExtras().getInt("player0ResId"));                           //플레이어 캐릭터
        characterView[1].setTag ( intent.getExtras().getInt("player1ResId"));
        characterView[2].setTag ( intent.getExtras().getInt("player2ResId"));
        characterView[3].setTag ( intent.getExtras().getInt("player3ResId"));

        for (int i = 0; i < 4; i++) {
            if ((Integer)characterView[i].getTag() != 0) {
                characterView[i].setVisibility(View.VISIBLE);
                characterView[i].setImageResource( (Integer)characterView[i].getTag() );
            }
            else
                characterView[i].setVisibility(View.INVISIBLE);
        }
        for(int i=0; i<4;i++){
            scores[i] = 0;
        }
        for(int i = 0; i < numPlayer;i++){                                                                      //플레이어 점수들
            scoreView[i].setText( intent.getExtras().getString("score"+i));
        }
        for(int i = 0; i < numPlayer;i++){
            scores[i] = Integer.parseInt(scoreView[i].getText().toString());
        }

        for(int i = 0; i < numPlayer; i++){                                                                         //랭킹 초기화
            ranking[i] = 1;
        }

        for (int i = 0; i < numPlayer; i++){                                                                         //랭킹 정함
            for(int j = 0; j < numPlayer; j++){
                if( scores[i] < scores[j] ){
                    ranking[i] = ranking [i]+1;
                }
            }
        }

        for(int i = 0; i < numPlayer; i++){
            rank[i].setText(ranking[i]+"");                                                                           //랭킹 보여줌
        }
    }
    public Socket getSocket(){
        return sock;
    }

}

