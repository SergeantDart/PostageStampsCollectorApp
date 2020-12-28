package com.example.postagestampscollectorapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //request codes
    final int REQUST_COLLECTION_CREATOR_ACTIVITY = 0;
    final int REQUEST_STAMP_REGISTRATION_ACTIVITY = 1;
    final int REQUEST_VIEW_STAMP_ACTIVITY = 2;

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
    int currentUser;

    //SQLite database
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

        //creating and populating the spinner with all the user's collection names
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

        //make it so that when we touch the dropdown arrow next to the spinner we also get the spinner expanded
        dropdownArrowImageView = (ImageView)findViewById(R.id.dropdownArrowImageView);
        dropdownArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionsSpinner.performClick();
            }
        });

        stampsListView = (ListView) findViewById(R.id.stampsListView);

        new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUser);

        resetCollectionsNamesAdapter();

        //setting the ability to redirect to modify/delete activity of the touched postage stamp from the listview
        stampsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ViewStampActivity.class);
                int postageStampId = chosenCollection.getStampsList().get(position).getStampId();
                intent.putExtra("postageStampId", postageStampId);
                intent.putExtra("collectionId",chosenCollection.getCollectionId());
                intent.putExtra("userId",currentUser);
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
            case R.id.profileInfoOption:
                Log.i("Profile activity: ", "opened");
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
            stampAdapter = new StampAdapter(getApplicationContext(), chosenCollection.getStampsList());
            stampsListView.setAdapter(stampAdapter);
            stampAdapter.notifyDataSetChanged();
    }

    //directing to collection creation activity
    public void openCollectionCreationActivity() {
        Intent intent = new Intent(getApplicationContext(), CollectionCreationActivity.class);
        intent.putExtra("userId", currentUser);
        startActivityForResult(intent, REQUST_COLLECTION_CREATOR_ACTIVITY);
    }

    //directing to postage stamp creation ( it is created inside the current selected collection ) activity
    public void openStampRegistrationActivity(View v) {
        if(chosenCollection!=null){
            Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
            intent.putExtra("userId", currentUser);
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

    //response management for the end of other activities which are recognised by their request code which was given to them when they were started
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //repopulating the listview after succesfully creating and adding a new postage stamp to the currently selected collection
        if (resultCode == RESULT_OK && requestCode == REQUEST_STAMP_REGISTRATION_ACTIVITY) {
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.getCollectionId());
        }
        //repopulating the collections' names spinner after succesfully creating and adding a new collection
        else if (resultCode == RESULT_OK && requestCode == REQUST_COLLECTION_CREATOR_ACTIVITY) {
            new GetAllUserStampCollectionsAsyncTask(stampsCollectionDao).execute(currentUser);
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
            //populate the collection desciption edittext
            TextView collectionDescriptionTextView = (TextView)findViewById(R.id.collectionDescriptonTextView);
            collectionDescriptionTextView.setText(chosenCollection.getCollectionDescription());
            new GetWantedStampsAsyncTask(postageStampDao).execute(chosenCollection.getCollectionId());
        }
    }

    //async task for getting all the current user's collections
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
                Log.i("stampCollection", "collectionId: "+sc.getCollectionId() +" ,collectionName: "+sc.getCollectionName() + " ,userId: " + sc.getUserId() + " ,description: " + sc.getCollectionDescription());
            }
        }
    }
}