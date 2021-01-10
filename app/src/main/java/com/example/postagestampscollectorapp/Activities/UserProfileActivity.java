package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Data.User;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.UserDao;
import com.example.postagestampscollectorapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    EditText profileUsernameEditText;
    EditText profilePasswordEditText;
    EditText profileFullNameEditText;
    EditText profileTelephoneEditText;
    EditText profileEmailEditText;
    EditText profileShortBioEditText;

    Button profileSaveButton;
    Button profileModifyButton;

    FirebaseDatabase fbDatabase;

    Database database;
    UserDao userDao;

    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        currentUserId = getIntent().getIntExtra("userId",0);

        profileUsernameEditText = (EditText)findViewById(R.id.profileUsernameEditText);
        profileFullNameEditText = (EditText)findViewById(R.id.profileFullNameEditText);
        profilePasswordEditText = (EditText)findViewById(R.id.profilePasswordEditText);
        profileEmailEditText = (EditText)findViewById(R.id.profileEmailEditText);
        profileTelephoneEditText = (EditText)findViewById(R.id.profileTelephoneEditText);
        profileShortBioEditText = (EditText)findViewById(R.id.profileShortBioEditText);

        profileFullNameEditText.setEnabled(false);
        profilePasswordEditText.setEnabled(false);
        profileEmailEditText.setEnabled(false);
        profileTelephoneEditText.setEnabled(false);
        profileShortBioEditText.setEnabled(false);
        profileUsernameEditText.setEnabled(false);

        profileSaveButton = (Button)findViewById(R.id.saveProfileButton);
        profileModifyButton = (Button)findViewById(R.id.modifyProfileButton);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        userDao = database.userDao();

        fbDatabase = FirebaseDatabase.getInstance();

        new GetUserProfileInfoAsyncTask(userDao).execute(currentUserId);
    }

    //on click function for the modify buttton
    public void modifyProfile(View v){
        profileFullNameEditText.setEnabled(true);
        profilePasswordEditText.setEnabled(true);
        profileEmailEditText.setEnabled(true);
        profileTelephoneEditText.setEnabled(true);
        profileShortBioEditText.setEnabled(true);

        profileModifyButton.setVisibility(View.INVISIBLE);
        profileSaveButton.setVisibility(View.VISIBLE);
    }

    //on click function for canceling the activity
    public void cancelProfile(View v){
        finish();
    }

    //on click function for the save button
    public void saveProfile(View v){
        String fullName = profileFullNameEditText.getText().toString();
        String username = profileUsernameEditText.getText().toString();
        String password = profilePasswordEditText.getText().toString();
        String email = profileEmailEditText.getText().toString();
        String telephone = profileTelephoneEditText.getText().toString();
        String shortBio = profileShortBioEditText.getText().toString();

        //validating data integrity
        if(fullName.equals("")){
            Toast.makeText(getApplicationContext(), "Enter your full name !", Toast.LENGTH_SHORT).show();
        }else{
            if(password.equals("") || password.length() < 6){
                Toast.makeText(getApplicationContext(), "Your password must be at least 6 characters long !", Toast.LENGTH_SHORT).show();
            }else{
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Enter your email !", Toast.LENGTH_SHORT).show();
                }else{
                    if(telephone.equals("") || telephone.length() != 10){
                        Toast.makeText(getApplicationContext(), "Enter a valid phone number !", Toast.LENGTH_SHORT).show();
                    }else{
                        if(shortBio.equals("") || shortBio.length() < 10){
                            Toast.makeText(getApplicationContext(), "The user bio must be at least 10 characters long !", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            new UpdateProfileInfoAsyncTask(userDao).execute(currentUserId, fullName, username, password, email, telephone, shortBio);
                        }
                    }
                }
            }
        }

    }

    //async task for updating the user profile both in the SQLite and in the Firebase DB
    protected class UpdateProfileInfoAsyncTask extends AsyncTask<Object, Void, Void>{

        UserDao userDao;

        UpdateProfileInfoAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            userDao.updateUser((int)objects[0],(String)objects[1], (String)objects[2], (String)objects[3], (String)objects[4], (String)objects[5], (String)objects[6]);
            DatabaseReference currentUserReference = fbDatabase.getReference("users/user - " + Integer.valueOf(currentUserId));
            currentUserReference.child("fullName").setValue((String)objects[1]);
            currentUserReference.child("username").setValue((String)objects[2]);
            currentUserReference.child("password").setValue((String)objects[3]);
            currentUserReference.child("email").setValue((String)objects[4]);
            currentUserReference.child("phone").setValue((String)objects[5]);
            currentUserReference.child("shortBio").setValue((String)objects[6]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }

    //async task for fetching profile info from the SQLite DB ( it is not neccesary to fetch from Firebase DB)
    protected class GetUserProfileInfoAsyncTask extends AsyncTask<Integer, Void, User>{
        UserDao userDao;

        GetUserProfileInfoAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }

        @Override
        protected User doInBackground(Integer... integers) {
            return userDao.getUserById(integers[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            profileFullNameEditText.setText(user.getFullName());
            profileUsernameEditText.setText(user.getUsername());
            profilePasswordEditText.setText(user.getPassword());
            profileEmailEditText.setText(user.getEmail());
            profileTelephoneEditText.setText(user.getPhone());
            profileShortBioEditText.setText(user.getShortBio());
        }
    }
}