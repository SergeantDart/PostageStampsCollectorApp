package com.example.postagestampscollectorapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.room.Room;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.PostageStampDao;
import com.example.postagestampscollectorapp.R;
import com.example.postagestampscollectorapp.Others.StampAdapter;
import com.example.postagestampscollectorapp.Database.StampsCollectionDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //request codes
    final int REQUEST_COLLECTION_CREATOR_ACTIVITY = 0;
    final int REQUEST_STAMP_REGISTRATION_ACTIVITY = 1;
    final int REQUEST_VIEW_STAMP_ACTIVITY = 2;

    //hash map to store key-value pair => country-stamp frequence and its iterator
    HashMap<String, Float> byCountryFrequenciesMap;
    Iterator<Map.Entry<String, Float>> iterator;


    Spinner collectionsSpinner;
    ListView stampsListView;
    ImageView dropdownArrowImageView;

    //current user's collections
    List<StampsCollection> collectionsList = new ArrayList<StampsCollection>();

    //currently selected collection from the spinner
    StampsCollection chosenCollection;

    //lists for storing names and ids of the user's collections in the same order ( the lists' indexes point to same object )
    List<String> collectionsNames;
    List<Integer> collectionsIds;

    StampAdapter stampAdapter;
    ArrayAdapter<String> collectionsNamesAdapter;

    //current user's id
    int currentUserId;

    //SQLite database
    Database database;
    StampsCollectionDao stampsCollectionDao;
    PostageStampDao postageStampDao;

    //Firebase database
    FirebaseDatabase fbDatabase;

    //holds the selected collection index on both names and ids lists
    int currentCollectionSpinnerIndex;

    int totalStampsNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbDatabase = FirebaseDatabase.getInstance();

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();
        postageStampDao = database.postageStampDao();

        currentUserId = getIntent().getIntExtra("userId", 0);

        //creating and populating the spinner with all the user's collection names
        collectionsSpinner = (Spinner) findViewById(R.id.collectionSpinner);
        collectionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                currentCollectionSpinnerIndex = position;
                int collectionId=collectionsIds.get(position);
                new GetChosenStampCollectionAsyncTask(stampsCollectionDao).execute(collectionId, currentUserId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //your code here
            }
        });

        //make it so that when we touch the dropdown arrow next to the spinner we also get the spinner expanded
        dropdownArrowImageView = (ImageView)findViewById(R.id.dropdownArrowImageView);
        dropdownArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionsSpinner.performClick();
            }
        });

        stampsListView = (ListView) findViewById(R.id.stampsListView);

        new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUserId);

        resetCollectionsNamesAdapter();

        //setting the ability to redirect to modify/delete activity of the touched postage stamp from the listview
        stampsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewStampActivity.class);
                int postageStampId = chosenCollection.getStampsList().get(position).getStampId();
                intent.putExtra("postageStampId", postageStampId);
                intent.putExtra("collectionId",chosenCollection.getCollectionId());
                intent.putExtra("userId",currentUserId);
                startActivityForResult(intent, REQUEST_VIEW_STAMP_ACTIVITY);
            }
        });
    }

    //assigns the wanted menu to the activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    //assigns each menu item an onclick function
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addNewCollectionOption:
                openCollectionCreationActivity();
                return true;
            case R.id.searchOtherUsersOption:
                openSearchOtherUsersActivity();
                return true;
            case R.id.logoutOption:
                logout();
                return true;
            case R.id.generateChartOption:
                openChartActivity();
                return true;
            case R.id.profileInfoOption:
                openUserProfileActivity(currentUserId);
                return true;
            default:
                return true;
        }
    }

    //reseter for the collections' names spinner adapter
    public void resetCollectionsNamesAdapter() {
        collectionsNames = new ArrayList<>();
        collectionsIds = new ArrayList<>();

        for (StampsCollection stampsCollection : collectionsList) {
            collectionsNames.add(stampsCollection.getCollectionName());
            collectionsIds.add(stampsCollection.getCollectionId());
        }

        collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, collectionsNames);
        collectionsNamesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
        collectionsSpinner.setAdapter(collectionsNamesAdapter);
    }


    //reseter for the postage stamps' listview adapter
    public void resetStampAdapter() {
        if (collectionsNames != null) {
            stampAdapter = new StampAdapter(getApplicationContext(), chosenCollection.getStampsList());
            stampsListView.setAdapter(stampAdapter);
            stampAdapter.notifyDataSetChanged();
        }
    }

    //directing to collection creation activity
    public void openCollectionCreationActivity() {
        Intent intent = new Intent(getApplicationContext(), CollectionCreationActivity.class);
        intent.putExtra("userId", currentUserId);
        startActivityForResult(intent, REQUEST_COLLECTION_CREATOR_ACTIVITY);
    }

    //directing to postage stamp creation ( it is created inside the current selected collection ) activity
    public void openStampRegistrationActivity(View v) {
        if(chosenCollection!=null){
            Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("collectionId", chosenCollection.getCollectionId());
            startActivityForResult(intent, REQUEST_STAMP_REGISTRATION_ACTIVITY);
        }else{
            Toast.makeText(getApplicationContext(), "Create or select a collection to add stamps to !", Toast.LENGTH_SHORT).show();
        }
    }

    //directing to search other users activity
    public void openSearchOtherUsersActivity(){
        Intent intent = new Intent(getApplicationContext(), SearchOtherUsersActivity.class);
        startActivity(intent);
    }

    //directing to logout acitivity and ending the current session
    public void logout(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }

    public void openUserProfileActivity(int userId){
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("userId", currentUserId);
        startActivity(intent);
    }

    public void openChartActivity(){
        new FetchAllPostageStampsAndProcessData(postageStampDao).execute(currentUserId);
    }

    public void deleteSelectedCollection(View v){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("WARNING")
                .setMessage("Are you sure you want to delete this collection?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(collectionsNames.size() != 0) {
                            new DeleteSelectedCollectionAsyncTask(stampsCollectionDao, postageStampDao).execute(currentUserId, chosenCollection.getCollectionId());
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //data process function for converting to scalable data values
    HashMap<String, Float> processDataForCanvasDrawing(HashMap<String, Float> byCountryFrequenciesMap) {
        //calculating total number of user's postage stamps
        totalStampsNumber = 0;
        for (Map.Entry<String, Float> pair : byCountryFrequenciesMap.entrySet()) {
            float frequence = pair.getValue();
            totalStampsNumber += frequence;
        }

        //converting stamps frequences to degrees in order to draw proportional arches on the circle
        for (Map.Entry<String, Float> pair : byCountryFrequenciesMap.entrySet()) {
            String country = pair.getKey();
            byCountryFrequenciesMap.put(country, 360 * (byCountryFrequenciesMap.get(country) / totalStampsNumber));
        }
        return byCountryFrequenciesMap;
    }

    //response management for the end of other activities which are recognised by their request code which was given to them when they were started
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //repopulating the listview after succesfully creating and adding a new postage stamp to the currently selected collection
        if (resultCode == RESULT_OK && requestCode == REQUEST_STAMP_REGISTRATION_ACTIVITY) {
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.getCollectionId());
        }
        //repopulating the collections' names spinner after succesfully creating and adding a new collection
        else if (resultCode == RESULT_OK && requestCode == REQUEST_COLLECTION_CREATOR_ACTIVITY) {
            new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUserId);
        }
        //repopulating the listview after succesfully modifying/deleting postage stamp of the currently selected collection
        else if(resultCode == RESULT_OK && requestCode == REQUEST_VIEW_STAMP_ACTIVITY){
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.getCollectionId());
        }
    }

    //async task for getting all the currently selected collection's postage stamps
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
            chosenCollection.setStampsList(postageStamps);
            resetStampAdapter();
        }
    }

    //async task for getting the user's chosen collection from the spinner
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
            //populate the collection description edit text
            TextView collectionDescriptionTextView = (TextView)findViewById(R.id.collectionDescriptonTextView);
            collectionDescriptionTextView.setText(chosenCollection.getCollectionDescription());
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.getCollectionId());
        }
    }

    //async task for getting all the current user's collections
    public class GetAllUserStampCollectionsAsyncTask extends AsyncTask<Integer, Void, List<StampsCollection>>{
        StampsCollectionDao dao;

        GetAllUserStampCollectionsAsyncTask(StampsCollectionDao dao){
            this.dao = dao;
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
            if(collectionsNames.size() != 0) {
                for (StampsCollection sc : stampsCollectionList) {
                    Log.i("stampCollection", "collectionId: " + sc.getCollectionId() + " ,collectionName: " + sc.getCollectionName() + " ,userId: " + sc.getUserId() + " ,description: " + sc.getCollectionDescription());
                }
            }else{
                chosenCollection = null;
            }
        }
    }

    //async task for deleting the selected collection from both SQLite and Firebase DB
    protected class DeleteSelectedCollectionAsyncTask extends AsyncTask<Integer, Void, Integer>{
        StampsCollectionDao stampsCollectionDao;
        PostageStampDao postageStampDao;

        int collectionId;
        int userId;

        DeleteSelectedCollectionAsyncTask(StampsCollectionDao stampsCollectionsDao, PostageStampDao postageStampDao){
            this.stampsCollectionDao = stampsCollectionsDao;
            this.postageStampDao = postageStampDao;
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            userId = integers[0];
            collectionId = integers[1];

            stampsCollectionDao.deleteCollectionById(collectionId);

            DatabaseReference collectionReference = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + Integer.valueOf(collectionId));
            collectionReference.setValue(null);

            return collectionId;

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            TextView selectedCollectionDescription = (TextView)findViewById(R.id.collectionDescriptonTextView);
            selectedCollectionDescription.setText("");
            if(collectionsNames.size() == 1) {
                stampsListView.setAdapter(null);
            }

            new DeleteSelectedCollectionsStampsAsyncTask(postageStampDao, stampsCollectionDao).execute(collectionId);
        }
    }

    //async task for deleting the selected collection's stamps from SQLite DB
    protected class DeleteSelectedCollectionsStampsAsyncTask extends AsyncTask<Integer, Void, Void>{
        StampsCollectionDao stampsCollectionDao;
        PostageStampDao postageStampDao;

        DeleteSelectedCollectionsStampsAsyncTask(PostageStampDao postageStampDao, StampsCollectionDao stampsCollectionDao){
            this.postageStampDao = postageStampDao;
            this.stampsCollectionDao = stampsCollectionDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int collectionId = integers[0];
            postageStampDao.deleteStampsByCollectionId(collectionId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUserId);
        }
    }

    //asyns task for fetching all the user's stamps,
    //process the list to get the stamp frequencies by countries stored in a map,
    //convert processesed map values to circle degrees in order to provide drawable data for the cart
    //pass the processed map to the chart activity
    public class FetchAllPostageStampsAndProcessData extends AsyncTask<Integer, Void, List<PostageStamp>> {

        PostageStampDao postageStampDao;


        public FetchAllPostageStampsAndProcessData(PostageStampDao postageStampDao) {
            this.postageStampDao = postageStampDao;
        }

        @Override
        protected List<PostageStamp> doInBackground(Integer... integers) {
            return postageStampDao.getAllPostageStampsByUserId(integers[0]);
        }

        @Override
        protected void onPostExecute(List<PostageStamp> postageStampList) {
            super.onPostExecute(postageStampList);

            boolean countryAlreadyExistsInMap = false;
            //we store the countries and the stamp frequencies in a map data structure
            byCountryFrequenciesMap = new HashMap<>();
            byCountryFrequenciesMap.clear();

            //iterating through the stamps list
            for (PostageStamp ps : postageStampList) {
                //resetting the iterator each time
                countryAlreadyExistsInMap = false;
                //iterator = byCountryFrequenciesMap.entrySet().iterator();

                //iterating through the map to find out if country already exists in the map
                //if not we push the country to the map, alongside the initial frequence of 0
                for (Map.Entry<String, Float> pair : byCountryFrequenciesMap.entrySet()) {
                    String country = pair.getKey();
                    if (ps.getCountry().equals(country)) {
                        countryAlreadyExistsInMap = true;
                        break;
                    }
                }
                if (countryAlreadyExistsInMap == false) {
                    byCountryFrequenciesMap.put(ps.getCountry(), (float) 0);
                }
            }

            //counting how many stamps are from each country and assigning stamp frequencies to each country in the map
            for (Map.Entry<String, Float> pair : byCountryFrequenciesMap.entrySet()) {
                String country = pair.getKey();
                for (PostageStamp ps : postageStampList) {
                    if (ps.getCountry().equals(country)) {
                        byCountryFrequenciesMap.put(country, byCountryFrequenciesMap.get(country) + 1);
                    }
                }
            }
            //processing data for proportional chart representation
            byCountryFrequenciesMap = processDataForCanvasDrawing(byCountryFrequenciesMap);

            //start the chart activity
            Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
            intent.putExtra("byCountryFrequenciesMap", byCountryFrequenciesMap);
            startActivity(intent);
        }
    }
}