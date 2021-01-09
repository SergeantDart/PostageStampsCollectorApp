package com.example.postagestampscollectorapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.List;

public class UserSignupActivity extends AppCompatActivity {

    EditText fullNameRegistrationEditText;
    EditText usernameRegistrationEditText;
    EditText passwordRegistrationEditText;
    EditText emailRegistrationEditText;
    EditText phoneRegistrationEditText;
    EditText shortBioRegistrationEditText;

    //Firebase database
    FirebaseDatabase fbDatabase;
    Query usernamesQuery;
    ValueEventListener valueEventListener;

    //SQLite database
    Database database;
    UserDao userDao;

    List<String> usernamesList;

    boolean usernameAlreadyExists;

    String typedUsername;

    String fullName;
    String password;
    String phone;
    String email;
    String shortBio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        database= Room.databaseBuilder(getApplicationContext(), Database.class,"myDatabase").fallbackToDestructiveMigration().build();
        fbDatabase = FirebaseDatabase.getInstance();
        userDao = database.userDao();

        fullNameRegistrationEditText = (EditText) findViewById(R.id.fullNameRegistrationEditText);
        usernameRegistrationEditText = (EditText) findViewById(R.id.usernameRegistrationEditText);
        passwordRegistrationEditText = (EditText) findViewById(R.id.passwordRegistrationEditText);
        emailRegistrationEditText = (EditText) findViewById(R.id.emailRegistrationEditText);
        phoneRegistrationEditText = (EditText) findViewById(R.id.phoneRegistrationEditText);
        shortBioRegistrationEditText = (EditText) findViewById(R.id.shortBioRegistrationEditText);

        usernamesList = new ArrayList<>();
    }

    //the create new account button onclick function
    public void createNewAccount(View v) {

        new CheckIfUsernameExists().execute();
    }

    //async task for adding and inserting a new user to the DB
    protected class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao dao;

        InsertUserAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            dao.addUser(params[0]);
            fbDatabase.getReference("users").child("user - " + Integer.valueOf(params[0].getUserId()) ).setValue(params[0]);
            DatabaseReference userReference = fbDatabase.getReference("users/user - " + Integer.valueOf(params[0].getUserId()));
            userReference.child("collections").setValue("null");
            return null;
        }
    }

    protected class CheckIfUsernameExists extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            usernamesQuery = fbDatabase.getReference("users").orderByChild("username");

            //ValueEventListener is a kind of what to do once the query is done, the results being stored in snapshot
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //checking if such a match exists ( the snapshot is delivered by default through a list even though it contains only 1 element ! )
                    String username;
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            //add all the Firebase DB usernames in the usernames list
                            username = userSnapshot.child("username").getValue(String.class);
                            usernamesList.add(username);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //nothing
                }
            };
            //starting the query
            usernamesQuery.addValueEventListener(valueEventListener);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //get user input
            typedUsername = usernameRegistrationEditText.getText().toString();
            fullName = fullNameRegistrationEditText.getText().toString();
            password = passwordRegistrationEditText.getText().toString();
            email = emailRegistrationEditText.getText().toString();
            phone = phoneRegistrationEditText.getText().toString();
            shortBio = shortBioRegistrationEditText.getText().toString();

            usernameAlreadyExists = false;

            //check if username already exists in the Firebase DB
            for (String username : usernamesList) {
                if (username.equals(typedUsername)) {
                    usernameAlreadyExists = true;
                }
            }
            //data validating
            if (typedUsername.equals("")) {
                Toast.makeText(getApplicationContext(), "Enter a username !", Toast.LENGTH_SHORT).show();
            } else {
                if (usernameAlreadyExists == true) {
                    Toast.makeText(getApplicationContext(), "This username already exists !", Toast.LENGTH_SHORT).show();
                } else {
                    if (fullName.equals("")) {
                        Toast.makeText(getApplicationContext(), "Enter your name !", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.equals("") || password.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long !", Toast.LENGTH_SHORT).show();
                        } else {
                            if (email.equals("")) {
                                Toast.makeText(getApplicationContext(), "Enter your email !", Toast.LENGTH_SHORT).show();
                            } else {
                                if (phone.equals("") || phone.length() != 10) {
                                    Toast.makeText(getApplicationContext(), "Enter your valid telephone number !", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (shortBio.equals("") || shortBio.length() < 10) {
                                        Toast.makeText(getApplicationContext(), "Bio must be at least 10 characters long !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        User user = new User(fullName, typedUsername, password, email, phone, shortBio);
                                        new InsertUserAsyncTask(userDao).execute(user);
                                        Toast.makeText(getApplicationContext(), "User added to DB !", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}