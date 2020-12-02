package com.example.postagestampscollectorapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.postagestampscollectorapp.Others.BitmapUtilities;
import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.R;

public class ViewStampActivity extends AppCompatActivity {

    ImageView currentStampImageView;
    EditText currentStampNameEditText;
    EditText currentStampYearEditText;
    EditText currentStampCountryEditText;
    EditText currentStampDescriptionEditText;
    Button currentStampModifyButton;
    Button currentStampDeleteButton;
    Button currentStampSaveButton;
    Database database;
    PostageStampDao postageStampDao;
    int postageStampId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stamp);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        postageStampDao = database.postageStampDao();
        //PostageStamp ps = getIntent().getParcelableExtra("myStamp");
        postageStampId = getIntent().getIntExtra("postageStampId", 0);

        currentStampImageView = (ImageView) findViewById(R.id.currentStampImageView);
        currentStampNameEditText = (EditText) findViewById(R.id.currentStampNameEditText);
        currentStampYearEditText = (EditText) findViewById(R.id.currentStampYearEditText);
        currentStampCountryEditText = (EditText) findViewById(R.id.currentStampCountryEditText);
        currentStampDescriptionEditText = (EditText) findViewById(R.id.currentStampDescriptionEditText);
        currentStampModifyButton = (Button) findViewById(R.id.currentStampModifyButton);
        currentStampDeleteButton = (Button) findViewById(R.id.currentStampDeleteButton);
        currentStampSaveButton = (Button)findViewById(R.id.currentStampSaveButton);

        new GetPostageStampByIdAsyncTask(postageStampDao).execute(postageStampId);


    }

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
            currentStampCountryEditText.setText(postageStamp.getCountry());
            currentStampDescriptionEditText.setText(postageStamp.getDescription());
        }
    }

    public class UpdatePostageStampByIdAsyncTask extends AsyncTask<Object, Void, Void> {
        PostageStampDao dao;

        UpdatePostageStampByIdAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Object... objects) {
            dao.updateStampById((int) objects[0], (String) objects[1], (int) objects[2], (String) objects[3], (String) objects[4]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            currentStampNameEditText.setEnabled(false);
            currentStampYearEditText.setEnabled(false);
            currentStampCountryEditText.setEnabled(false);
            currentStampDescriptionEditText.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Postage stamp succesfully updated in the DB !", Toast.LENGTH_SHORT).show();
            currentStampModifyButton.setText("MODIFY");
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    public class DeletePostageStampByIdAsyncTask extends AsyncTask<Integer, Void, Void>{
        PostageStampDao dao;
        DeletePostageStampByIdAsyncTask(PostageStampDao dao){
            this.dao=dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            dao.deleteStampById(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Postage stamps was deleted from the DB...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    public void modifyCurrentStamp(View v) {
        currentStampNameEditText.setEnabled(true);
        currentStampYearEditText.setEnabled(true);
        currentStampCountryEditText.setEnabled(true);
        currentStampDescriptionEditText.setEnabled(true);
        currentStampModifyButton.setVisibility(View.INVISIBLE);
        currentStampSaveButton.setVisibility(View.VISIBLE);


    }

    public void saveCurrentStamp(View v){
        new UpdatePostageStampByIdAsyncTask(postageStampDao).execute(postageStampId, currentStampNameEditText.getText().toString(), Integer.valueOf(currentStampYearEditText.getText().toString()),
                currentStampCountryEditText.getText().toString(), currentStampDescriptionEditText.getText().toString());
    }

    public void deleteCurrentStamp(View v) {
        new DeletePostageStampByIdAsyncTask(postageStampDao).execute(postageStampId);
    }
}