 package com.example.postagestampscollectorapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Data.User;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.Database.StampsCollectionDao;
import com.example.postagestampscollectorapp.R;
import com.example.postagestampscollectorapp.Database.UserDao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import io.grpc.Context;

 public class LoginActivity extends AppCompatActivity {

     //SQLite database
     Database database;
     UserDao userDao;
     StampsCollectionDao stampsCollectionDao;
     PostageStampDao postageStampDao;

     //Firebase database
     FirebaseDatabase fbDatabase;
     int activeTasks;

     //login query for Firebase DB
     Query loginQuery;
     //to enable the query we need to add to it a ValueEventListener, it's mandatory with any kind of Firebase DB query, otherwise the query won't start
     ValueEventListener valueEventListener;

     //boolean used to know then do deactivate the FB query ( if needed)
     boolean hasLogged;

     EditText usernameEditText;
     EditText passwordEditText;

     @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        userDao = database.userDao();
        stampsCollectionDao = database.stampsCollectionDao();
        postageStampDao = database.postageStampDao();

        fbDatabase = FirebaseDatabase.getInstance();

        //to know when all the async tasks are done, in EACH async task we do the following:
        //-- increment activeTasks when an async task begins,
        //-- decrement activeTasks when an async task ends
        //in the end, once all of them are finished we should have activeTasks = 0
        activeTasks = 0;

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        SharedPreferences settings = getSharedPreferences("LOGIN_INFO", 0);
        String rememberedUsername = settings.getString("username", "");
        String rememberedPassword = settings.getString("password", "");
        String lastLoggedInDate = settings.getString("lastLoggedInDate", "");

        TextView loggedInDateLabelTextView = (TextView)findViewById(R.id.loggedInDateLabelTextView);
        TextView lastLoginDateTextView = (TextView) findViewById(R.id.lastLoginDateTextView);

        if(!rememberedUsername.equals("")) {
            if (lastLoggedInDate.equals("")) {
                loggedInDateLabelTextView.setVisibility(View.INVISIBLE);
            } else {
                loggedInDateLabelTextView.setVisibility(View.VISIBLE);
            }

            lastLoginDateTextView.setText(lastLoggedInDate);
        }

        usernameEditText.setText(rememberedUsername);
        passwordEditText.setText(rememberedPassword);
    }

     @Override
     protected void onPause() {
         super.onPause();
         if(hasLogged == true) {
             loginQuery.removeEventListener(valueEventListener);
         }
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         if(hasLogged == true){
             loginQuery.removeEventListener(valueEventListener);
         }
     }

     //async task for querying a match for the credentials, deleting the existing SQLite DB data and populating it with the current user's data from Firebase
    protected class CheckIfAccountExistsAsyncTask extends AsyncTask<String, Void, Void> {

        UserDao userDao;
        PostageStampDao postageStampDao;
        StampsCollectionDao stampsCollectionDao;
        String username;
        String password;


         CheckIfAccountExistsAsyncTask(UserDao userDao, StampsCollectionDao stampsCollectionDao, PostageStampDao postageStampDao) {
            this.userDao = userDao;
            this.postageStampDao = postageStampDao;
            this.stampsCollectionDao = stampsCollectionDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            activeTasks = 0;

            username=strings[0];
            password=strings[1];

            loginQuery = fbDatabase.getReference("users").orderByChild("username").equalTo(username);

            //ValueEventListener is a kind of what to do once the query is done, the results being stored in snapshot
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //checking if such a match exists ( the snapshot is delivered by default through a list even though it contains only 1 element ! )
                    activeTasks = 0;
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {

                            //automatically creates an user object based on the matched attributes from the Firebase DB
                            User user = userSnapshot.getValue(User.class);
                            if (user.getPassword().equals(password)) {
                                //emptying the current SQLite DB ( async tasks for doing these actions are mandatory )
                                new DeleteAllUsersAsyncTask(userDao).execute();
                                new DeleteAllCollectionsAsyncTask(stampsCollectionDao).execute();
                                new DeleteAllStampsAsyncTask(postageStampDao).execute();

                                while(activeTasks != 0){
                                    Log.i("SQLite DB status: ", "not empty yet...");
                                }

                                //adding the current user to SQLite DB from Firebase DB
                                new InsertNewUserAsyncTask(userDao).execute(user);

                                //iterating through the current user's collections and adding them to SQLite DB from Firebase DB
                                DataSnapshot collectionsSnapshot = userSnapshot.child("collections");
                                if (collectionsSnapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot collectionSnapshot : collectionsSnapshot.getChildren()) {

                                        //automatically creates a stamps collection object based on the matched attributes from the Firebase DB
                                        StampsCollection stampsCollection = collectionSnapshot.getValue(StampsCollection.class);
                                        new InsertNewCollectionAsyncTask(stampsCollectionDao).execute(stampsCollection);

                                        //iterating through each of the current user's collection's stamps and adding them to SQLite DB from Firebase DB
                                        DataSnapshot stampsSnapshot = collectionSnapshot.child("stamps");
                                        if (stampsSnapshot.getChildrenCount() != 0) {
                                            for (DataSnapshot stampSnapshot : stampsSnapshot.getChildren()) {
                                                //getting the postage stamps attributes one by one in Firebase DB ( because we cannot do it directly as we did with users and collection beforehand
                                                int stampId = stampSnapshot.child("stampId").getValue(Integer.class);
                                                int collectionId = stampSnapshot.child("collectionId").getValue(Integer.class);
                                                String name = stampSnapshot.child("name").getValue(String.class);
                                                String country = stampSnapshot.child("country").getValue(String.class);
                                                int year = stampSnapshot.child("year").getValue(Integer.class);
                                                String description = stampSnapshot.child("description").getValue(String.class);
                                                String stampPicUri = stampSnapshot.child("stampPicUri").getValue(String.class);

                                                //download the image from the Firebase Storage based on the stampPicUri ( image address in Firebase Storage )
                                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                StorageReference storageReference = firebaseStorage.getReference(stampPicUri);
                                                storageReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    //we create the postage stamp object once the stamp picture is SUCCESFULLY downloaded from Firebase Storage
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        byte[] picBytes = bytes;
                                                        PostageStamp postageStamp = new PostageStamp(stampId, collectionId, name, picBytes, year, country, description);
                                                        new InsertNewStampAsyncTask(postageStampDao).execute(postageStamp);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                try{
                                    Thread.sleep(1500);
                                }catch(InterruptedException e){
                                    e.printStackTrace();
                                }

                                //we remember the last logged in user through shared preferences
                                SharedPreferences settings = getSharedPreferences("LOGIN_INFO", 0);
                                SharedPreferences.Editor editor = settings.edit();

                                Date currentTime = Calendar.getInstance().getTime();
                                editor.putString("lastLoggedInDate", currentTime.toString());
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.commit();;

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("userId", user.getUserId());
                                startActivity(intent);
                            }
                            else {
                                invalidResponse();
                            }
                        }
                    }
                    else {
                        invalidResponse();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            };

            hasLogged=true;
            //we add the ValueEventListener to the query to start it off
            loginQuery.addValueEventListener(valueEventListener);

            return null;
        }
    }

     //function for response when credentials are wrong
     public void invalidResponse(){
         Toast.makeText(getApplicationContext(), "Invalid username or passowrd! Try again...", Toast.LENGTH_SHORT).show();
         usernameEditText.setText("");
         passwordEditText.setText("");
     }

     //the login button onclick function
     public void loginAction(View v) {
         String username = usernameEditText.getText().toString();
         String password = passwordEditText.getText().toString();

         new CheckIfAccountExistsAsyncTask(userDao, stampsCollectionDao, postageStampDao).execute(username, password);
     }

     //the signup button onclick function, redirecting to the user signup activity
     public void openUserSignupActivity(View v) {
         Intent intent = new Intent(getApplicationContext(), UserSignupActivity.class);
         startActivity(intent);
     }

    //async task for deleting all the users data from SQLite DB
     protected class DeleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void>{
        UserDao userDao;

        DeleteAllUsersAsyncTask(UserDao userDao){

            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUsers();
            activeTasks++;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            activeTasks--;
        }
    }

     //async task for deleting all the collections data from SQLite DB
     protected class DeleteAllCollectionsAsyncTask extends AsyncTask<Void, Void, Void>{
         StampsCollectionDao stampsCollectionDao;

         DeleteAllCollectionsAsyncTask(StampsCollectionDao stampsCollectionDao){
             this.stampsCollectionDao = stampsCollectionDao;
         }

         @Override
         protected Void doInBackground(Void... voids) {
             stampsCollectionDao.deleteAllCollections();
             activeTasks++;
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             activeTasks--;
         }
     }

     //async task for deleting all the stamps data from SQLite DB
     protected class DeleteAllStampsAsyncTask extends AsyncTask<Void, Void, Void>{
         PostageStampDao postageStampDao;

         DeleteAllStampsAsyncTask(PostageStampDao postageStampDao){
             this.postageStampDao = postageStampDao;
         }

         @Override
         protected Void doInBackground(Void... voids) {
             postageStampDao.deleteAllStamps();
             activeTasks++;
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             activeTasks--;
         }
     }

     //async task for creating and inserting a new user to the SQLite DB
     protected class InsertNewUserAsyncTask extends AsyncTask<User,Void,Void>
     {
         UserDao userDao;

         InsertNewUserAsyncTask(UserDao userDao){

             this.userDao = userDao;
         }

         @Override
         protected Void doInBackground(User... users) {
             userDao.addUser(users[0]);
             activeTasks++;
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             activeTasks--;
         }
     }

     //async task for creating and inserting a new collection to the SQLite DB
     protected class InsertNewCollectionAsyncTask extends AsyncTask<StampsCollection,Void,Void>
     {
         StampsCollectionDao stampsCollectionDao;

         InsertNewCollectionAsyncTask(StampsCollectionDao stampsCollectionDao){
             this.stampsCollectionDao = stampsCollectionDao;
         }

         @Override
         protected Void doInBackground(StampsCollection... stampsCollections) {
             stampsCollectionDao.addStampCollection(stampsCollections[0]);
             activeTasks++;
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             activeTasks--;
         }
     }

     //async task for creating and inserting a new stamp to the SQLite DB
     protected class InsertNewStampAsyncTask extends AsyncTask<PostageStamp, Void, Void>
     {
         PostageStampDao postageStampDao;

         InsertNewStampAsyncTask(PostageStampDao postageStampDao){
             this.postageStampDao = postageStampDao;
         }

         @Override
         protected Void doInBackground(PostageStamp... postageStamps) {
             postageStampDao.addPostageStamp(postageStamps[0]);
             activeTasks++;
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             activeTasks--;
         }
     }
}


