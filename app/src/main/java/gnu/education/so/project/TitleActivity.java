package gnu.education.so.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.education.so.project.R;

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