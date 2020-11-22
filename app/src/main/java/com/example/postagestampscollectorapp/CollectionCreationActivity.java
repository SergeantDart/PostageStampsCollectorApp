package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CollectionCreationActivity extends AppCompatActivity {

    String collectionName;
    boolean isPrivate;
    String collectionDescription;

    EditText collectionNameEditText;
    EditText collectionDescriptionEditText;
    RadioGroup collectionAccesabilitRadioGroup;
    RadioButton choiceRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_creation);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.9));
    }

    public void createNewCollection(View v){
        collectionNameEditText = (EditText)findViewById(R.id.colectionNameEditText);
        collectionDescriptionEditText=(EditText)findViewById(R.id.colectionNameEditText);
        collectionAccesabilitRadioGroup=(RadioGroup)findViewById(R.id.accesabilityRadioGroup);

        collectionName = collectionNameEditText.getText().toString();
        collectionDescription = collectionDescriptionEditText.getText().toString();
        String choice = ((RadioButton)findViewById(collectionAccesabilitRadioGroup.getCheckedRadioButtonId())).getText().toString();
        if(choice=="Private"){
            isPrivate=true;
        }
        else if(choice=="Public"){
            isPrivate=false;
        }

        StampsCollection sc = new StampsCollection(collectionName,null,isPrivate,collectionDescription);
        Intent intent  =new Intent();
        intent.putExtra("collection",sc);
        setResult(RESULT_OK, intent);

        finish();


    }
}