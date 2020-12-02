package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Data.User;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.R;
import com.example.postagestampscollectorapp.Database.UserDao;

import java.util.List;

public class UserSignupActivity extends AppCompatActivity {

    EditText fullNameRegistrationEditText;
    EditText usernameRegistrationEditText;
    EditText passwordRegistrationEditText;
    EditText emailRegistrationEditText;
    EditText phoneRegistrationEditText;
    EditText shortBioRegistrationEditText;

    Database database;
    UserDao userDao;

    protected class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao dao;

        InsertUserAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            dao.addUser(params[0]);
            return null;
        }
    }

    protected class SelectAllUsersAsyncTask extends AsyncTask<Void, Void, List<User>> {

        UserDao dao;

        SelectAllUsersAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            return dao.getAllUsers();
        }

        @Override
        protected void onPostExecute(List<User> databaseUserDataList) {
            super.onPostExecute(databaseUserDataList);
            List<User> list = databaseUserDataList;
            for (User dud : list) {
                Log.i("users", "userId: " + dud.getUserId() + " ,username: " + dud.getUsername() + " ,passoword: " + dud.getPassword() + " ,fullName: " + dud.getFullName());
            }
        }
    }

    //private static Database database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        database= Room.databaseBuilder(getApplicationContext(), Database.class,"myDatabase").fallbackToDestructiveMigration().build();
        userDao = database.userDao();


    }

    public void createNewAccount(View v) {
        fullNameRegistrationEditText = (EditText) findViewById(R.id.fullNameRegistrationEditText);
        usernameRegistrationEditText = (EditText) findViewById(R.id.usernameRegistrationEditText);
        passwordRegistrationEditText = (EditText) findViewById(R.id.passwordRegistrationEditText);
        emailRegistrationEditText = (EditText) findViewById(R.id.emailRegistrationEditText);
        phoneRegistrationEditText = (EditText) findViewById(R.id.phoneRegistrationEditText);
        shortBioRegistrationEditText = (EditText) findViewById(R.id.shortBioRegistrationEditText);

        String fullName = fullNameRegistrationEditText.getText().toString();
        String username = usernameRegistrationEditText.getText().toString();
        String password = passwordRegistrationEditText.getText().toString();
        String email = emailRegistrationEditText.getText().toString();
        String phone = phoneRegistrationEditText.getText().toString();
        String shortBio = shortBioRegistrationEditText.getText().toString();

        User user = new User(fullName, username, password, email, phone, shortBio);

        new InsertUserAsyncTask(userDao).execute(user);
        new SelectAllUsersAsyncTask(userDao).execute();
        Toast.makeText(this, "User added to DB !", Toast.LENGTH_SHORT).show();


        finish();
    }
}