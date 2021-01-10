package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Others.BitmapUtilities;
import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ViewStampActivity extends AppCompatActivity {

    ImageView currentStampImageView;
    EditText currentStampNameEditText;
    EditText currentStampYearEditText;
    Spinner currentStampCountrySpinner;
    EditText currentStampDescriptionEditText;
    Button currentStampModifyButton;
    Button currentStampDeleteButton;
    Button currentStampSaveButton;

    //SQLite database
    Database database;
    PostageStampDao postageStampDao;

    //selected postage stamp's id
    int postageStampId;
    //stamp's collection's id
    int collectionId;
    //current user's id
    int userId;

    //Firebase database
    FirebaseDatabase fbDatabase;

    //list that holds all the countries
    ArrayList<String> countries;
    ArrayAdapter<String> countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stamp);

        fbDatabase = FirebaseDatabase.getInstance();

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        postageStampDao = database.postageStampDao();

        //retrieve the intent sent parameters
        postageStampId = getIntent().getIntExtra("postageStampId", 0);
        collectionId = getIntent().getIntExtra("collectionId", 0);
        userId = getIntent().getIntExtra("userId", 0);

        currentStampImageView = (ImageView) findViewById(R.id.currentStampImageView);
        currentStampNameEditText = (EditText) findViewById(R.id.currentStampNameEditText);
        currentStampYearEditText = (EditText) findViewById(R.id.currentStampYearEditText);
        currentStampDescriptionEditText = (EditText) findViewById(R.id.currentStampDescriptionEditText);
        currentStampModifyButton = (Button) findViewById(R.id.currentStampModifyButton);
        currentStampDeleteButton = (Button) findViewById(R.id.currentStampDeleteButton);
        currentStampSaveButton = (Button) findViewById(R.id.currentStampSaveButton);
        currentStampCountrySpinner = (Spinner) findViewById(R.id.currentStampCountrySpinner);

        //populating the countries spinner with system available data
        Locale[] locales = Locale.getAvailableLocales();
        countries = new ArrayList<String>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);

        countryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.country_spinner_item, countries);
        countryAdapter.setDropDownViewResource(R.layout.country_spinner_item);

        currentStampCountrySpinner.setAdapter(countryAdapter);

        new GetPostageStampByIdAsyncTask(postageStampDao).execute(postageStampId);

        currentStampCountrySpinner.setEnabled(false);


    }

    //the modify button onclick function which changes its visibility when pressed and enables editing information
    public void modifyCurrentStamp(View v) {
        currentStampNameEditText.setEnabled(true);
        currentStampYearEditText.setEnabled(true);
        currentStampCountrySpinner.setEnabled(true);
        currentStampDescriptionEditText.setEnabled(true);
        currentStampModifyButton.setVisibility(View.INVISIBLE);
        currentStampSaveButton.setVisibility(View.VISIBLE);
    }

    //the save button onclick function which becomes visible after pressing the modify button
    public void saveCurrentStamp(View v) {
        new UpdatePostageStampByIdAsyncTask(postageStampDao).execute(postageStampId, currentStampNameEditText.getText().toString(), Integer.valueOf(currentStampYearEditText.getText().toString()),
                currentStampCountrySpinner.getSelectedItem().toString(), currentStampDescriptionEditText.getText().toString());
    }

    //the delete button onclick function
    public void deleteCurrentStamp(View v) {
        new AlertDialog.Builder(ViewStampActivity.this)
                .setTitle("WARNING")
                .setMessage("Are you sure you want to delete this stamp?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new DeletePostageStampByIdAsyncTask(postageStampDao).execute(postageStampId);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //on click function for canceling the activity
    public void cancelViewStamp(View v) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    //async task for getting the selected postage stamp's information from the DB
    public class GetPostageStampByIdAsyncTask extends AsyncTask<Integer, Void, PostageStamp> {
        PostageStampDao dao;

        GetPostageStampByIdAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected PostageStamp doInBackground(Integer... integers) {
            return dao.getPostageStampById(integers[0]);
        }

        @Override
        protected void onPostExecute(PostageStamp postageStamp) {
            super.onPostExecute(postageStamp);
            currentStampImageView.setImageBitmap(BitmapUtilities.getBitmap(postageStamp.getPicBytes()));
            currentStampNameEditText.setText(postageStamp.getName());
            currentStampYearEditText.setText(String.valueOf(postageStamp.getYear()));
            currentStampDescriptionEditText.setText(postageStamp.getDescription());
            for (String country : countries) {
                if (country.equals(postageStamp.getCountry())) {
                    int spinnerPosition = countryAdapter.getPosition(country);
                    currentStampCountrySpinner.setSelection(spinnerPosition);
                }
            }
        }
    }

    //async task for updating the selected postage stamp's information to the DB
    public class UpdatePostageStampByIdAsyncTask extends AsyncTask<Object, Void, Void> {
        PostageStampDao dao;

        UpdatePostageStampByIdAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            //SQLite DB implementation
            if (currentStampNameEditText.getText().toString().equals("") ||
                    currentStampYearEditText.getText().toString().length() != 4 ||
                    currentStampYearEditText.getText().toString().equals("") ||
                    currentStampDescriptionEditText.getText().toString().equals("") ||
                    currentStampDescriptionEditText.getText().toString().length() < 10) {
                //nothing
            } else {
                dao.updateStampById((int) objects[0], (String) objects[1], (int) objects[2], (String) objects[3], (String) objects[4]);
                //Firebase DB implementation
                DatabaseReference chosenStampReference = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + Integer.valueOf(collectionId) + "/stamps/stamp - " + Integer.valueOf(postageStampId));
                chosenStampReference.child("name").setValue((String) objects[1]);
                chosenStampReference.child("year").setValue((int) objects[2]);
                chosenStampReference.child("country").setValue((String) objects[3]);
                chosenStampReference.child("description").setValue((String) objects[4]);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (currentStampNameEditText.getText().equals("")) {
                Toast.makeText(getApplicationContext(), "Give the stamp a name !", Toast.LENGTH_SHORT).show();
            } else if (currentStampYearEditText.getText().toString().equals("") || currentStampYearEditText.getText().toString().length() != 4) {
                Toast.makeText(getApplicationContext(), "Give a valid year !", Toast.LENGTH_SHORT).show();
            } else if (currentStampDescriptionEditText.getText().toString().equals("") || currentStampDescriptionEditText.getText().toString().length() < 10) {
                Toast.makeText(getApplicationContext(), "The stamp description must be at least 10 characters long !", Toast.LENGTH_SHORT).show();
            } else {
                currentStampNameEditText.setEnabled(false);
                currentStampYearEditText.setEnabled(false);
                currentStampCountrySpinner.setEnabled(false);
                currentStampDescriptionEditText.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Postage stamp succesfully updated in the DB !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    //async task for deleting the selected postage stamp from the DB
    public class DeletePostageStampByIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        PostageStampDao dao;

        DeletePostageStampByIdAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            //SQLite DB implementation
            dao.deleteStampById(integers[0]);
            //Firebase DB implementation
            DatabaseReference chosenStampReference = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + Integer.valueOf(collectionId) + "/stamps/stamp - " + Integer.valueOf(postageStampId));
            chosenStampReference.removeValue();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Postage stamps was deleted from the DB...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
