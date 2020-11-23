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
    static int ok=0;

    ArrayList<PostageStamp> stampsList;
    Spinner stampCountrySpinner;
    EditText stampNameEditText;
    EditText stampYearEditText;
    EditText stampDescriptionEditText;
    ImageView choiceImageView;

    Uri imageUri;

    String name ;
    String picUri ;
    int year;
    String country ;
    String description;
    Bitmap stampPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stamp_registration);
        stampNameEditText = (EditText) findViewById(R.id.stampNameEditText);
        stampYearEditText = (EditText) findViewById(R.id.stampYearEditText);
        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);
        stampDescriptionEditText = (EditText) findViewById(R.id.stampDescriptionEditText);
        choiceImageView = (ImageView) findViewById(R.id.choiceImageView);

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

        if(ok==1 && !stampNameEditText.getText().toString().equals("") && !stampYearEditText.getText().toString().matches("") && !stampCountrySpinner.getSelectedItem().toString().equals("") && !stampDescriptionEditText.getText().toString().equals("") ){
            name = stampNameEditText.getText().toString();
            year = Integer.parseInt(stampYearEditText.getText().toString());
            country = stampCountrySpinner.getSelectedItem().toString();
            description = stampDescriptionEditText.getText().toString();
            ok=0;


            Toast.makeText(getApplicationContext(), "Nice! Your stamp was added to the collection!", Toast.LENGTH_SHORT).show();
            PostageStamp ps = new PostageStamp(name,stampPic,year,country,description);
            Intent intent  =new Intent();
            intent.putExtra("stamp",ps);
            setResult(RESULT_OK, intent);
            finish();
        }

            else if(stampNameEditText.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Add a name to your stamp!", Toast.LENGTH_SHORT).show();
            }
           else if(stampYearEditText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Add a year to your stamp!", Toast.LENGTH_SHORT).show();
            }
           else if(stampDescriptionEditText.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(), "Add a description your stamp!", Toast.LENGTH_SHORT).show();
            }
           else if(ok==0){
                Toast.makeText(getApplicationContext(), "Add a picture of your stamp!", Toast.LENGTH_SHORT).show();
        }
           else {
               Toast.makeText(getApplicationContext(), "Incomplete data!", Toast.LENGTH_SHORT).show();
        }


    }
}