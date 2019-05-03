package com.example.so.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.so.project.R;

public class LoginActivity extends AppCompatActivity {

    EditText myID = null;
    EditText myIP = null;
    EditText myPort = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myID = (EditText)findViewById(R.id.ID);
        myIP = (EditText)findViewById(R.id.editText_ip);
        myPort = (EditText)findViewById(R.id.editText_port);

        Button button_login = (Button)findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ReadyRoom.class);


                intent.putExtra("id",myID.getText().toString());

                intent.putExtra("ip",myIP.getText().toString());
                Log.d("ip넘김확인",myIP.getText().toString());

                intent.putExtra("port",myPort.getText().toString());
                Log.d("port넘김확인",myPort.getText().toString());


                Log.d("dd", myID.getText().toString());

                startActivity(intent);
            }
        });
    }
}
