package com.example.so.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.os.CountDownTimer;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class GamePlay extends AppCompatActivity {

    private TextView[] idTextView = new TextView[4];
    private EditText[][] hintTextViews = new EditText[3][];
    private TextView[] chatTextView = new TextView[4];
    private TextView[] scoreView = new TextView[4];

    private Button enterButton;
    private EditText chatText;
    private TextView roundView;

    private TextView hintTimeView;

    private String myID;
    private int myNumber;
    private int stage;

    private Socket sock;
    private String answer;

    private MessageHandler mesgHandler = null;
    private GamePlayMesgRecv recvThread = null;

    private long mTimeLeftInMillis = 20000;



    private CountDownTimer mCountDownTimer;
    private boolean isHintDialogOpen = false;

    private void initGamePlay () {
        // 초기화하는 함수

        // 이전 GrameReady로부터 Intent로 여러 데이터를 넘겨 받는다.
        Intent intent = getIntent();
        myID = intent.getExtras().getString("myID");
        myNumber = intent.getIntExtra("myNum", -1);

        idTextView[0].setText( intent.getExtras().getString("player0") );
        idTextView[1].setText( intent.getExtras().getString("player1") );
        //idTextView[2].setText( intent.getExtras().getString("player2") );
        //idTextView[3].setText( intent.getExtras().getString("player3") );

        hintTextViews[0] = new  EditText[3];
        hintTextViews[0][0] = (EditText)findViewById(R.id.Hint_00);
        hintTextViews[0][1] = (EditText)findViewById(R.id.Hint_01);
        hintTextViews[0][2] = (EditText)findViewById(R.id.Hint_02);

        enterButton = (Button)findViewById(R.id.btnEnter);

        hintTimeView = (TextView) findViewById(R.id.hintTimer);

        // Socket을 전역변수로부터 얻느다.
        sock = SocketSingleton.getSocket();

        // Message Handler 생성
        mesgHandler = new MessageHandler();

        // 서버로부터 메시지 받는 쓰레드 생성
        recvThread = new GamePlayMesgRecv(this);
        recvThread.start();

        sendMesg("P2S_READY_PLAY", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        idTextView[0] = (TextView)findViewById(R.id.id0);
        idTextView[1] = (TextView)findViewById(R.id.id1);
        idTextView[2] = (TextView)findViewById(R.id.id2);
        idTextView[3] = (TextView)findViewById(R.id.id3);

        chatTextView[0] = (TextView)findViewById(R.id.chatTextView0);
        chatTextView[1] = (TextView)findViewById(R.id.chatTextView1);
        chatTextView[2] = (TextView)findViewById(R.id.chatTextView2);
        chatTextView[3] = (TextView)findViewById(R.id.chatTextView3);

        scoreView[0] = (TextView)findViewById(R.id.score0);
        scoreView[1] = (TextView)findViewById(R.id.score1);
        scoreView[2] = (TextView)findViewById(R.id.score2);
        scoreView[3] = (TextView)findViewById(R.id.score3);

        enterButton = (Button) findViewById(R.id.btnEnter);
        chatText = (EditText) findViewById(R.id.chatText);
        roundView = (TextView) findViewById(R.id.RoundView);
        // 초기화 하는 함수 호출
        initGamePlay();

    }

    private void sendMesg(String type, String data){
        GameMesgSender sendThread = new GameMesgSender(sock, type,data);
        sendThread.start();
    }

    private void sendGuessAnswer(){
        String answer = chatText.getText().toString();
        String data = myNumber + " " + answer;
        sendMesg("P2S_SEND_GUESS_ANSWER", data);
    }

    private void showAnswer () {
        // 정답을 보여주고 3초간 시간 관리

        stage = 0;  // 0 단계 스테이지


        //isHintDialogOpen = true;
        showImg();
        // TODO: 3초 시간 관리 구현


        //while (isHintDialogOpen) ;
        //startTimer();

        // TODO: 힌트 View 활성화

    }
    AlertDialog ad;

    private void showImg(){
        Random random = new Random();
        int answer = random.nextInt(2);
        if(answer==0){
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastlayout));
            AlertDialog.Builder aDialog = new AlertDialog.Builder(GamePlay.this);

            aDialog.setTitle("정답");
            aDialog.setView(layout);
            aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad = aDialog.create();
            ad.show();
        }
        if(answer==1){
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastlayout));
            AlertDialog.Builder aDialog = new AlertDialog.Builder(GamePlay.this);

            aDialog.setTitle("정답");
            aDialog.setView(layout);
            aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad = aDialog.create();
            ad.show();
        }
        if(answer==2){
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastlayout));
            AlertDialog.Builder aDialog = new AlertDialog.Builder(GamePlay.this);

            aDialog.setTitle("정답");
            aDialog.setView(layout);
            aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            ad = aDialog.create();
            ad.show();
        }


        //일단 기린
        // TODO: 받은 정답에 따라 해당하는 이미지를 팝업창으로 띄워준다.

        mTimeLeftInMillis = 5000;
        mCountDownTimer = new HintCountDownTimer(this, ad, mTimeLeftInMillis, 1000);
        mCountDownTimer.start();

    }

    private void showHintList(int stage, String []hintStrs){

        hintTextViews[stage][0].setText(hintStrs[0]);
        hintTextViews[stage][1].setText(hintStrs[1]);
        hintTextViews[stage][2].setText(hintStrs[2]);
    }

    private void showGuessAnswer(int number, String guessAnswer){
        chatTextView[number].setText(guessAnswer);
    }

    private void showScore(int number, int score){
        scoreView[number].setText(score+"");
    }

    private void setRound(int roundNum){
        roundView.setText("ROUND "+roundNum);
    }

    public static final int S2P_RECV_ANSWER = 200;
    public static final int S2P_RECV_HINT_READY = 201;
    public static final int S2P_RECV_HINT_LIST = 202;
    public static final int S2P_RECV_GUESS_ANSWER = 203;
    public static final int S2P_CORRECT_ANSWER = 204;
    public static final int S2P_NEW_ROUND = 205;
    public static final int HINT_TIME_OVER = 206;


    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case S2P_RECV_ANSWER:
                    answer = (String)msg.obj;
                    showAnswer ();
                    break;
                case S2P_RECV_HINT_READY:
                    Toast.makeText(getApplicationContext(),"hint ready",Toast.LENGTH_LONG).show();
                    break;
                case S2P_RECV_HINT_LIST:
                    int stage = msg.arg1;
                    String hintList = (String)msg.obj;
                    String[] hintStrs = hintList.split(" ");
                    showHintList(stage, hintStrs);
                    break;
                case S2P_RECV_GUESS_ANSWER:
                    int number = msg.arg1;
                    String guessAnswer = (String)msg.obj;
                    showGuessAnswer(number,guessAnswer);
                    break;
                case S2P_CORRECT_ANSWER:
                    int pnumber = msg.arg1;
                    int score = msg.arg2;
                    showScore(pnumber,score);
                    break;
                case S2P_NEW_ROUND:
                    int roundNum = msg.arg1;
                    setRound(roundNum);
                    break;
                case HINT_TIME_OVER:
                    startTimer();
                    break;
            }
        }
    }

    public MessageHandler getHandler(){
        return mesgHandler;
    }

/*
    //'팝업창'클릭시 팝업 구현
    OnClickListener mClickListener = new OnClickListener(){
        AlertDialog ad;
        @Override
        public void onClick(View v) {
                    Context mContext = getApplicationContext();
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

                    View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastlayout));
                    AlertDialog.Builder aDialog = new AlertDialog.Builder(GamePlay.this);

                    aDialog.setTitle("정답");
                    aDialog.setView(layout);
                    aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                   ad = aDialog.create();
                   ad.show();


            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    TimerTask task = new TimerTask(){
                        @Override
                        public void run(){
                            ad.dismiss();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task,3000);
                }
            });
            thread.start();
        }
    };
*/

    public void onHintSendClicked(View v)  {
        // 일단 Hint 보내는 것을 버튼 클릭으로 구현
        String hintStr = null;
        hintStr = hintTextViews[stage][0].getText().toString();
        hintStr += " " + hintTextViews[stage][1].getText().toString();
        hintStr += " " + hintTextViews[stage][2].getText().toString();

        sendMesg("P2S_SEND_HINT_LIST", stage + " " + hintStr);

    }


    private void showCountDownText () {
        hintTimeView.setText(""+ mTimeLeftInMillis / 1000);
    }

    private void hintTimeOut () {
        String hintStr = null;
        hintStr = hintTextViews[stage][0].getText().toString();
        hintStr += " " + hintTextViews[stage][1].getText().toString();
        hintStr += " " + hintTextViews[stage][2].getText().toString();

        sendMesg("P2S_SEND_HINT_LIST", stage + " " + hintStr);

    }


    private void startTimer() {
        mTimeLeftInMillis = 20000;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                showCountDownText();
            }

            @Override
            public void onFinish() {
                hintTimeOut ();
                //mTimerRunning = false;
                //타이머리셋
                //mTimeLeftInMillis = START_TIME_IN_MILLIS;
                //CountDownText();
            }
        }.start();
        //mTimerRunning = true;
    }


    public void onEnterButtonClicked(View v){
        sendGuessAnswer();
    }


    ////////채팅////////
//    public void enterclicked(View v) throws InterruptedException {
//        final TextView chat1 = (TextView) findViewById(R.id.chat1);
//        EditText chattext = (EditText) findViewById(R.id.chattext);
//        chat1.setVisibility(View.VISIBLE);
//        chat1.setText(chattext.getText());
//        Thread thread = new Thread(new Runnable(){
//            @Override
//            public void run(){
//                TimerTask task = new TimerTask(){
//                    @Override
//                    public void run(){
//                        chat1.setVisibility(View.INVISIBLE);
//                    }
//                };
//                Timer timer = new Timer();
//                timer.schedule(task,3000);
//            }
//        });
//        thread.start();
//    }
}
