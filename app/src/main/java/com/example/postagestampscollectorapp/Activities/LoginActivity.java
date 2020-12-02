package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.R;
import com.example.postagestampscollectorapp.Database.UserDao;

public class LoginActivity extends AppCompatActivity {

     Database database;
     UserDao userDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       // Button loginButton = (Button) findViewById(R.id.loginButton);
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        userDao = database.userDao();

    }

    protected class GetUserIdAsyncTask extends  AsyncTask<String, Void, Integer>{
        UserDao dao;

        GetUserIdAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            return dao.getUserId(strings[0],strings[1]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("userId",integer);
            //database.close();
            startActivity(intent);
        }
    }

    protected class CheckIfAccountExistsAsyncTask extends AsyncTask<String, Void, Integer> {

        UserDao dao;
        String username;
        String password;
        CheckIfAccountExistsAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            username=strings[0];
            password=strings[1];
            return dao.searchIfAccountExists(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (count == 1) {
                new GetUserIdAsyncTask(dao).execute(username,password);
            } else if (count == 0) {
                Toast.makeText(getApplicationContext(), "Invalid username or passowrd! Try again...", Toast.LENGTH_SHORT).show();
                EditText username = (EditText) findViewById(R.id.usernameEditText);
                username.setText("");
                EditText password = (EditText) findViewById(R.id.passwordEditText);
                password.setText("");
            }
        }
    }

    public void loginAction(View v) {
        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        new CheckIfAccountExistsAsyncTask(userDao).execute(username, password);

    }

    public void openUserSignupActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), UserSignupActivity.class);
        startActivity(intent);
    }

}


