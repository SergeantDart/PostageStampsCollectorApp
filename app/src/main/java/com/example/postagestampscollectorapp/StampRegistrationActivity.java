package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class StampRegistrationActivity extends AppCompatActivity {

    Spinner stampCountrySpinner;
    EditText stampNameEditText;
    EditText stampYearEditText;
    EditText stampDescriptionEditText;
    ImageView stampImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp_registration);
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

    public void selectImage (View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    public void addStamp(View v) {
        stampNameEditText = (EditText) findViewById(R.id.stampNameEditText);
        stampYearEditText = (EditText) findViewById(R.id.stampYearEditText);
        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampDescriptionEditText = (EditText) findViewById(R.id.stampDescriptionEditText);
       // stampImageView = (ImageView) findViewById(R.id.stampImageView);

    }
}