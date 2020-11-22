package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username=(EditText)findViewById(R.id.usernameEditText);
        EditText password=(EditText)findViewById(R.id.passwordEditText) ;
        Button blogin = (Button)findViewById(R.id.loginButton);

        blogin.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                String user = username.getText().toString();
                String passWord=password.getText().toString();

                if(user.equals("username") && passWord.equals("password")){
                    Toast.makeText(getApplicationContext(), "Redirecting..",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if(user.equals("")&&passWord.equals("")){
                    Toast.makeText(getApplicationContext(), "Please fill the required fields!",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password. Please try again!",
                          Toast.LENGTH_LONG).show();
               }

            }
        });

    }

}


