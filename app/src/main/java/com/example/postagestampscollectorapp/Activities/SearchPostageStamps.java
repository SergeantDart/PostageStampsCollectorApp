package com.example.postagestampscollectorapp.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Data.User;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.StampsCollectionDao;
import com.example.postagestampscollectorapp.Database.UserDao;
import com.example.postagestampscollectorapp.Others.StampAdapter;
import com.example.postagestampscollectorapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPostageStamps extends AppCompatActivity {

    SearchView searchView;

    ListView stampsListView;
    StampAdapter stampAdapter;
    ArrayAdapter<String> collectionsNamesAdapter;

    StampsCollectionDao stampsCollectionDao;
    Database database;
    List<StampsCollection> collectionsList = new ArrayList<StampsCollection>();
    List<String> collectionsNames;
    List<Integer> collectionsIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_stamps);

        searchView=(SearchView)findViewById(R.id.search_view);
        String query=searchView.getQuery().toString();
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();
        stampsListView = (ListView) findViewById(R.id.listViewUsersCollections);

        //resetCollectionsNamesAdapter();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetCollectionsNamesAdapter();
            }
        });






    }
    public class GetAllUserStampCollectionsAsyncTask extends AsyncTask<Integer, Void, List<StampsCollection>>{
        StampsCollectionDao dao;
        UserDao userDao;

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

    protected class GetUserIdAsyncTask extends  AsyncTask<String, Void, Integer>{
       UserDao dao;
       StampsCollectionDao sDao;

        GetUserIdAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            return dao.getSearchedUserId(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            new GetAllUserStampCollectionsAsyncTask(sDao).execute(integer);
        }
    }


    //CHECK IF ACCOUNT EXISTS
    protected class CheckIfAccountExistsAsyncTask extends AsyncTask<String, Void, Integer> {

        UserDao dao;
        String username;

        CheckIfAccountExistsAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            username=strings[0];
            return dao.getSearchedUserId(strings[0]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if (count == 1) {
                new GetUserIdAsyncTask(dao).execute(username);
            } else if (count == 0) {
                Toast.makeText(getApplicationContext(), "Invalid username or passowrd! Try again...", Toast.LENGTH_SHORT).show();

            }
        }
    }



    public void resetCollectionsNamesAdapter() {
        collectionsNames = new ArrayList<>();
        collectionsIds = new ArrayList<>();

        for (StampsCollection stampsCollection : collectionsList) {
            collectionsNames.add(stampsCollection.getCollectionName());
            collectionsIds.add(stampsCollection.getCollectionId());
            Toast.makeText(this, stampsCollection.getCollectionName(), Toast.LENGTH_SHORT).show();

        }

        collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, collectionsNames);
        stampsListView.setAdapter(collectionsNamesAdapter);

    }

}
