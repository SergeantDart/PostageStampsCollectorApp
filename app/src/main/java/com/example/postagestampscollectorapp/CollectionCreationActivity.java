package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

        collectionNameEditText = (EditText) findViewById(R.id.colectionNameEditText);
        collectionDescriptionEditText = (EditText) findViewById(R.id.colectionNameEditText);
        collectionAccesabilitRadioGroup = (RadioGroup) findViewById(R.id.accesabilityRadioGroup);

        collectionName = collectionNameEditText.getText().toString();
        collectionDescription = collectionDescriptionEditText.getText().toString();
        String choice = ((RadioButton) findViewById(collectionAccesabilitRadioGroup.getCheckedRadioButtonId())).getText().toString();

        if (choice == "Private") {
            isPrivate = true;
        } else if (choice == "Public") {
            isPrivate = false;
        }

        Log.i("info",choice);


        //TRB SA LUCREZ AICI
            if( !collectionName.equals("") && !collectionDescription.equals("") && !choice.equals("") ) {
                StampsCollection sc = new StampsCollection(collectionName, null, isPrivate, collectionDescription);
                Intent intent = new Intent();
                intent.putExtra("collection", sc);
                setResult(RESULT_OK, intent);

                finish();
                Toast.makeText(getApplicationContext(), "Nice! You created a new collection!", Toast.LENGTH_SHORT).show();
            }
            else if( collectionName.equals("") ){
                Toast.makeText(getApplicationContext(), "Add a name to your collection!", Toast.LENGTH_SHORT).show();
            }
           // else if(collectionDescription.equals("")){
            //    Toast.makeText(getApplicationContext(), "Add a description to your collection!", Toast.LENGTH_SHORT).show();
           // }
            else if( choice.equals("") ){
                Log.i("info",choice);
                Toast.makeText(getApplicationContext(), "Check an option!", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(), "Incomplete data!", Toast.LENGTH_SHORT).show();

    }
}