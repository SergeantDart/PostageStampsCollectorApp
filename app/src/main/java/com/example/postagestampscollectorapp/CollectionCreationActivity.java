package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Database.Database;

public class CollectionCreationActivity extends AppCompatActivity {

    String collectionName;
    boolean isPrivate;
    String collectionDescription;

    EditText collectionNameEditText;
    EditText collectionDescriptionEditText;
    RadioGroup collectionAccesabilitRadioGroup;
    RadioButton choiceRadioButton;

    Database database;
    StampsCollectionDao stampsCollectionDao;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_creation);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();

        userId = getIntent().getIntExtra("userId", 0);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.9));
    }

    public class InsertStampCollectionAsyncTask extends AsyncTask<StampsCollection, Void, Void> {
        StampsCollectionDao dao;

        InsertStampCollectionAsyncTask(StampsCollectionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(StampsCollection... stampsCollections) {
            dao.addStampCollection(stampsCollections[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Stamp collection was added to the DB!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            // intent.putExtra("collection", sc);
            setResult(RESULT_OK, intent);
            Toast.makeText(getApplicationContext(), "Nice! You created a new collection!", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    public void createNewCollection(View v) {

        collectionNameEditText = (EditText) findViewById(R.id.colectionNameEditText);
        collectionDescriptionEditText = (EditText) findViewById(R.id.collectionDescriptionEditText);
        collectionAccesabilitRadioGroup = (RadioGroup) findViewById(R.id.collectionAccesabilityRadioGroup);

        collectionName = collectionNameEditText.getText().toString();
        collectionDescription = collectionDescriptionEditText.getText().toString();
        String choice = ((RadioButton) findViewById(collectionAccesabilitRadioGroup.getCheckedRadioButtonId())).getText().toString();

        if (choice == "Private") {
            isPrivate = true;
        } else if (choice == "Public") {
            isPrivate = false;
        }
        // else choice="";

        Log.i("inf", choice);

        //TRB SA LUCREZ AICI
        if (!collectionName.equals("") && !collectionDescription.equals("") && collectionAccesabilitRadioGroup.getCheckedRadioButtonId() != -1) {

            StampsCollection sc = new StampsCollection(collectionName, userId,null, isPrivate, collectionDescription);
            new InsertStampCollectionAsyncTask(stampsCollectionDao).execute(sc);



        } else if (collectionName.equals("")) {
            Toast.makeText(getApplicationContext(), "Add a name to your collection!", Toast.LENGTH_SHORT).show();
        } else if (collectionDescription.equals("")) {
            Toast.makeText(getApplicationContext(), "Add a description to your collection!", Toast.LENGTH_SHORT).show();
        } else if (collectionAccesabilitRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Check an option!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Incomplete data!", Toast.LENGTH_SHORT).show();

    }
}