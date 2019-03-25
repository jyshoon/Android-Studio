package com.example.so.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ReadyRoom extends AppCompatActivity {

    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_room);

        Intent intent = getIntent();
        myID = intent.getExtras().getString("id");


        Log.d("dd", myID);

        Button enterbutton = (Button)findViewById(R.id.enterbutton);

        enterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GameReady.class);

                intent.putExtra("id",myID);

                startActivity(intent);
            }
        });
    }
}
