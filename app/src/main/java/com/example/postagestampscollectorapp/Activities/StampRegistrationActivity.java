package com.example.postagestampscollectorapp.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Others.BitmapUtilities;
import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.grpc.Context;

public class StampRegistrationActivity extends AppCompatActivity {

    final static int ACCESS_GALLERY = 1;
    static int ok = 0;



    Spinner stampCountrySpinner;
    EditText stampNameEditText;
    EditText stampYearEditText;
    EditText stampDescriptionEditText;
    ImageView choiceImageView;

    //internal storage memory adress of a file
    Uri imageUri;

    String name;
    String picUri;
    int year;
    String country;
    String description;
    Bitmap stampPic;

    //Firebase database
    FirebaseDatabase fbDatabase;

    //SQLite database
    Database database;
    PostageStampDao postageStampDao;

    //currently selected collection's id
    int collectionId;

    //current user's id
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_registration);

        fbDatabase = FirebaseDatabase.getInstance();

        stampNameEditText = (EditText) findViewById(R.id.stampNameEditText);
        stampYearEditText = (EditText) findViewById(R.id.stampYearEditText);
        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampDescriptionEditText = (EditText) findViewById(R.id.stampDescriptionEditText);
        choiceImageView = (ImageView) findViewById(R.id.choiceImageView);

        userId = getIntent().getIntExtra("userId",0);
        collectionId = getIntent().getIntExtra("collectionId", 0);


        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        postageStampDao = database.postageStampDao();

        //populating the countries spinner with system available data
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.country_spinner_item, countries);
        countryAdapter.setDropDownViewResource(R.layout.country_spinner_item);

        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampCountrySpinner.setAdapter(countryAdapter);
    }

    //the select image button onclick function for allowing the user to select an image from the device memory
    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), ACCESS_GALLERY);
    }

    //the onactivityresult function for after the user selects a photo from the device's internal storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {

                stampPic = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                choiceImageView.setImageBitmap(stampPic);
                picUri = imageUri.toString();
                ok++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //the add postage stamp button onclick function
    //the picture is compressed to JPEG format and stored in the DB inside a byte array
    //when selected from the DB, the picture is decompressed and displayed to its original form
    //the compression/decompression operations are performed with the helper class BitmapUtilites
    //for speed purposes the compression quality is set to a low value ( 10% )
    public void addStamp(View v) {

        if (ok == 1 && !stampNameEditText.getText().toString().equals("") && !stampYearEditText.getText().toString().matches("") && !stampCountrySpinner.getSelectedItem().toString().equals("") && !stampDescriptionEditText.getText().toString().equals("")) {
            name = stampNameEditText.getText().toString();
            year = Integer.parseInt(stampYearEditText.getText().toString());
            country = stampCountrySpinner.getSelectedItem().toString();
            description = stampDescriptionEditText.getText().toString();
            ok = 0;

            byte[] picBytes = BitmapUtilities.getBytes(stampPic);
            Toast.makeText(getApplicationContext(), "Nice! Your stamp was added to the collection!", Toast.LENGTH_SHORT).show();
            PostageStamp ps = new PostageStamp(collectionId, name, picBytes, year, country, description);
            new InsertPostageStampAsyncTask(postageStampDao).execute(ps);
        } else if (stampNameEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Add a name to your stamp!", Toast.LENGTH_SHORT).show();
        } else if (stampYearEditText.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Add a year to your stamp!", Toast.LENGTH_SHORT).show();
        } else if (stampDescriptionEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Add a description your stamp!", Toast.LENGTH_SHORT).show();
        } else if (ok == 0) {
            Toast.makeText(getApplicationContext(), "Add a picture of your stamp!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Incomplete data!", Toast.LENGTH_SHORT).show();
        }
    }

    //async task for adding and inserting a new postage stamp to the DB (the stamp belonging to the currently selected collection)
    protected class InsertPostageStampAsyncTask extends AsyncTask<PostageStamp, Void, Void> {
        PostageStampDao dao;

        public InsertPostageStampAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PostageStamp... postageStamps) {
            dao.addPostageStamp(postageStamps[0]);

            //adding a postage stamp information to Firebase DB, item by item ( because we cannot store a byte array / bitmap / image in Firebase DB
            DatabaseReference collectionsReference = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + postageStamps[0].getCollectionId() + "/stamps");
            collectionsReference.child("stamp - " + postageStamps[0].getStampId());
            DatabaseReference stampReference = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + postageStamps[0].getCollectionId() + "/stamps/stamp - " + postageStamps[0].getStampId());
            stampReference.child("stampId").setValue(postageStamps[0].getStampId());
            stampReference.child("collectionId").setValue(postageStamps[0].getCollectionId());
            stampReference.child("name").setValue(postageStamps[0].getName());
            stampReference.child("year").setValue(postageStamps[0].getYear());
            stampReference.child("country").setValue(postageStamps[0].getCountry());
            stampReference.child("description").setValue(postageStamps[0].getDescription());

            //the stamp picture is converted to byte array and stored in Firebase Storage, being referenced in the Firebase DB by its URI (path)
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference("stamps").child("stampImage - " + Integer.valueOf(postageStamps[0].getStampId()));
            storageReference.putBytes(postageStamps[0].getPicBytes()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
            stampReference.child("stampPicUri").setValue("stamps/stampImage - " + Integer.valueOf(postageStamps[0].getStampId()));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Postage stamp added succesfully to the DB!", Toast.LENGTH_SHORT).show();
            //new GetAllPostageStampsAsyncTask(dao).execute();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}