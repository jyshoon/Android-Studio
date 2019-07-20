package gnu.education.so.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Timer;
import android.os.CountDownTimer;

import com.education.so.project.R;

public class GamePlay extends AppCompatActivity {

    private int flag = 0;                                       //0이면 첫번째 시도 wrong ans 1이면 두번째 시도 wrong ans

    private TextView[] idTextView = new TextView[4];
    private EditText[][] hintTextViews = new EditText[3][];
    private TextView[] chatTextView = new TextView[4];
    private TextView[] scoreView = new TextView[4];
    private ImageView[] characterView = new ImageView[4];

    private TextView roomNameView;

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

    private String addr = null;
    private int port;

    private Socket sock;
    private String answer;
    private int check;

    private MessageHandler mesgHandler = null;
    private GamePlayMesgRecv recvThread = null;

    private long mTimeLeftInMillis = 20000;



    private SolvingTimer mCountDownTimer = null;
    private CountDownTimer mHintTimer = null;

    private boolean isHintDialogOpen = false;

    public int getNumPlayer () {
        return numPlayer;
    }


    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // 초기화 하는 함수 호출
        initGamePlay();
    }

    private void initGamePlay () {
        // 초기화하는 함수

        // 이전 GrameReady로부터 Intent로 여러 데이터를 넘겨 받는다.
        Intent intent = getIntent();
        addr = intent.getExtras().getString("ip").trim();
        port = Integer.parseInt(intent.getExtras().getString("port").trim());
        myID = intent.getExtras().getString("myID");
        myNumber = intent.getIntExtra("myNum", -1);
        myImgResId = intent.getIntExtra("myImgId", 0);
        numPlayer = intent.getIntExtra("numPlayer", 4);

        String roomName = intent.getExtras().getString("roomName");

        roomNameView = (TextView)findViewById(R.id.roomname);
        roomNameView.setText(roomName);

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
                scoreView[i].setText("0");
            }
            else
                characterView[i].setVisibility(View.INVISIBLE);

        }

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

        enterButton = (Button) findViewById(R.id.btnEnter);
        chatText = (EditText) findViewById(R.id.chatText);
        roundView = (TextView) findViewById(R.id.RoundView);


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
        chatText.setFocusable(false);
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

        if (answer.compareTo("Giraffe") == 0) {
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
        //if (answer == 1) {
        else if (answer.compareTo("Hedgehog") == 0) {
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
        else if (answer.compareTo("Leopard") == 0) {

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
        else if (answer.compareTo("Cat") == 0) {

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
        else if (answer.compareTo("Raccon") == 0) {

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
        else if (answer.compareTo("Lion") == 0) {

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
        else if (answer.compareTo("Pigeon") == 0) {

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
        else if (answer.compareTo("Rabbit") == 0) {

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
        else if (answer.compareTo("Wolf") == 0) {

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
        else if (answer.compareTo("Dog") == 0) {

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
        else if (answer.compareTo("Smartphone") == 0) {

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
        else if (answer.compareTo("Elephant") == 0) {

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
        else if (answer.compareTo("Butterfly") == 0) {

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
        else if (answer.compareTo("Strawberry") == 0) {

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
        else if (answer.compareTo("Blueberry") == 0) {

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
        for (int i = 0; i < numPlayer; i++) {

            int index = Integer.parseInt(scores[2*i]);

            scoreView[index].setText(scores[2*i+1]);

        }
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

        Intent intent = new Intent(getApplicationContext(),GameRank.class);


        intent.putExtra("ip", addr);
        intent.putExtra("port",Integer.toString(port));
        intent.putExtra("myID",myID);
        intent.putExtra("myNum", myNumber);
        intent.putExtra ("myImgResId", myImgResId);
        intent.putExtra ("numPlayer", numPlayer);

        intent.putExtra("score0",scoreView[0].getText().toString());
        intent.putExtra("score1",scoreView[1].getText().toString());
        intent.putExtra("score2",scoreView[2].getText().toString());
        intent.putExtra("score3",scoreView[3].getText().toString());

        intent.putExtra("player0",idTextView[0].getText().toString());
        intent.putExtra("player1",idTextView[1].getText().toString());
        intent.putExtra("player2",idTextView[2].getText().toString());
        intent.putExtra("player3",idTextView[3].getText().toString());
        intent.putExtra("player0ResId", (Integer)characterView[0].getTag());
        intent.putExtra("player1ResId", (Integer)characterView[1].getTag());
        intent.putExtra("player2ResId", (Integer)characterView[2].getTag());
        intent.putExtra("player3ResId", (Integer)characterView[3].getTag());

        String scores = "";


        startActivityForResult(intent,REQUEST_CODE_GAMERANK);

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
    public static final int S2P_WRONG_ANSWER = 209;
    public static final int S2P_NEW_STAGE = 210;
    public static final int REQUEST_CODE_GAMERANK = 402;

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

            switch(msg.what) {
                case S2P_RECV_ANSWER:
                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    clearHintViews();
                    hintTextViews[0][0].setFocusable(true);
                    hintTextViews[0][1].setFocusable(true);
                    hintTextViews[0][2].setFocusable(true);
                    hintTextViews[0][0].setFocusableInTouchMode(true);
                    hintTextViews[0][1].setFocusableInTouchMode(true);
                    hintTextViews[0][2].setFocusableInTouchMode(true);
                    answer = (String) msg.obj;
                    isHostPlayer = true;
                    chatText.setFocusable(false);
                    showAnswer();
                    break;
                case S2P_RECV_HINT_READY:

                    LayoutInflater inflater5 = getLayoutInflater();
                    View layout5 = inflater5.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                    TextView text5 = (TextView) layout5.findViewById(R.id.text);
                    text5.setText("출제자가 20초간 문제 작성중! 기다려주세요!");
                    Toast toast5 = new Toast(getApplicationContext());
                    toast5.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast5.setDuration(Toast.LENGTH_LONG);
                    toast5.setView(layout5);
                    toast5.show();

                    chatText.setFocusable(false);                                                       //20초 간 !host 채팅창 블록

                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    clearHintViews();

                    isHostPlayer = false;
                    HintstartTimer(25);
                    break;
                case S2P_RECV_HINT_LIST_END:
                    stage = msg.arg1;
                    hintList = (String[]) msg.obj;
                    showHintList(stage, hintList);
                    //문제푸는타이머적용
                    startGuessAnswer();

                    break;
                case S2P_RECV_HINT_LIST:
                    stage = msg.arg1;
                    hintList = (String[]) msg.obj;
                    showHintList(stage, hintList);
                    break;
                case S2P_RECV_GUESS_ANSWER:
                    int number = msg.arg1;
                    String guessAnswer = (String) msg.obj;
                    showGuessAnswer(number, guessAnswer, check);

                    if (msg.arg2 == 0) {                                                //오답이라면
                        chatText.setFocusable(false);

                        if(flag == 0) {                                                 //첫번째 시도 오답
                            LayoutInflater inflater3 = getLayoutInflater();
                            View layout3 = inflater3.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                            TextView text3 = (TextView) layout3.findViewById(R.id.text);
                            text3.setText("Player " + number + " 오답!!!!!  20초간 추가 힌트 제공합니다!");
                            Toast toast3 = new Toast(getApplicationContext());
                            toast3.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast3.setDuration(Toast.LENGTH_LONG);
                            toast3.setView(layout3);
                            toast3.show();
                        }

                        flag = 1;
                        if (number == myNumber)
                            chatText.setFocusable(false);

                    } else {

                    }
                    break;
                case S2P_CORRECT_ANSWER:
                    check = 1;
                    int pnumber = msg.arg1;
                    String[] scores = (String[]) msg.obj;
                    showScore(scores);


                    LayoutInflater inflater4 = getLayoutInflater();
                    View layout4 = inflater4.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                    TextView text4 = (TextView) layout4.findViewById(R.id.text);
                    text4.setText("Player " + idTextView[pnumber].getText().toString() + " 정답! 10점 획득  다음문제로 넘어갑니다");
                    Toast toast4 = new Toast(getApplicationContext());
                    toast4.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast4.setDuration(Toast.LENGTH_LONG);
                    toast4.setView(layout4);
                    toast4.show();

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
                    //chatText.setFocusableInTouchMode(true);
                    //chatText.setFocusable(true);
                    if (mCountDownTimer != null) {
                        mCountDownTimer.stop();
                        mCountDownTimer = null;
                    }
                    break;
                case HINT_TIME_OVER:
                    HintstartTimer(20);
                    break;
                case S2P_END_GAME:
                    HashMap<String, String> playerScoreMap = (HashMap<String, String>)msg.obj;
                    endGame (playerScoreMap);
                    break;
                case S2P_WRONG_ANSWER:                                                                              //아예 맞추지 못했을 경우
                    check = 0;
                    LayoutInflater inflater1 = getLayoutInflater();
                    View layout1 = inflater1.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                    TextView text1 = (TextView) layout1.findViewById(R.id.text);
                    text1.setText("아무도 문제를 맞추지 못하였습니다. 다음문제로 넘어갑니다!");
                    Toast toast1 = new Toast(getApplicationContext());
                    toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast1.setDuration(Toast.LENGTH_LONG);
                    toast1.setView(layout1);
                    toast1.show();

                    flag = 0;

                    break;
                case S2P_NEW_STAGE:
                    if (mCountDownTimer != null) {
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
                    //chatText.setFocusable(true);
                    //chatText.setFocusableInTouchMode(true);
                    // test
                }
            }
        }

        public void stop () {
            super.cancel();
        }
    }

    private void startGuessAnswer(){
        chatText.setFocusable(true);
        chatText.setFocusableInTouchMode(true);                 //힌트가 왔을때 답 입력 가능하게끔

        LayoutInflater inflater4 = getLayoutInflater();
        View layout4 = inflater4.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text4 = (TextView) layout4.findViewById(R.id.text);
        text4.setText("40초 start!");
        Toast toast4 = new Toast(getApplicationContext());
        toast4.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast4.setDuration(Toast.LENGTH_LONG);
        toast4.setView(layout4);
        toast4.show();


        mCountDownTimer = new SolvingTimer(40, 1000);
        mCountDownTimer.start();

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

            //chatText.setFocusable(true);
            //chatText.setFocusableInTouchMode(true);
            Toast.makeText(GamePlay.this, "Waitinf for Hint", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEnterButtonClicked(View v){
        sendGuessAnswer();
    }




    private Timer timer;
    private ChatClearCountDownTimer chatClearCountDownTimer;

    public void showGuessAnswer(final int number, String mesg, final int check){

        chatTextView[number].setText(mesg);

        if(check == 1){
            chatClearCountDownTimer = new ChatClearCountDownTimer(chatTextView[number], ChatClearCountDownTimer.CORRET_ANSWER, 5000, 1000);
            chatClearCountDownTimer.start();
        }
        else{
            chatClearCountDownTimer = new ChatClearCountDownTimer(chatTextView[number], ChatClearCountDownTimer.INCORRECT_ANSWER, 5000, 1000);
            chatClearCountDownTimer.start();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}
