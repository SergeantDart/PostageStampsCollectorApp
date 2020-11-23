package com.example.postagestampscollectorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner collectionsSpinner;
    ListView stampsListView;

    ArrayList<PostageStamp> stampsList1 = new ArrayList<PostageStamp>();
    ArrayList<PostageStamp> stampsList2 = new ArrayList<PostageStamp>();

    ArrayList<StampsCollection> collectionsList = new ArrayList<StampsCollection>();
    StampsCollection chosenCollection;
    ArrayList<String> collectionsNames;

    StampAdapter stampAdapter;
    ArrayAdapter<String> collectionsNamesAdapter;

    SQLiteDatabase myDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabase = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS PostageStamps(id NUMBER,name VARCHAR, pic BLOB, year NUMBER, country NUMBER, description VARCHAR)");

        collectionsSpinner = (Spinner) findViewById(R.id.collectionSpinner);

        collectionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                for(StampsCollection collection : collectionsList){
                    if(collection.collectionName==collectionsSpinner.getSelectedItem().toString()){
                        chosenCollection=collection;
                        resetStampAdapter();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
               //your code here
            }

        });

        Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);
        Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);

        PostageStamp ps1 = new PostageStamp("John Lewis", bm1, 1919, "USA", "Baseball star");
        PostageStamp ps2 = new PostageStamp("Dam Bridge", bm2, 2001, "USA", "famous US Landmarks");
        PostageStamp ps3 = new PostageStamp("Zulu",bm1,1991,"Australia","a nice one");
        PostageStamp ps4 = new PostageStamp("Margarets Flowers", bm1, 2002, "Romania" , "my favourite one");
        PostageStamp ps5 = new PostageStamp("Hell's Riders",bm1,2010,"New Zeeland", "motorcycle club, very dangerous");

        stampsList1.add(ps1);
        stampsList1.add(ps2);
        stampsList1.add(ps3);
        stampsList2.add(ps4);
        stampsList2.add(ps5);

        StampsCollection sc1 = new StampsCollection("myCollection", stampsList1, true, "my collection is beautiful");
        StampsCollection sc2 = new StampsCollection("myPreviousCollection", stampsList2,false,"this is for testing purposes only");

        collectionsList.add(sc1);
        collectionsList.add(sc2);


        stampsListView = (ListView) findViewById(R.id.stampsListView);
        chosenCollection = sc1;


        resetCollectionsNamesAdapter();
        resetStampAdapter();

//        stampsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getApplicationContext(),ViewStampByClickingActivity.class);
//                startActivity(intent);
//            }
//        });


    }

    public void resetCollectionsNamesAdapter(){
        collectionsNames = new ArrayList<String>();

        for (StampsCollection stampsCollection : collectionsList) {
            collectionsNames.add(stampsCollection.collectionName);
            Toast.makeText(this, stampsCollection.collectionName, Toast.LENGTH_SHORT).show();
        }

        collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, collectionsNames);
        collectionsSpinner.setAdapter(collectionsNamesAdapter);

    }
    public void resetStampAdapter() {
        stampAdapter = new StampAdapter(getApplicationContext(), chosenCollection.stampsList);
        stampsListView.setAdapter(stampAdapter);

    }


    public void openCollectionCreationActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), CollectionCreationActivity.class);
        startActivityForResult(intent, 1);
    }

    public void openStampRegistrationActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
        startActivityForResult(intent, 0);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode,  @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            PostageStamp ps = data.getParcelableExtra("stamp");
            chosenCollection.stampsList.add(ps);
            Toast.makeText(this, ps.toString(), Toast.LENGTH_LONG).show();
            stampAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_OK && requestCode == 1) {
            StampsCollection collection = data.getParcelableExtra("collection");
            collectionsList.add(collection);
            resetCollectionsNamesAdapter();
            Toast.makeText(this, collection.toString(), Toast.LENGTH_LONG).show();
        }
    }
}