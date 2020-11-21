 package com.example.postagestampscollectorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner stampCountrySpinner;
    ListView stampsListView;
    ArrayList<PostageStamp> stampsList = new ArrayList<PostageStamp>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* add new stamp layout
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }

        Collections.sort(countries);
        for (String country : countries) {
            System.out.println(country);
        }

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, countries);

        countryAdapter.setDropDownViewResource(R.layout.spinner_item);

        stampCountrySpinner = (Spinner) findViewById(R.id.stampCountrySpinner);

        stampCountrySpinner.setAdapter(countryAdapter);*/

        //listview layout
        PostageStamp ps1 = new PostageStamp("John Lewis", 1919, "USA", "Baseball star");
        PostageStamp ps2 = new PostageStamp("Dam Bridge", 2001, "USA", "famous US Landmarks");
        stampsList.add(ps1);
        stampsList.add(ps2);
        stampsListView = (ListView) findViewById(R.id.stampsListView);
        StampAdapter stampAdapter = new StampAdapter(getApplicationContext(), stampsList);
        stampsListView.setAdapter(stampAdapter);

    }

    public void openStampRegistrationActivity(View v){
        Intent intent = new Intent(getApplicationContext(), StampRegistrationActivity.class);
        intent.putParcelableArrayListExtra("stampsList", stampsList);
        startActivity(intent);
    }
}