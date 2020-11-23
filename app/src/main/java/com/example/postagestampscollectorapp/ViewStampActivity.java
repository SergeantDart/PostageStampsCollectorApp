package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewStampActivity extends AppCompatActivity {

    ImageView currentStampImageView;
    EditText currentStampNameEditText;
    EditText currentStampYearEditText;
    EditText currentStampCountryEditText;
    EditText currentStampDescriptionEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stamp);
        PostageStamp ps = getIntent().getParcelableExtra("myStamp");

        currentStampImageView = (ImageView)findViewById(R.id.currentStampImageView);
        currentStampNameEditText=(EditText)findViewById(R.id.currentStampNameEditText);
        currentStampYearEditText=(EditText)findViewById(R.id.currentStampYearEditText);
        currentStampCountryEditText=(EditText)findViewById(R.id.currentStampCountryEditText);
        currentStampDescriptionEditText=(EditText)findViewById(R.id.currentStampDescriptionEditText);

        currentStampImageView.setImageBitmap(ps.pic);
        currentStampNameEditText.setText(ps.name);
        currentStampYearEditText.setText(String.valueOf(ps.year));
        currentStampCountryEditText.setText(ps.country);
        currentStampDescriptionEditText.setText(ps.description);
    }

    public void modifyCurrentStamp(View v){

    }

    public void deleteCurrentStamp(View v){

    }
}