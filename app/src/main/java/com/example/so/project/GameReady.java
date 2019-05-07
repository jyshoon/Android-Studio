package com.example.so.project;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class GameReady extends AppCompatActivity {
    TextView textView3; //채팅내용변수


    private TextView[] idTextView = new TextView[4];
    private TextView[] chatTextView = new TextView[4];
    private TextView[] readyTextView = new TextView[4];
    private ImageView[] characterView = new ImageView[4];

    private EditText sendText;
    private String myID;
    private int myImgId;
    private int myNumber;
    private Button btnGameReady;
    private GameReadyMesgRecv recvThread;

    public Socket sock;

    private MessageHandler mesgHandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ready);

        Intent intent = getIntent();
        myID = intent.getExtras().getString("id");
        myImgId = intent.getExtras().getInt("imgId");




        idTextView[0] = (TextView)findViewById(R.id.id0);
        idTextView[1] = (TextView)findViewById(R.id.id1);
        idTextView[2] = (TextView)findViewById(R.id.id2);
        idTextView[3] = (TextView)findViewById(R.id.id3);

        chatTextView[0] = (TextView)findViewById(R.id.chatTextView0);
        chatTextView[1] = (TextView)findViewById(R.id.chatTextView1);
        chatTextView[2] = (TextView)findViewById(R.id.chatTextView2);
        chatTextView[3] = (TextView)findViewById(R.id.chatTextView3);

        readyTextView[0] = (TextView) findViewById(R.id.readyTextView0);
        readyTextView[1] = (TextView) findViewById(R.id.readyTextView1);
        readyTextView[2] = (TextView) findViewById(R.id.readyTextView2);
        readyTextView[3] = (TextView) findViewById(R.id.readyTextView3);

        characterView[0] = (ImageView) findViewById(R.id.user1character);
        characterView[1] = (ImageView) findViewById(R.id.user2character);
        characterView[2] = (ImageView) findViewById(R.id.user3character);
        characterView[3] = (ImageView) findViewById(R.id.user4character);
        for (int i = 0; i < 4; i++) {
            characterView[i].setVisibility(View.INVISIBLE);
            characterView[i].setTag(0);
        }


        sendText = (EditText)findViewById(R.id.sendTextView);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(20);
        sendText.setFilters(FilterArray);

        btnGameReady = (Button)findViewById(R.id.btn_GameReady);

        mesgHandler = new MessageHandler();

        initNetwork();


    }
/*
    public void enterclicked(View v) throws InterruptedException {

        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        EditText editText4 = (EditText) findViewById(R.id.editText4);
        textView3.setVisibility(View.VISIBLE);
        textView3.setText(editText4.getText());
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                TimerTask task = new TimerTask(){
                    @Override
                    public void run(){
                        textView3.setVisibility(View.INVISIBLE);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task,3000);
            }
        });
        thread.start();
    }
*/
    public void onButtonGameReadyClicked(View v){
        btnGameReady.setText("준비완료");
        sendMesg("P2S_READY_GAME", myNumber+"");
    }

    public void onEnterClicked(View v){
        String args[] = new String[2];
        args[0] = myNumber +"";
        args[1] = sendText.getText().toString();

        sendMesg("P2S_SEND_GAME_READY_CHAT", args);

        sendText.setText("");
    }


    public Socket getSocket(){
        return sock;
    }


/*
    class ConnectThread extends Thread{
        String hostname;
        public ConnectThread(String addr){
            hostname=addr;
        }
        public void run(){
            try{

                sock = new Socket(hostname,port);

                SocketSingleton.setSocket(sock);

                isConnected = true;
//
//                OutputStream outstream = null;
//                outstream = sock.getOutputStream();
//
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outstream));
//
//
//                bw.write(myID);
//                bw.newLine();
//                bw.flush();

                Log.d("dffjjjjjjjjfffff","fffjjjjjjjjjjjjjjjjjjjjjjjff");

            } catch (UnknownHostException e) {
                Log.d("eeeeeeeeeeeeee","eeeeeeeeeeeee");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("xxxxxxxxxx","xxxxxxxxxxx");
                e.printStackTrace();
            }
        }
    }
*/
    private void sendMesg(String type, String data){
        GameMesgSender sendThread = new GameMesgSender(sock, type,data);
        sendThread.start();
    }

    private void sendMesg (String type) {
        GameMesgSender sendThread = new GameMesgSender (sock, type);
        sendThread.start ();
    }

    private void sendMesg (String type, String[] args) {
        GameMesgSender sendThread = new GameMesgSender (sock, type, args);
        sendThread.start ();
    }

    private void initNetwork(){

//        connectThread = new ConnectThread(addr);
//        connectThread.start();
//
//        while(isConnected == false);

        sock = SocketSingleton.getSocket();

        recvThread = new GameReadyMesgRecv(this);
        recvThread.start();

        String args[] = new String[2];
        args[0] = myID;
        args[1] = myImgId + "";

        sendMesg("P2S_CONNECT_CLIENT", args);

    }




    public static final int S2P_CLIENT_NUMBER = 100;
    public static final int S2P_START_GAME = 101;
    public static final int S2P_SEND_GAME_READY_CHAT = 102;
    public static final int S2P_PLAYER_GAME_READY = 103;
    public static final int REQUEST_CODE_GAMEPLAY = 404;



    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case S2P_CLIENT_NUMBER:
                    connectNewID(msg.arg1,(String)msg.obj, msg.arg2);
                    break;
                case S2P_START_GAME:
                    startGame(msg.arg1);
                    break;
                case S2P_SEND_GAME_READY_CHAT:
                    setMessage(msg.arg1, (String)msg.obj);
                    break;
                case S2P_PLAYER_GAME_READY:
                    setReady(msg.arg1);
                    Toast.makeText(getApplicationContext(),"ddddddddddffffffffffffff",Toast.LENGTH_LONG ).show();
                    break;
            }
        }
    }
    public MessageHandler getHandler(){
        return mesgHandler;
    }





    private void startGame(int numPlayer){
        Intent intent = new Intent(getApplicationContext(),GamePlay.class);

        intent.putExtra("myNum", myNumber);
        intent.putExtra("myID",myID);
        intent.putExtra ("myResId", myImgId);
        intent.putExtra ("numPlayer", numPlayer);


        intent.putExtra("player0",idTextView[0].getText().toString());
        intent.putExtra("player1",idTextView[1].getText().toString());
        intent.putExtra("player2",idTextView[2].getText().toString());                                //////////////////////////////////////////에러
        intent.putExtra("player3",idTextView[3].getText().toString());
        intent.putExtra("player0ResId", (Integer)characterView[0].getTag());
        intent.putExtra("player1ResId", (Integer)characterView[1].getTag());
        intent.putExtra("player2ResId", (Integer)characterView[2].getTag());
        intent.putExtra("player3ResId", (Integer)characterView[3].getTag());

        startActivityForResult(intent,REQUEST_CODE_GAMEPLAY);
        //startActivity(intent);
    }

    public void connectNewID(int num, String ID, int resId){
        if(ID.compareTo(myID) == 0)
            myNumber = num;

        idTextView[num].setText(ID);
        characterView[num].setVisibility(View.VISIBLE);
        characterView[num].setImageResource (resId);
        characterView[num].setTag(resId);

        Log.d("GGGGGGGGGGGG", ID + num+"");


    }

    private Timer timer;
    private ChatClearCountDownTimer chatClearCountDownTimer;

    public void setMessage(final int number, String mesg){

        chatTextView[number].setText(mesg);

        chatClearCountDownTimer = new ChatClearCountDownTimer(chatTextView[number],ChatClearCountDownTimer.CHAT, 2000, 1000);
        chatClearCountDownTimer.start();

    }

    public void setReady(int number){
        readyTextView[number].setText("준비완료");
        Toast.makeText(getApplicationContext(),"dddddddddd",Toast.LENGTH_LONG ).show();
    }

    @Override
    public void onBackPressed () {
        Toast.makeText(this, "One more back for exit.", Toast.LENGTH_SHORT).show ();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ssssss","ssss");
        if (resultCode == RESULT_OK) {
            Log.d("dddddd","dddd");
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }

    }

}