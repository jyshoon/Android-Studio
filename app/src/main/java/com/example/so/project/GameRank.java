package com.example.so.project;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

public class GameRank extends AppCompatActivity {

    private int numPlayer;
    private ImageView[] characterView = new ImageView[4];
    private TextView[] idTextView = new TextView[4];
    private TextView[] scoreView = new TextView[4];
    private int[] scores = new int[4];
    private int[] ranking = new int[4];
    private String myID;
    private int myImgResId;
    private TextView[] rank = new TextView[4];
    public static final int REQUEST_CODE_GAMEPLAY = 400;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rank);

        idTextView[0] = (TextView)findViewById(R.id.id0);
        idTextView[1] = (TextView)findViewById(R.id.id1);
        idTextView[2] = (TextView)findViewById(R.id.id2);
        idTextView[3] = (TextView)findViewById(R.id.id3);



        scoreView[0] = (TextView)findViewById(R.id.score0);
        scoreView[1] = (TextView)findViewById(R.id.score1);
        scoreView[2] = (TextView)findViewById(R.id.score2);
        scoreView[3] = (TextView)findViewById(R.id.score3);

        rank[0] = (TextView)findViewById(R.id.rank0);
        rank[1] = (TextView)findViewById(R.id.rank1);
        rank[2] = (TextView)findViewById(R.id.rank2);
        rank[3] = (TextView)findViewById(R.id.rank3);



        initGameRank();
        Button restartButton = (Button)findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(),GamePlay.class);
                //intent.putExtra("id",myID);
                Intent intent = new Intent();
                //intent.putExtra("result_msg", "결과가 넘어간다 얍!");
                setResult(RESULT_OK, intent);
                finish();

               // finish();
            }
        });

    }

    private void initGameRank() {
        Intent intent = getIntent();

        myID = intent.getExtras().getString("myID");
        myImgResId = intent.getIntExtra("myImgId", 0);

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
}
