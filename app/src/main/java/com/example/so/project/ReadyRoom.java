package com.example.so.project;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class ReadyRoom extends AppCompatActivity {

    String myID;
    ListView roomListView;

    //ArrayList<String> roomList;
    //ArrayAdapter<String> listAdapter;
    RoomListAdapter listAdapter;
    ArrayList<RoomListItemData> roomList;

    private Socket sock;
    private boolean isConnected = false;

    private String addr = "192.168.0.114".trim();
    private int port = 8002;
    private ConnectThread connectThread;
    private ReadyRoomMesgRecv recvThread;
    private MessageHandler mesgHandler;

    private String selectedRoomName = "";

    private EditText roomText;
    private int imgId = 0;

    Socket getSocket () {
        return sock;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_room);

        Intent intent = getIntent();



        myID = intent.getExtras().getString("id");
        Log.d("dd", myID);
        roomText = (EditText)findViewById(R.id.roomText);
        Button enterbutton = (Button)findViewById(R.id.enterbutton);

        enterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMesg("P2S_ENTER_ROOM", selectedRoomName);
            }
        });

        Button createButton = (Button)findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedRoomName = roomText.getText().toString();
                sendMesg("P2S_CREATE_ROOM", selectedRoomName);

            }
        });


        roomList = new ArrayList<RoomListItemData>();
        listAdapter = new RoomListAdapter(roomList);

        roomListView = (ListView)findViewById(R.id.roomListView);
        roomListView.setAdapter(listAdapter);


        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RoomListItemData roomItemData = (RoomListItemData) parent.getItemAtPosition(position);

                selectedRoomName = roomItemData.getRoomName();

            }
        });

        Button roomListRefresh = (Button)findViewById(R.id.roomListRefresh);
        roomListRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMesg("P2S_REQ_ROOM_LIST");
            }
        });

        mesgHandler = new MessageHandler();
        initNetwork ();

//랜덤으로 레디룸 이미지(캐릭터) 띄우기 여기부터
        Random r = new Random();
        int randNum = r.nextInt(5);

        if (randNum == 0) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c1);
            imgId = R.drawable.c1;
        }
        if (randNum == 1) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c2);
            imgId = R.drawable.c2;

        }
        if (randNum == 2) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c3);
            imgId = R.drawable.c3;

        }
        if (randNum == 3) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c4);
            imgId = R.drawable.c4;

        }
        if (randNum == 4) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c5);
            imgId = R.drawable.c5;

        }
        if (randNum == 5) {
            ImageView test = (ImageView)findViewById(R.id.imageView);
            test.setVisibility(View.VISIBLE);
            test.setImageResource(R.drawable.c6);
            imgId = R.drawable.c6;

        }
//랜덤으로 레디룸 이미지(캐릭터) 띄우기 여기까지
    }

    private void recvRoomList (String mesg) {
        String[] parsedStr;
        parsedStr = mesg.split("####");

        int roomNum = Integer.parseInt(parsedStr[1]);
        roomList.clear();
        for (int i = 0; i < roomNum; i++) {

            //roomList.add(parsedStr[2+i]);
            String[] roomInfo = parsedStr[2+i].split("::");
            roomList.add(new RoomListItemData(roomInfo[0], roomInfo[1], roomInfo[2]));
        }
        listAdapter.notifyDataSetChanged();



    }

    private void enterRoom () {

        Intent intent = new Intent(getApplicationContext(),GameReady.class);
        intent.putExtra("id",myID);
        intent.putExtra("imgId", imgId );

        startActivity(intent);
        Log.d("HHHHHHHHHHHHHHHH", myID);
    }

    private void showRoomEnterFail ()
    {
        Toast.makeText(this, "Fail to enter room " + selectedRoomName, Toast.LENGTH_LONG).show();
    }


    private void createRoom () {

        Intent intent = new Intent(getApplicationContext(),GameReady.class);
        intent.putExtra("id",myID);

        startActivity(intent);
        Log.d("HHHHHHHHHHHHHHHH", myID);
    }

    private void createRoomFail ()
    {
        Toast.makeText(this, "Fail to create room " + selectedRoomName, Toast.LENGTH_LONG).show();
    }

    public static final int S2P_SEND_ROOM_LIST = 300;
    public static final int S2P_ENTER_ROOM_OK = 301;
    public static final int S2P_ENTER_ROOM_FAIL = 302;
    public static final int S2P_CREATE_ROOM_OK = 303;
    public static final int S2P_CREATE_ROOM_FAIL = 304;


    class MessageHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){

                case S2P_SEND_ROOM_LIST:
                    recvRoomList ((String)msg.obj);
                    break;
                case S2P_ENTER_ROOM_OK:
                    enterRoom ();
                    break;
                case S2P_ENTER_ROOM_FAIL:
                    showRoomEnterFail ();
                    break;
                case S2P_CREATE_ROOM_OK:
                    createRoom ();
                    break;
                case S2P_CREATE_ROOM_FAIL:
                    createRoomFail ();
                    break;
                    /*
                case S2P_START_GAME:
                    startGame();
                    break;
                case S2P_SEND_GAME_READY_CHAT:
                    setMessage(msg.arg1, (String)msg.obj);
                    break;*/
            }
        }
    }
    public MessageHandler getHandler(){
        return mesgHandler;
    }



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


            } catch (UnknownHostException e) {
                Log.d("eeeeeeeeeeeeee","eeeeeeeeeeeee");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("xxxxxxxxxx","xxxxxxxxxxx");
                e.printStackTrace();
            }
        }
    }

    private void sendMesg (String type) {
        GameMesgSender sendThread = new GameMesgSender(sock, type);
        sendThread.start ();
    }
    private void sendMesg (String type, String data) {
        GameMesgSender sendThread = new GameMesgSender(sock, type, data);
        sendThread.start();
    }
    private void sendMesg(String type, String[] args){
        GameMesgSender sendThread = new GameMesgSender(sock, type, args);
        sendThread.start();
    }

    private void initNetwork(){


        connectThread = new ConnectThread(addr);
        connectThread.start();

        while(isConnected == false);

        recvThread = new ReadyRoomMesgRecv(this);
        recvThread.start();



    }

    @Override
    public void onBackPressed () {
        Toast.makeText(this, "One more back for exit.", Toast.LENGTH_SHORT).show ();
    }

}
