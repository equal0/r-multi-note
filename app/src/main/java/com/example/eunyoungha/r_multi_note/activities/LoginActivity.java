package com.example.eunyoungha.r_multi_note.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eunyoungha.r_multi_note.APIRequest;
import com.example.eunyoungha.r_multi_note.APIResponse;
import com.example.eunyoungha.r_multi_note.R;
import com.example.eunyoungha.r_multi_note.interfaces.NoteAPICallback;
import com.example.eunyoungha.r_multi_note.models.UserInformation;

public class LoginActivity extends AppCompatActivity implements NoteAPICallback{

    private EditText mLoginId;
    private EditText mLoginPassword;
    private Button mLoginBtn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar setup
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get view references
        mLoginId = (EditText) findViewById(R.id.text_id);
        mLoginPassword = (EditText) findViewById(R.id.text_password);
        mLoginBtn = (Button) findViewById(R.id.button_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLoginInfo();
            }
        });

    }

    private void sendLoginInfo(){
        String []parameter = {"login","http://10.0.2.2:8080/engine/api/RNote/login"};
        new NoteAPI(getApplicationContext(),this).execute(parameter);
    }

    @Override
    public void processFinish(APIResponse response) {
        UserInformation user = response.getUser();

        String id = user.getUserId();
        String pw = user.getUserPassword();

        String inputId = mLoginId.getText().toString();
        String inputPw = mLoginPassword.getText().toString();

        if(id.equals(inputId) && pw.equals(inputPw)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }
}
