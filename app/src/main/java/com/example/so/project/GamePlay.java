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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import android.os.CountDownTimer;
//////김태훈 branches
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class GamePlay extends AppCompatActivity {

    private TextView[] idTextView = new TextView[4];
    private EditText[][] hintTextViews = new EditText[3][];
    private TextView[] chatTextView = new TextView[4];
    private TextView[] scoreView = new TextView[4];
    private ImageView[] characterView = new ImageView[4];

    private Button enterButton;
    private EditText chatText;
    private TextView roundView;

    private TextView hintTimeView;
    private TextView answerTimeView;

    private String myID;
    private int myNumber;
    private int myImgResId;
    private int numPlayer;
    private int stage;
    private boolean isHostPlayer = false;


    private Socket sock;
    private String answer;

    private MessageHandler mesgHandler = null;
    private GamePlayMesgRecv recvThread = null;

    private long mTimeLeftInMillis = 20000;



    private SolvingTimer mCountDownTimer = null;
    private CountDownTimer mHintTimer = null;

    private boolean isHintDialogOpen = false;

    public int getNumPlayer () {
        return numPlayer;
    }
    private void initGamePlay () {
        // 초기화하는 함수

        // 이전 GrameReady로부터 Intent로 여러 데이터를 넘겨 받는다.
        Intent intent = getIntent();
        myID = intent.getExtras().getString("myID");
        myNumber = intent.getIntExtra("myNum", -1);
        myImgResId = intent.getIntExtra("myImgId", 0);
        numPlayer = intent.getIntExtra("numPlayer", 4);

        idTextView[0].setText( intent.getExtras().getString("player0") );
        idTextView[1].setText( intent.getExtras().getString("player1") );
        idTextView[2].setText( intent.getExtras().getString("player2") );
        idTextView[3].setText( intent.getExtras().getString("player3") );


        characterView[0] = (ImageView) findViewById(R.id.user1);
        characterView[1] = (ImageView) findViewById(R.id.user2);
        characterView[2] = (ImageView) findViewById(R.id.user3);
        characterView[3] = (ImageView) findViewById(R.id.user4);
        characterView[0].setTag ( intent.getExtras().getInt("player0ResId"));
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
        //characterView[1].setImageResource ( intent.getExtras().getInt("player1ResId"));
        //characterView[2].setImageResource ( intent.getExtras().getInt("player2ResId"));
        //characterView[3].setImageResource ( intent.getExtras().getInt("player3ResId"));


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

        hintTextViews[0][0].setText("");
        hintTextViews[0][1].setText("");
        hintTextViews[0][2].setText("");
        hintTextViews[1][0].setText("");
        hintTextViews[1][1].setText("");
        hintTextViews[1][2].setText("");


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

    @Override
    public void onBackPressed () {
        Toast.makeText(this, "One more back for exit.", Toast.LENGTH_SHORT).show ();
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

        //hintView 두번째 라인 비활성화
        hintTextViews[0][0].setFocusable(true);
        hintTextViews[0][1].setFocusable(true);
        hintTextViews[0][2].setFocusable(true);
        hintTextViews[0][0].setFocusableInTouchMode(true);
        hintTextViews[0][1].setFocusableInTouchMode(true);
        hintTextViews[0][2].setFocusableInTouchMode(true);

    }
    AlertDialog ad;

    private void showImg() {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout;
        if(answer.compareTo("Giraffe") == 0){
            layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if(answer.compareTo("Hedgehog") == 0){
            layout = inflater.inflate(R.layout.toast1, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Leopard") == 0) {
            layout = inflater.inflate(R.layout.toast2, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Cat") == 0) {
            layout = inflater.inflate(R.layout.toast3, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Raccon") == 0) {
            layout = inflater.inflate(R.layout.toast4, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Lion") == 0) {
            layout = inflater.inflate(R.layout.toast5, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Leopard") == 0) {
            layout = inflater.inflate(R.layout.toast6, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Pigeon") == 0) {
            layout = inflater.inflate(R.layout.toast7, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Rabbit") == 0) {
            layout = inflater.inflate(R.layout.toast8, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Dog") == 0) {
            layout = inflater.inflate(R.layout.toast9, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Smartphone") == 0) {
            layout = inflater.inflate(R.layout.toast10, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Elephant") == 0) {
            layout = inflater.inflate(R.layout.toast11, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Butterfly") == 0) {
            layout = inflater.inflate(R.layout.toast12, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else if (answer.compareTo("Smartphone") == 0) {
            layout = inflater.inflate(R.layout.toast13, (ViewGroup) findViewById(R.id.toastlayout));
        }
        else {
            layout = inflater.inflate(R.layout.toast14, (ViewGroup) findViewById(R.id.toastlayout));
        }

        AlertDialog.Builder aDialog = new AlertDialog.Builder(GamePlay.this);
        aDialog.setTitle("정답");
        aDialog.setView(layout);
        aDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ad = aDialog.create();
        ad.show();

//

        //일단 기린
        // TODO: 받은 정답에 따라 해당하는 이미지를 팝업창으로 띄워준다.

        mTimeLeftInMillis = 5000;
        mHintTimer = new HintCountDownTimer(this, ad, mTimeLeftInMillis, 1000);
        mHintTimer.start();


    }

    private void showHintList(int stage, String []hintStrs){

        hintTextViews[stage][0].setText(hintStrs[0]);
        hintTextViews[stage][1].setText(hintStrs[1]);
        hintTextViews[stage][2].setText(hintStrs[2]);

    }


    //private void showScore(int number, int score){
    private void showScore(String[] scores){
        for (int i = 0; i < numPlayer; i++)
            scoreView[i].setText(scores[i]);

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

    private void clearHintViews () {
        hintTextViews[0][0].setText("");
        hintTextViews[0][1].setText("");
        hintTextViews[0][2].setText("");
        hintTextViews[1][0].setText("");
        hintTextViews[1][1].setText("");
        hintTextViews[1][2].setText("");

        hintTextViews[0][0].setFocusable(false);
        hintTextViews[0][1].setFocusable(false);
        hintTextViews[0][2].setFocusable(false);
        hintTextViews[1][0].setFocusable(false);
        hintTextViews[1][1].setFocusable(false);
        hintTextViews[1][2].setFocusable(false);
    }

    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            String[] hintList;
            int stage;

            switch(msg.what){
                case S2P_RECV_ANSWER:
                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    clearHintViews ();
                    hintTextViews[0][0].setFocusable(true);
                    hintTextViews[0][1].setFocusable(true);
                    hintTextViews[0][2].setFocusable(true);
                    hintTextViews[0][0].setFocusableInTouchMode(true);
                    hintTextViews[0][1].setFocusableInTouchMode(true);
                    hintTextViews[0][2].setFocusableInTouchMode(true);
                    answer = (String)msg.obj;
                    isHostPlayer = true;
                    chatText.setFocusable(false);
                    showAnswer ();
                    break;
                case S2P_RECV_HINT_READY:
                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    clearHintViews ();

                    chatText.setFocusable(true);
                    chatText.setFocusableInTouchMode(true);

                    Toast.makeText(getApplicationContext(),"hint ready",Toast.LENGTH_LONG).show();
                    Log.d("KHKim ", "HintstarTimer in S2P_RECV_HINT_READY");

                    isHostPlayer = false;
                    HintstartTimer(25);
                    break;
                case S2P_RECV_HINT_LIST_END:
                    stage = msg.arg1;
                    hintList = (String[])msg.obj;
                    //String[] hintStrs = hintList.split(" ");
                    showHintList(stage, hintList);
                    //문제푸는타이머적용
                    Log.d ("KHKim ", "S2P_RECV_HINT_LIST_END  --- ");
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

                    if (msg.arg2 == 0) {
                        Toast.makeText(GamePlay.this, "Player "+ number + " WRONG ANSWER", Toast.LENGTH_SHORT).show();
                        if (number == myNumber)
                            chatText.setFocusable(false);
                    }
                    else {
                        Toast.makeText(GamePlay.this, "Player "+ number + " Correct ANSWER", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case S2P_CORRECT_ANSWER:
                    /*
                    int pnumber = msg.arg1;
                    int score = msg.arg2;
                    */
                    int pnumber = msg.arg1;
                    String[] scores = (String[])msg.obj;
                    showScore(scores);
                    Toast.makeText(GamePlay.this,idTextView[pnumber].getText().toString()+"가 문제를 맞췄습니다!",Toast.LENGTH_SHORT).show();
                    if (isHostPlayer){
                        isHostPlayer = false;
                        chatText.setFocusable(true);
                        chatText.setFocusableInTouchMode(true);
                    }

                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }

                    break;
                case S2P_NEW_ROUND:
                    int roundNum = msg.arg1;
                    setRound(roundNum);
                    chatText.setFocusableInTouchMode(true);
                    chatText.setFocusable(true);
                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    break;
                case HINT_TIME_OVER:
                    Log.d("KHKim ", "HintstarTimer in HINT_TIME_OVER");

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
                    if (mCountDownTimer != null) {
                        Log.d("--> KHKim <--", "cancel mCountDownTimer");
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
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

        Log.d("KHKim ", "..... send HINT LIST END ....");
        sendMesg("P2S_SEND_HINT_LIST_END", args);

    }

    class hintOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey (View view, int KeyCode, KeyEvent event){
            if(KeyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
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

                Log.d ("-- KHKim --", "length = " + args.length + args[0] + "$" + args[1]
                                + "$" + args[2] + "$" + args[3] + "$");
                sendMesg("P2S_SEND_HINT_LIST", args);
            }
            return false;
        }
    }


    private void HintstartTimer(long timeLeftSec) {
        if (isHostPlayer) {
            mTimeLeftInMillis = timeLeftSec * 1000;
            mHintTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    showCountDownText();
                    Log.d ("KHKim ", "Hintstart Timer tick ()");

                }

                @Override
                public void onFinish() {
                    Log.d ("KHKim ", "Hintstart Timer stopped...");
                    hintTimeOut();
                }
            }.start();
        }
        else {
            mTimeLeftInMillis = timeLeftSec * 1000;
            mHintTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    showCountDownText();
                    Log.d ("KHKim ", "Hintstart Timer else tick ()");

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

    private class SolvingTimer extends CountDownTimer {
        private long millsecLeft = 40000;
        public SolvingTimer (long secLeft, long interval) {
            super(secLeft * 1000, interval);
            millsecLeft = secLeft * 1000;
            Log.d ("KHKim ", "Solving Timer is created....................");
        }

        public void onTick(long millisUntilFinished) {
            millsecLeft = millisUntilFinished;
            answerTimeView.setText(""+ millsecLeft / 1000);


            Log.d ("KHKim ", "Solving tick ()");
        }

        public void onFinish() {
            //주어진 시간내에 문제를 풀지 못 했을 경우
            if(isHostPlayer == true){
                //hintView 두번째 라인 활성화
                hintTextViews[1][0].setFocusable(true);
                hintTextViews[1][1].setFocusable(true);
                hintTextViews[1][2].setFocusable(true);
                hintTextViews[1][0].setFocusableInTouchMode(true);
                hintTextViews[1][1].setFocusableInTouchMode(true);
                hintTextViews[1][2].setFocusableInTouchMode(true);
                sendMesg("P2S_ANSWER_TIME_OVER");
                if (stage == 1) {
                    isHostPlayer = false;
                    chatText.setFocusable(true);
                    chatText.setFocusableInTouchMode(true);
                    // test
                }
            }
        }

        public void stop () {
            Log.d ("KHKim ", "Solving timer     stopped........");
            super.cancel();
        }
    }

    private void startGuessAnswer(){
        mCountDownTimer = new SolvingTimer(40, 1000);
        mCountDownTimer.start();

        /*
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
                    //hintView 두번째 라인 활성화
                    hintTextViews[1][0].setFocusable(true);
                    hintTextViews[1][1].setFocusable(true);
                    hintTextViews[1][2].setFocusable(true);
                    hintTextViews[1][0].setFocusableInTouchMode(true);
                    hintTextViews[1][1].setFocusableInTouchMode(true);
                    hintTextViews[1][2].setFocusableInTouchMode(true);
                    sendMesg("P2S_ANSWER_TIME_OVER");
                    if (stage == 1) {
                        isHostPlayer = false;
                        chatText.setFocusable(true);
                        chatText.setFocusableInTouchMode(true);
                        // test
                    }
                }


            }
        };

        mCountDownTimer.start();
        */
    }


    public void startNewStage(int newStage){
        stage = newStage;

        Log.d("KHKim ", "HintstarTimer in startNewStage");
        if(isHostPlayer == true){
            HintstartTimer(20);
            hintTextViews[stage][0].setFocusable(true);
            hintTextViews[stage][1].setFocusable(true);
            hintTextViews[stage][2].setFocusable(true);
            hintTextViews[stage][0].setFocusableInTouchMode(true);
            hintTextViews[stage][1].setFocusableInTouchMode(true);
            hintTextViews[stage][2].setFocusableInTouchMode(true);
        }
        else{
            HintstartTimer(20);

            chatText.setFocusable(true);
            chatText.setFocusableInTouchMode(true);
            Toast.makeText(GamePlay.this, "Waitinf for Hint", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEnterButtonClicked(View v){
        sendGuessAnswer();
    }




    private Timer timer;
    private ChatClearCountDownTimer chatClearCountDownTimer;

    public void showGuessAnswer(final int number, String mesg){

        chatTextView[number].setText(mesg);

        chatClearCountDownTimer = new ChatClearCountDownTimer(chatTextView[number], ChatClearCountDownTimer.CORRET_ANSWER, 5000, 1000);
        chatClearCountDownTimer.start();

    }
}
