package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.R;
import com.example.postagestampscollectorapp.Database.StampsCollectionDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionCreationActivity extends AppCompatActivity {

    String collectionName;
    boolean isPrivate;
    String collectionDescription;

    EditText collectionNameEditText;
    EditText collectionDescriptionEditText;
    RadioGroup collectionAccesabilitRadioGroup;

    //SQLite database
    Database database;
    StampsCollectionDao stampsCollectionDao;

    //Firebase database
    FirebaseDatabase fbDatabase;

    //current user's id
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_creation);

        fbDatabase = FirebaseDatabase.getInstance();
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();

        userId = getIntent().getIntExtra("userId", 0);

        //making the activity's size fit inside the main activity
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.9));

        //set public accesability by default
        RadioButton publicAccesabilityRadioButton = (RadioButton) findViewById(R.id.publicRadioButton);
        publicAccesabilityRadioButton.setChecked(true);

    }

    //the create new collection button onclick function
    public void createNewCollection(View v) {

        collectionNameEditText = (EditText) findViewById(R.id.collectionNameEditText);
        collectionDescriptionEditText = (EditText) findViewById(R.id.collectionDescriptionEditText);
        collectionAccesabilitRadioGroup = (RadioGroup) findViewById(R.id.collectionAccesabilityRadioGroup);

        collectionName = collectionNameEditText.getText().toString();
        collectionDescription = collectionDescriptionEditText.getText().toString();

        RadioGroup accesabilityRadioGroup = (RadioGroup) findViewById(R.id.collectionAccesabilityRadioGroup);
        RadioButton choiceButton = (RadioButton) findViewById(accesabilityRadioGroup.getCheckedRadioButtonId());
        String choice = choiceButton.getText().toString();


        if (choice.equals("Private")) {
            isPrivate = true;
        } else if (choice.equals("Public")) {
            isPrivate = false;
        }

        if (collectionName.equals("")) {
            Toast.makeText(getApplicationContext(), "Add a name to your collection !", Toast.LENGTH_SHORT).show();
        } else if (collectionDescription.equals("") || collectionDescription.length() < 10) {
            Toast.makeText(getApplicationContext(), "The collection description must be at least 10 characters long !", Toast.LENGTH_SHORT).show();
        }else{
            StampsCollection sc = new StampsCollection(userId, collectionName, isPrivate, collectionDescription);
            new InsertStampCollectionAsyncTask(stampsCollectionDao).execute(sc);
        }
    }

    public void cancelCollectionCreation(View v){
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //async task for adding and inserting a new collection to the DB
    public class InsertStampCollectionAsyncTask extends AsyncTask<StampsCollection, Void, Void> {
        StampsCollectionDao dao;

        InsertStampCollectionAsyncTask(StampsCollectionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(StampsCollection... stampsCollections) {
            dao.addStampCollection(stampsCollections[0]);
            DatabaseReference collectionsReference = fbDatabase.getReference("users/user - " + Integer.valueOf(stampsCollections[0].getUserId()) + "/collections");
            collectionsReference.child("collection - " + stampsCollections[0].getCollectionId()).setValue(stampsCollections[0]);
            collectionsReference.child("collection - " + stampsCollections[0].getCollectionId()).child("stamps").setValue("null");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Stamp collection was added to the DB!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            Toast.makeText(getApplicationContext(), "Nice! You created a new collection!", Toast.LENGTH_SHORT).show();
            finish();

        }
    }
}