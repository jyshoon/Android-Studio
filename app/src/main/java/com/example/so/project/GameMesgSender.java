package com.example.so.project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class GameMesgSender extends Thread{

    Socket sock;
    String message;
    public GameMesgSender(Socket s, String type, String data){
        sock = s;
        message = type + " " + data;
    }

    public void run(){
        sendMesg(message);
    }

    public void sendMesg(String msg){
        OutputStream outstream = null;
        try {
            outstream = sock.getOutputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outstream));

            bw.write(msg);
            bw.newLine();
            bw.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
