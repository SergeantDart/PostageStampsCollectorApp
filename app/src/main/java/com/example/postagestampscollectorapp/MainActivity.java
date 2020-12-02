package com.example.postagestampscollectorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import com.example.postagestampscollectorapp.Database.Database;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final int REQUST_COLLECTION_CREATOR_ACTIVITY = 0;
    final int REQUEST_STAMP_REGISTRATION_ACTIVITY = 1;
    final int REQUEST_VIEW_STAMP_ACTIVITY = 2;
    Spinner collectionsSpinner;
    ListView stampsListView;

    //List<PostageStamp> stampsList1 = new ArrayList<PostageStamp>();
    //List<PostageStamp> stampsList2 = new ArrayList<PostageStamp>();

    List<StampsCollection> collectionsList = new ArrayList<StampsCollection>();
    StampsCollection chosenCollection;

    List<String> collectionsNames;
    List<Integer> collectionsIds;

    StampAdapter stampAdapter;
    ArrayAdapter<String> collectionsNamesAdapter;

    int currentUser;

    Database database;
    StampsCollectionDao stampsCollectionDao;
    PostageStampDao postageStampDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();
        postageStampDao = database.postageStampDao();

        currentUser = getIntent().getIntExtra("userId", 0);

        collectionsSpinner = (Spinner) findViewById(R.id.collectionSpinner);
        collectionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int collectionId=collectionsIds.get(position);
                new GetChosenStampCollectionAsyncTask(stampsCollectionDao).execute(collectionId, currentUser);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //your code here
            }

        });

        //Bitmap bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);
        //Bitmap bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.star_stamp);

        stampsListView = (ListView) findViewById(R.id.stampsListView);

        new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUser);

        //resetCollectionsNamesAdapter();

        //StampsCollection sc1 = new StampsCollection("myCollection", currentUser, stampsList1, true, "my collection is beautiful");
        //StampsCollection sc2 = new StampsCollection("myPreviousCollection", currentUser, stampsList2, false, "this is for testing purposes only");

        //collectionsList.add(sc1);
        //collectionsList.add(sc2);

        //chosenCollection = new StampsCollection(currentUser);

        /*PostageStamp ps1 = new PostageStamp(chosenCollection.collectionId, "John Lewis", BitmapUtilities.getBytes(bm1), 1919, "USA", "Baseball star");
        PostageStamp ps2 = new PostageStamp(chosenCollection.collectionId, "Dam Bridge", BitmapUtilities.getBytes(bm1), 2001, "USA", "famous US Landmarks");
        PostageStamp ps3 = new PostageStamp(chosenCollection.collectionId, "Zulu", BitmapUtilities.getBytes(bm1), 1991, "Australia", "a nice one");
        PostageStamp ps4 = new PostageStamp(chosenCollection.collectionId, "Margarets Flowers", BitmapUtilities.getBytes(bm2), 2002, "Romania", "my favourite one");
        PostageStamp ps5 = new PostageStamp(chosenCollection.collectionId, "Hell's Riders", BitmapUtilities.getBytes(bm2), 2010, "New Zeeland", "motorcycle club, very dangerous");

        stampsList1.add(ps1);
        stampsList1.add(ps2);
        stampsList1.add(ps3);
        stampsList2.add(ps4);
        stampsList2.add(ps5);*/


        resetCollectionsNamesAdapter();
        //resetStampAdapter();

        stampsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewStampActivity.class);
                int postageStampId = chosenCollection.stampsList.get(position).stampId;
                intent.putExtra("postageStampId", postageStampId);
                startActivityForResult(intent, REQUEST_VIEW_STAMP_ACTIVITY);
            }
        });

    }

    public class GetWantedStampsAsyncTask extends AsyncTask<Integer, Void, List<PostageStamp>> {
        PostageStampDao dao;

        GetWantedStampsAsyncTask(PostageStampDao dao) {
            this.dao = dao;
        }

        @Override
        protected List<PostageStamp> doInBackground(Integer... integers) {
            return dao.getCertainCollectionStamps(integers[0]);
        }

        @Override
        protected void onPostExecute(List<PostageStamp> postageStamps) {
            super.onPostExecute(postageStamps);
            chosenCollection.stampsList = postageStamps;
            resetStampAdapter();

        }
    }

    public class GetChosenStampCollectionAsyncTask extends AsyncTask<Integer, Void, StampsCollection> {

        StampsCollectionDao dao;

        GetChosenStampCollectionAsyncTask(StampsCollectionDao dao) {
            this.dao = dao;
        }

        @Override
        protected StampsCollection doInBackground(Integer... integers) {
            return dao.getChosenStampCollection(integers[0], integers[1]);
        }

        @Override
        protected void onPostExecute(StampsCollection stampsCollection) {
            super.onPostExecute(stampsCollection);
            chosenCollection = stampsCollection;
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.collectionId);
            //Toast.makeText(getApplicationContext(), chosenCollection.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public class GetAllUserStampCollectionsAsyncTask extends AsyncTask<Integer, Void, List<StampsCollection>>{
        StampsCollectionDao dao;

        GetAllUserStampCollectionsAsyncTask(StampsCollectionDao dao){
            this.dao=dao;
        }

        @Override
        protected List<StampsCollection> doInBackground(Integer... integers) {
            return dao.getAllUserStampCollections(integers[0]);
        }

        @Override
        protected void onPostExecute(List<StampsCollection> stampsCollectionList) {
            super.onPostExecute(stampsCollectionList);
            collectionsList = stampsCollectionList;
            resetCollectionsNamesAdapter();
            for(StampsCollection sc : stampsCollectionList){
                Log.i("stampCollection", "collectionId: "+sc.collectionId +" ,collectionName: "+sc.collectionName + " ,userId: " + sc.userId + " ,description: " + sc.collectionDescription);
            }
        }
    }

    public void resetCollectionsNamesAdapter() {
        collectionsNames = new ArrayList<>();
        collectionsIds = new ArrayList<>();

        for (StampsCollection stampsCollection : collectionsList) {
            collectionsNames.add(stampsCollection.collectionName);
            collectionsIds.add(stampsCollection.collectionId);
            //Toast.makeText(this, stampsCollection.collectionName, Toast.LENGTH_SHORT).show();
        }

        collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, collectionsNames);
        collectionsSpinner.setAdapter(collectionsNamesAdapter);

    }


    public void resetStampAdapter() {
            stampAdapter = new StampAdapter(getApplicationContext(), chosenCollection.stampsList);
            stampsListView.setAdapter(stampAdapter);
            stampAdapter.notifyDataSetChanged();

    }


    public void openCollectionCreationActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), CollectionCreationActivity.class);
        intent.putExtra("userId", currentUser);
        startActivityForResult(intent, REQUST_COLLECTION_CREATOR_ACTIVITY);
    }

    public void openStampRegistrationActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
        intent.putExtra("collectionId", chosenCollection.collectionId);
        startActivityForResult(intent, REQUEST_STAMP_REGISTRATION_ACTIVITY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_STAMP_REGISTRATION_ACTIVITY) {
            //PostageStamp ps = data.getParcelableExtra("stamp");
            //chosenCollection.stampsList.add(ps);
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.collectionId);
        } else if (resultCode == RESULT_OK && requestCode == REQUST_COLLECTION_CREATOR_ACTIVITY) {
            // StampsCollection collection = data.getParcelableExtra("collection");
            new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUser);
        }else if(resultCode == RESULT_OK && requestCode == REQUEST_VIEW_STAMP_ACTIVITY){
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.collectionId);
        }
    }
}