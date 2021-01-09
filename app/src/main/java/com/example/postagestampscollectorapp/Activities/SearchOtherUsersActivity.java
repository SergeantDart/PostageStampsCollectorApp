package com.example.postagestampscollectorapp.Activities;

import android.app.Activity;
import android.app.AsyncNotedAppOp;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.postagestampscollectorapp.Data.PostageStamp;
import com.example.postagestampscollectorapp.Data.StampsCollection;
import com.example.postagestampscollectorapp.Data.User;
import com.example.postagestampscollectorapp.Database.Database;
import com.example.postagestampscollectorapp.Others.SearchedUserStampAdapter;
import com.example.postagestampscollectorapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

//in this activity all the information is extracted and processed from Firebase DB and Firestore
//this activities features are not available if not connected to the Internet
public class SearchOtherUsersActivity extends AppCompatActivity {

    SearchView searchView;

    //SQLite database
    Database database;

    //Firebase database
    FirebaseDatabase fbDatabase;
    FirebaseStorage fbStorage;

    Query usernamesQuery;
    ValueEventListener usernamesValueEventListener;

    Query searchedUserQuery;
    ValueEventListener searchedUserValueEventListener;

    Query searchedUserSelectedCollectionQuery;
    ValueEventListener searchedUserSelectedCollectionValueEventListener;

    //search view's result listview adapter
    ArrayAdapter usernameAdapter;

    //searched user's currently selected collections' stamp adapter
    SearchedUserStampAdapter searchedUserStampAdapter;

    //list that holds all the fetched usernames from the Firebase DB
    List<String> usernamesList;

    //listview used to hold the search view's result info
    ListView usernamesListView;

    //array adapter for the searched user's public collections names listview
    ArrayAdapter<String> collectionsNamesAdapter;

    //list that holds all the searched user's public collections' names
    List<String> collectionsNamesList;

    //list that holds all the searched user's public collections' id
    List<Integer> collectionsIdsList;

    //list that holds all the searched user's currently selected public collections
    List<PostageStamp> postageStampsList;

    SearchView.OnQueryTextListener onQueryTextListener;

    //currently selected user's username, floating in the search view
    String currentlySearchedUserUsername;
    //currently selected user's id
    int currentlySearchedUserId;

    //listview that holds all the searched user's currently selected collections' stamps
    ListView searchedUserPostageStampsListView;

    //labels
    TextView usernameLabelTextView;
    TextView fullNameLabelTextView;
    TextView emailLabelTextView;
    TextView telephoneLabelTextView;
    TextView shortBioLabelTextView;
    TextView collectionsLabelTextView;
    TextView collectionDescriptionLabelTextView;

    //info
    TextView searchedCollectionDescriptionTextView;
    Spinner searchedUserCollectionsSpinner;
    TextView searchedUsernameTextView;
    TextView searchedFullNameTextView;
    TextView searchedTelephoneTextView;
    TextView searchedEmailTextView;
    TextView searchedShortBioTextView;


    @Override
    protected void onPause() {
        super.onPause();
        //removing the value event listener and stop the query loop ( could bug out in other Firebase DB queries)
        usernamesQuery.removeEventListener(usernamesValueEventListener);
        searchedUserQuery.removeEventListener(searchedUserValueEventListener);
        searchedUserQuery.removeEventListener(searchedUserSelectedCollectionValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //removing the value event listener and stop the query loop ( could bug out in other Firebase DB queries)
        usernamesQuery.removeEventListener(usernamesValueEventListener);
        searchedUserQuery.removeEventListener(searchedUserValueEventListener);
        searchedUserQuery.removeEventListener(searchedUserSelectedCollectionValueEventListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_other_users);

        //labels
        usernameLabelTextView = (TextView) findViewById(R.id.searchedUserUsernameTextViewLabel);
        fullNameLabelTextView = (TextView) findViewById(R.id.searchedUserFullNameTextViewLabel);
        telephoneLabelTextView = (TextView) findViewById(R.id.searchedUserTelephonelTextViewLabel);
        emailLabelTextView = (TextView) findViewById(R.id.searchedUserEmailTextViewLabel);
        shortBioLabelTextView = (TextView) findViewById(R.id.searchedUserBioTextViewLabel);
        collectionsLabelTextView = (TextView) findViewById(R.id.searchedUserCollectionsLabel);
        collectionDescriptionLabelTextView = (TextView) findViewById(R.id.searchedUserCollectionDescriptionTextBoxLabel);

        //info
        searchedUsernameTextView = (TextView) findViewById(R.id.userUsernameTextView);
        searchedFullNameTextView = (TextView) findViewById(R.id.userFullNameTextView);
        searchedTelephoneTextView = (TextView) findViewById(R.id.userTelephoneTextView);
        searchedEmailTextView = (TextView) findViewById(R.id.userEmailTextView);
        searchedShortBioTextView = (TextView) findViewById(R.id.userBioTextView);
        searchedUserCollectionsSpinner = (Spinner) findViewById(R.id.userCollectionsSpinner);
        searchedCollectionDescriptionTextView = (TextView) findViewById(R.id.userCollectionDescriptionTextView);


        fbDatabase = FirebaseDatabase.getInstance();
        fbStorage = FirebaseStorage.getInstance();

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "myDatabase").fallbackToDestructiveMigration().build();

        searchView = (SearchView) findViewById(R.id.searchView);
        usernamesListView = (ListView) findViewById(R.id.usernamesListView);
        searchedUserPostageStampsListView = (ListView) findViewById(R.id.searchedUserPostageStampsListView);

        //holds all the fetched usernames from the Firebase DB
        usernamesList = new ArrayList<>();

        collectionsNamesList = new ArrayList<>();
        collectionsIdsList = new ArrayList<>();

        postageStampsList = new ArrayList<>();

        usernameAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, usernamesList);


        usernamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usernamesListView.setVisibility(View.INVISIBLE);
                collectionDescriptionLabelTextView.setVisibility(View.VISIBLE);
                collectionsLabelTextView.setVisibility(View.VISIBLE);
                emailLabelTextView.setVisibility(View.VISIBLE);
                fullNameLabelTextView.setVisibility(View.VISIBLE);
                shortBioLabelTextView.setVisibility(View.VISIBLE);
                telephoneLabelTextView.setVisibility(View.VISIBLE);
                usernameLabelTextView.setVisibility(View.VISIBLE);

                searchedCollectionDescriptionTextView.setVisibility(View.VISIBLE);
                searchedEmailTextView.setVisibility(View.VISIBLE);
                searchedFullNameTextView.setVisibility(View.VISIBLE);
                searchedShortBioTextView.setVisibility(View.VISIBLE);
                searchedTelephoneTextView.setVisibility(View.VISIBLE);
                searchedUserCollectionsSpinner.setVisibility(View.VISIBLE);
                searchedUsernameTextView.setVisibility(View.VISIBLE);

                collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, collectionsNamesList);
                collectionsNamesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                searchedUserCollectionsSpinner.setAdapter(collectionsNamesAdapter);

                searchedUserStampAdapter = new SearchedUserStampAdapter(getApplicationContext(), postageStampsList, fbDatabase, fbStorage);
                searchedUserPostageStampsListView.setAdapter(searchedUserStampAdapter);

                //hiding the keyboard when selecting an username
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //putting the selected username from the result listview in the search view
                TextView text1 = view.findViewById(android.R.id.text1);
                searchView.setQuery(text1.getText().toString(), false);
                usernamesListView.setVisibility(View.INVISIBLE);

                currentlySearchedUserUsername = text1.getText().toString();

                postageStampsList.clear();
                searchedUserStampAdapter.notifyDataSetChanged();


                new FetchSearchedUserInformationAsyncTask().execute(currentlySearchedUserUsername);
            }
        });

        //setting onQueryTextListener to implement actions whenever the search view's text changes
        onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (usernamesList.contains(query)) {
                    usernameAdapter.getFilter().filter(query);
                } else {
                    Toast.makeText(getApplicationContext(), "No Match found", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            //if the text changes we apply the adapter filter to ignore upper/lower case differences and to check wether or not the result contains the searc view's text
            @Override
            public boolean onQueryTextChange(String text) {
                usernameAdapter.getFilter().filter(text);
                if (text.isEmpty()) {
                    usernamesListView.setVisibility(View.INVISIBLE);
                } else {
                    usernamesListView.setVisibility(View.VISIBLE);
                }
                return false;
            }
        };

        new FetchUsernamesAsyncTask().execute();
    }


    //async task for fetching all the usernames from the Firebase DB in order to populate the search view's results posibilities
    protected class FetchUsernamesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            usernamesQuery = fbDatabase.getReference("users").orderByChild("username");

            //ValueEventListener is a kind of what to do once the query is done, the results being stored in snapshot
            usernamesValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //checking if such a match exists ( the snapshot is delivered by default through a list even though it contains only 1 element ! )
                    String username;
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            //add all the Firebase DB usernames in the usernames list
                            username = userSnapshot.child("username").getValue(String.class);
                            usernamesList.add(username);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //nothing
                }
            };
            //starting the query
            usernamesQuery.addValueEventListener(usernamesValueEventListener);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            usernameAdapter.notifyDataSetChanged();
            usernamesListView.setAdapter(usernameAdapter);
            searchView.setOnQueryTextListener(onQueryTextListener);
        }
    }

    //async task used for fetching all the currently searched user's information
    protected class FetchSearchedUserInformationAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            searchedUserQuery = fbDatabase.getReference("users").orderByChild("username").equalTo(strings[0]);
            searchedUserValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    //checking if such a match exists ( the snapshot is delivered by default through a list even though it contains only 1 element ! )
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            //automatically creates an user object based on the matched attributes from the Firebase DB
                            User user = userSnapshot.getValue(User.class);
                            currentlySearchedUserId = user.getUserId();
                            searchedFullNameTextView.setText(user.getFullName());
                            searchedUsernameTextView.setText(user.getUsername());
                            searchedEmailTextView.setText(user.getEmail());
                            searchedTelephoneTextView.setText(user.getPhone());
                            searchedShortBioTextView.setText(user.getShortBio());
                            //iterating through the current user's collections and adding them to SQLite DB from Firebase DB
                            DataSnapshot collectionsSnapshot = userSnapshot.child("collections");
                            collectionsNamesList.clear();
                            collectionsIdsList.clear();
                            postageStampsList.clear();
                            for (DataSnapshot collectionSnapshot : collectionsSnapshot.getChildren()) {
                                //automatically creates a stamps collection object based on the matched attributes from the Firebase DB
                                StampsCollection stampsCollection = collectionSnapshot.getValue(StampsCollection.class);
                                collectionsNamesList.add(stampsCollection.getCollectionName());
                                collectionsIdsList.add(stampsCollection.getCollectionId());
                            }
                            //resetting the postage stamps listview and the collections' names spinner
                            collectionsNamesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, collectionsNamesList);
                            collectionsNamesAdapter.setDropDownViewResource(R.layout.spinner_dropdown_items);
                            searchedUserCollectionsSpinner.setAdapter(collectionsNamesAdapter);

                            searchedUserStampAdapter = new SearchedUserStampAdapter(getApplicationContext(), postageStampsList, fbDatabase, fbStorage);
                            searchedUserPostageStampsListView.setAdapter(searchedUserStampAdapter);



                            searchedCollectionDescriptionTextView.setText("");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //nothing
                }
            };
            //start the query
            searchedUserQuery.addValueEventListener(searchedUserValueEventListener);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //after the user's infromation has been loadded, we assign on click events on the collections spinner
            searchedUserCollectionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    postageStampsList.clear();
                    searchedUserStampAdapter.notifyDataSetChanged();
                    new FetchSearchedUserCollectionsInfoAsyncTask().execute(currentlySearchedUserId, collectionsIdsList.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //nothing
                }
            });
        }
    }

    //async task for fetching all currently searched user's information about the selected collection
    protected class FetchSearchedUserCollectionsInfoAsyncTask extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... integers) {
            int userId = integers[0];
            int collectionId = integers[1];


            searchedUserQuery = fbDatabase.getReference("users/user - " + Integer.valueOf(userId) + "/collections/collection - " + Integer.valueOf(collectionId));
            searchedUserSelectedCollectionValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String searchedCollectionDescription = snapshot.child("collectionDescription").getValue(String.class);
                        searchedCollectionDescriptionTextView.setText(searchedCollectionDescription);
                        DataSnapshot stampsSnapshot = snapshot.child("stamps");
                        if (stampsSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot stampSnapshot : stampsSnapshot.getChildren()){
                                int stampId = stampSnapshot.child("stampId").getValue(Integer.class);
                                int collectionId = stampSnapshot.child("collectionId").getValue(Integer.class);
                                String name = stampSnapshot.child("name").getValue(String.class);
                                String country = stampSnapshot.child("country").getValue(String.class);
                                int year = stampSnapshot.child("year").getValue(Integer.class);
                                String description = stampSnapshot.child("description").getValue(String.class);
                                String stampPicUri = stampSnapshot.child("stampPicUri").getValue(String.class);
                                PostageStamp ps = new PostageStamp(stampId, collectionId, name, stampPicUri, year, country, description);
                                postageStampsList.add(ps);

                                Log.i("Image locations: ", stampPicUri);
                            }
                            searchedUserStampAdapter = new SearchedUserStampAdapter(getApplicationContext(), postageStampsList, fbDatabase, fbStorage);
                            searchedUserPostageStampsListView.setAdapter(searchedUserStampAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            searchedUserQuery.addValueEventListener(searchedUserSelectedCollectionValueEventListener);
            return null;
        }
    }
}

