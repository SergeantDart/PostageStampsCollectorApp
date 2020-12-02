package com.example.postagestampscollectorapp;


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

import com.example.postagestampscollectorapp.Database.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StampRegistrationActivity extends AppCompatActivity {

    final static int ACCESS_GALLERY = 1;
    static int ok = 0;

    ArrayList<PostageStamp> stampsList;
    Spinner stampCountrySpinner;
    EditText stampNameEditText;
    EditText stampYearEditText;
    EditText stampDescriptionEditText;
    ImageView choiceImageView;

    Uri imageUri;

    String name;
    String picUri;
    int year;
    String country;
    String description;
    Bitmap stampPic;

    Database database;
    PostageStampDao postageStampDao;

    int collectionId;

    // BitmapUtilities bitmapUtilities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_registration);
        stampNameEditText = (EditText) findViewById(R.id.stampNameEditText);
        stampYearEditText = (EditText) findViewById(R.id.stampYearEditText);
        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampDescriptionEditText = (EditText) findViewById(R.id.stampDescriptionEditText);
        choiceImageView = (ImageView) findViewById(R.id.choiceImageView);

        collectionId = getIntent().getIntExtra("collectionId", 0);

        Log.i("collectionId",String.valueOf(collectionId));

        // bitmapUtilities = new BitmapUtilities();
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        postageStampDao = database.postageStampDao();

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

    protected class GetAllPostageStampsAsyncTask extends AsyncTask<Void, Void, List<PostageStamp>> {
        PostageStampDao dao;

        public GetAllPostageStampsAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<PostageStamp> doInBackground(Void... voids) {
            return dao.getAllPostageStamps();
        }

        @Override
        protected void onPostExecute(List<PostageStamp> postageStamps) {
            super.onPostExecute(postageStamps);
            for (PostageStamp ps : postageStamps) {
                Log.i("stamp", "stampName: " + ps.name + ", stampId: " + ps.stampId + " ,collectionId: " + ps.collectionId + " ,country: " + ps.country);
            }
        }
    }

    protected class InsertPostageStampAsyncTask extends AsyncTask<PostageStamp, Void, Void> {
        PostageStampDao dao;

        public InsertPostageStampAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(PostageStamp... postageStamps) {
            dao.addPostageStamp(postageStamps[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Postage stamp added succesfully to the DB!", Toast.LENGTH_SHORT).show();
            new GetAllPostageStampsAsyncTask(dao).execute();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select picture"), ACCESS_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCESS_GALLERY && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            try {

                stampPic = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //Toast.makeText(this, String.valueOf(imageUri), Toast.LENGTH_SHORT).show();
                choiceImageView.setImageBitmap(stampPic);
                picUri = imageUri.toString();
                ok++;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

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
}