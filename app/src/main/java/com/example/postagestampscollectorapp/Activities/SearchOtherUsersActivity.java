package com.example.postagestampscollectorapp.Activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Database.StampsCollectionDao;
import com.example.postagestampscollectorapp.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

//WORK IN PROGRESS
public class SearchOtherUsersActivity extends AppCompatActivity {

    SearchView searchView;

    //SQLite database
    StampsCollectionDao stampsCollectionDao;
    Database database;

    //Firebase database
    FirebaseDatabase fbDatabase;

    ArrayAdapter usernameAdapter;
    List<String> usernamesList;
    List<String> searchedUsernamesList;

    ListView usernamesListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_other_users);

        searchView = (SearchView) findViewById(R.id.searchView);
        usernamesListView = (ListView) findViewById(R.id.usernamesListView);

        usernamesList = new ArrayList<>();
        searchedUsernamesList = new ArrayList<>();

        usernamesList.add("Alina");
        usernamesList.add("Antonio");
        usernamesList.add("Cleo");
        usernamesList.add("Costel");
        usernamesList.add("Coco");

        usernameAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, usernamesList);
        usernamesListView.setAdapter(usernameAdapter);

        SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(usernamesList.contains(query)){
                    usernameAdapter.getFilter().filter(query);
                }else{
                    Toast.makeText(getApplicationContext(), "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                usernameAdapter.getFilter().filter(text);
                return false;
            }
        };

        searchView.setOnQueryTextListener(onQueryTextListener);

        fbDatabase = FirebaseDatabase.getInstance();

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();
        stampsCollectionDao = database.stampsCollectionDao();


    }
}
