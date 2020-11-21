package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class StampRegistrationActivity extends AppCompatActivity {

    final static int ACCESS_GALLERY = 1;
    Spinner stampCountrySpinner;
    EditText stampNameEditText;
    EditText stampYearEditText;
    EditText stampDescriptionEditText;
    ImageView stampImageView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stamp_registration);
        stampNameEditText = (EditText) findViewById(R.id.stampNameEditText);
        stampYearEditText = (EditText) findViewById(R.id.stampYearEditText);
        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampDescriptionEditText = (EditText) findViewById(R.id.stampDescriptionEditText);
        stampImageView = (ImageView) findViewById(R.id.stampImageView);


        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.country_spinner_item, countries);

        countryAdapter.setDropDownViewResource(R.layout.country_spinner_item);

        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);

        stampCountrySpinner.setAdapter(countryAdapter);
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

                Bitmap stampPic = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Toast.makeText(this, String.valueOf(imageUri), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void addStamp(View v) {


    }
}