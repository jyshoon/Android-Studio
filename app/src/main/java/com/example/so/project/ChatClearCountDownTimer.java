package com.example.so.project;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class ChatClearCountDownTimer extends CountDownTimer {
    public static final int CORRET_ANSWER = 1;
    public static final int INCORRECT_ANSWER = 2;
    public static final int CHAT = 3;

    private TextView chatView;
    private long mTimeLeftInMillis = 0;

    public ChatClearCountDownTimer (TextView _chatView, int type, long millsLeft, int interval) {
        super(millsLeft, interval);
        chatView = _chatView;
        chatView.setVisibility(View.VISIBLE);

        if(type == CORRET_ANSWER){
            chatView.setBackgroundResource(R.drawable.ccc);
        }
        else if(type == INCORRECT_ANSWER){
            chatView.setBackgroundResource(R.drawable.ccc);

        }
        else{
            chatView.setBackgroundResource(R.drawable.aaa);
        }
    }

    public void onTick(long millisUntilFinished) {
        mTimeLeftInMillis = millisUntilFinished;
    }

    @Override
    public void onFinish() {
        chatView.setVisibility(View.INVISIBLE);
    }

}
