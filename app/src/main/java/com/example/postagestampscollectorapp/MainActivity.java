package com.example.postagestampscollectorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner stampCountrySpinner;
    ListView stampsListView;
    ArrayList<PostageStamp> stampsList = new ArrayList<PostageStamp>();
    StampAdapter stampAdapter;
    SQLiteDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabase = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS PostageStamps(id NUMBER,name VARCHAR, pic BLOB, year NUMBER, country NUMBER, description VARCHAR)");


        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);

        PostageStamp ps1 = new PostageStamp("John Lewis", bm1, 1919, "USA", "Baseball star");
        PostageStamp ps2 = new PostageStamp("Dam Bridge", bm2,2001, "USA", "famous US Landmarks");

        stampsList.add(ps1);
        stampsList.add(ps2);
        stampsListView = (ListView) findViewById(R.id.stampsListView);
        stampAdapter = new StampAdapter(getApplicationContext(), stampsList);
        stampsListView.setAdapter(stampAdapter);

    }


    public void openCollectionCreationActivity(View v){
        Intent intent = new Intent(getApplicationContext(), CollectionCreationActivity.class);
        startActivityForResult(intent, 0);
    }

    public void openStampRegistrationActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            PostageStamp ps = data.getParcelableExtra("stamp");
            stampsList.add(ps);
            Toast.makeText(this, ps.toString(), Toast.LENGTH_LONG).show();
            stampAdapter.notifyDataSetChanged();

        }
    }
}