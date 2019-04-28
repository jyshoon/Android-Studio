package com.example.so.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import android.os.CountDownTimer;
//////허건녕
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
    private TextView answerTimeView;

    private String myID;
    private int myNumber;
    private int stage;
    private boolean isHostPlayer = false;

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
        //힌트추가생성
        hintTextViews[1] = new  EditText[3];
        hintTextViews[1][0] = (EditText)findViewById(R.id.Hint_03);
        hintTextViews[1][1] = (EditText)findViewById(R.id.Hint_04);
        hintTextViews[1][2] = (EditText)findViewById(R.id.Hint_05);
        //엔터키 이벤트
        hintTextViews[0][0].setOnKeyListener(new hintOnKeyListener());
        hintTextViews[0][1].setOnKeyListener(new hintOnKeyListener());
        hintTextViews[0][2].setOnKeyListener(new hintOnKeyListener());
        hintTextViews[1][0].setOnKeyListener(new hintOnKeyListener());
        hintTextViews[1][1].setOnKeyListener(new hintOnKeyListener());
        hintTextViews[1][2].setOnKeyListener(new hintOnKeyListener());

        enterButton = (Button)findViewById(R.id.btnEnter);

        hintTimeView = (TextView) findViewById(R.id.hintTimer);
        answerTimeView = (TextView) findViewById(R.id.hintTimer);

        // Socket을 전역변수로부터 얻느다.
        sock = SocketSingleton.getSocket();

        // Message Handler 생성
        mesgHandler = new MessageHandler();

        // 서버로부터 메시지 받는 쓰레드 생성
        recvThread = new GamePlayMesgRecv(this);
        recvThread.start();

        sendMesg("P2S_READY_PLAY");
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

    private void sendGuessAnswer(){
        String[] args = new String[2];
        args[0] = myNumber + "";
        args[1] = chatText.getText().toString();
        sendMesg("P2S_SEND_GUESS_ANSWER", args);
        chatText.setText("");
    }

    private void showAnswer () {

        // 정답을 보여주고 3초간 시간 관리

        stage = 0;  // 0 단계 스테이지


        //isHintDialogOpen = true;
        showImg();

        //while (isHintDialogOpen) ;
        //startTimer();

    }
    AlertDialog ad;

    private void showImg() {

        Random r = new Random();
        int answer = r.nextInt(14);
        if (answer == 0) {
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
        if (answer == 1) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast1, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 2) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast2, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 3) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast3, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 4) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast4, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 5) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast5, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 6) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast6, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 7) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast7, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 8) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast8, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 9) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast9, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 10) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast10, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 11) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast11, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 12) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast12, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 13) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast13, (ViewGroup) findViewById(R.id.toastlayout));
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
        if (answer == 14) {
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.toast14, (ViewGroup) findViewById(R.id.toastlayout));
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

        //hintView 초기화
        // @TODO : 현재는 맞췄을 경우 인데 틀렸을 경우에도 아래의 코드 넣어줘야함 // 태훈
        hintTextViews[0][0].setText("");
        hintTextViews[0][1].setText("");
        hintTextViews[0][2].setText("");
        hintTextViews[1][0].setText("");
        hintTextViews[1][1].setText("");
        hintTextViews[1][2].setText("");
    }

    private void setRound(int roundNum){
        roundView.setText("ROUND "+roundNum);
    }

    private void endGame (HashMap<String, String> playerScoreMap)
    {
        String scores = "";
        for (String key : playerScoreMap.keySet()) {
            Toast.makeText(this, key + " : " + playerScoreMap.get(key), Toast.LENGTH_LONG).show();
        }

    }


    public static final int S2P_RECV_ANSWER = 200;
    public static final int S2P_RECV_HINT_READY = 201;
    public static final int S2P_RECV_HINT_LIST = 202;
    public static final int S2P_RECV_GUESS_ANSWER = 203;
    public static final int S2P_CORRECT_ANSWER = 204;
    public static final int S2P_NEW_ROUND = 205;
    public static final int HINT_TIME_OVER = 206;
    public static final int S2P_END_GAME = 207;
    public static final int S2P_RECV_HINT_LIST_END = 208;
    public static  final int S2P_WRONG_ANSWER = 209;
    public static final int S2P_NEW_STAGE = 210;


    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            String[] hintList;
            int stage;

            switch(msg.what){
                case S2P_RECV_ANSWER:
                    answer = (String)msg.obj;
                    isHostPlayer = true;
                    showAnswer ();
                    break;
                case S2P_RECV_HINT_READY:
                    Toast.makeText(getApplicationContext(),"hint ready",Toast.LENGTH_LONG).show();
                    HintstartTimer(25);
                    break;
                case S2P_RECV_HINT_LIST_END:
                    stage = msg.arg1;
                    hintList = (String[])msg.obj;
                    //String[] hintStrs = hintList.split(" ");
                    showHintList(stage, hintList);
                    //문제푸는타이머적용
                    startGuessAnswer();
                    break;
                case S2P_RECV_HINT_LIST:
                    stage = msg.arg1;
                    hintList = (String[])msg.obj;
                    //String[] hintStrs = hintList.split(" ");
                    showHintList(stage, hintList);
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
                    if (isHostPlayer)
                        isHostPlayer = false;

                    mCountDownTimer.cancel();

                    break;
                case S2P_NEW_ROUND:
                    int roundNum = msg.arg1;
                    setRound(roundNum);
                    chatText.setFocusableInTouchMode(true);
                    chatText.setFocusable(true);
                    break;
                case HINT_TIME_OVER:
                    HintstartTimer(20);
                    break;
                case S2P_END_GAME:
                    HashMap<String, String> playerScoreMap = (HashMap<String, String>)msg.obj;
                    endGame (playerScoreMap);
                    break;
                case S2P_WRONG_ANSWER:
                    Toast.makeText(GamePlay.this, "WRONG ANSWER", Toast.LENGTH_SHORT).show();
                    chatText.setFocusable(false);
                    break;
                case S2P_NEW_STAGE:
                    startNewStage(msg.arg1);
                    break;
            }
        }
    }

    public MessageHandler getHandler(){
        return mesgHandler;
    }

    private void showCountDownText () {
        hintTimeView.setText(""+ mTimeLeftInMillis / 1000);
    }

    private void hintTimeOut () {
        String[] args = new String[4];
        args[0] = stage + "";
        args[1] = hintTextViews[stage][0].getText().toString();
        if (args[1].compareTo("") == 0)
            args[1] = " ";
        args[2] = hintTextViews[stage][1].getText().toString();
        if (args[2].compareTo("") == 0)
            args[2] = " ";
        args[3] = hintTextViews[stage][2].getText().toString();
        if (args[3].compareTo("") == 0)
            args[3] = " ";

        sendMesg("P2S_SEND_HINT_LIST_END", args);

    }

    class hintOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey (View view, int KeyCode, KeyEvent event){
            if(KeyCode == event.KEYCODE_ENTER){
                //엔터키를 누르고 실행시키고자 하는 사항
                String[] args = new String[4];
                args[0] = stage + "";
                args[1] = hintTextViews[stage][0].getText().toString();
                if (args[1].compareTo("") == 0)
                    args[1] = " ";
                args[2] = hintTextViews[stage][1].getText().toString();
                if (args[2].compareTo("") == 0)
                    args[2] = " ";
                args[3] = hintTextViews[stage][2].getText().toString();
                if (args[3].compareTo("") == 0)
                    args[3] = " ";

                Log.d("----> KHKim <---", view.getId() + "");
                sendMesg("P2S_SEND_HINT_LIST", args);
            }
            return false;
        }
    }


    private void HintstartTimer(long timeLeftSec) {
        if (isHostPlayer) {
            mTimeLeftInMillis = timeLeftSec * 1000;
            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    showCountDownText();
                }

                @Override
                public void onFinish() {
                    hintTimeOut();
                }
            }.start();
        }
        else {
            mTimeLeftInMillis = timeLeftSec * 1000;
            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    showCountDownText();
                }

                @Override
                public void onFinish() {
                    //hintTimeOut();
                }
            }.start();
        }
        //mTimerRunning = true;
    }


   /* private void AnswerstartTimer() {
        mTimeLeftInMillis = 40000;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                answerTimeView.setText(""+ mTimeLeftInMillis / 1000);
            }

            @Override
            public void onFinish() {
                //주어진 시간내에 문제를 풀지 못 했을 경우
            }
        }.start();
    }*/

    private void startGuessAnswer(){
        mTimeLeftInMillis = 40000;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                answerTimeView.setText(""+ mTimeLeftInMillis / 1000);
            }

            @Override
            public void onFinish() {
                //주어진 시간내에 문제를 풀지 못 했을 경우
                if(isHostPlayer == true){
                    sendMesg("P2S_ANSWER_TIME_OVER");
                    if (stage == 1) {
                        isHostPlayer = false;
                    }
                }

            }



        }.start();
    }


    public void startNewStage(int newStage){
        stage = newStage;
        if(isHostPlayer == true){
            HintstartTimer(20);
        }
        else{
            HintstartTimer(20);
            chatText.setFocusableInTouchMode(true);
            chatText.setFocusable(true);
            Toast.makeText(GamePlay.this, "Waitinf for Hint", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEnterButtonClicked(View v){
        sendGuessAnswer();
    }




    private Timer timer;
    private ChatClearCountDownTimer chatClearCountDownTimer;

    public void setMessage(final int number, String mesg){

        chatTextView[number].setText(mesg);

        chatClearCountDownTimer = new ChatClearCountDownTimer(chatTextView[number], 2000, 1000);
        chatClearCountDownTimer.start();

    }
    static class ChatClearCountDownTimer extends CountDownTimer {
        private TextView chatView;
        private long mTimeLeftInMillis = 2000;

        public ChatClearCountDownTimer (TextView _chatView, long millsLeft, int interval) {
            super(millsLeft, interval);
            chatView = _chatView;
        }

        public void onTick(long millisUntilFinished) {
            mTimeLeftInMillis = millisUntilFinished;
        }

        @Override
        public void onFinish() {
            chatView.setText("");
        }
    }
}
