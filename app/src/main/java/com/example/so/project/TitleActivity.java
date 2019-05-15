package com.example.so.project;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.so.project.LoginActivity;
import com.example.so.project.R;
import com.example.so.project.RegisterActivity;

public class TitleActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_LOGIN = 101;
    public static final int REQUEST_CODE_REGISTER = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
    }

    public void onButtonLoginClicked(View v){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }
    public void onButtonRegisterClicked(View v){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

}