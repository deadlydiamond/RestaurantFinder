package com.example.seekm.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String city,lat,lng,name,time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper myDB;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDB = new DatabaseHelper(this);
        final EditText latitude,longitude;
        final Button find,history;
        final ListView listView;
//        latitude = (EditText)findViewById(R.id.latitude);
//        longitude = (EditText)findViewById(R.id.longitude);
        find = (Button)findViewById(R.id.findButton);
        history = (Button)findViewById(R.id.historyButton);

        final String[] array = new String[]{
                "Karachi",
                "Peshawar",
                "Islamabad",
                "Lahore"
        };



        ArrayAdapter theAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line , array);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(theAdapter);
        theAdapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = spinner.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, city,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView = (ListView)findViewById(R.id.listView);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText restaurant = (EditText)findViewById(R.id.restaurant);
                String rest = restaurant.getText().toString();
                name = rest;
                char result;
                String my;
                for (int i =0;i<rest.length();i++) {
                    result = rest.charAt(i);
                    if (result == ' ') {
                        rest = rest.replaceAll("\\s","%20");
//                        Toast.makeText(MainActivity.this, rest,Toast.LENGTH_LONG).show();
                    }
                }
//                Toast.makeText(MainActivity.this, rest,Toast.LENGTH_LONG).show();
                final int count =0 ;
                final List<String> myList = new ArrayList<String>();
                String url2 = "https://api.opencagedata.com/geocode/v1/json?q="+rest+"%2C"+city+"%2Cpakistan&key=1e979d560c55430eb44e2b86768a2dcc&language=en&pretty=1";
                Ion.with(MainActivity.this)
                        .load(url2)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject data) {

                                String entries = data.get("total_results").toString();
//                                Toast.makeText(MainActivity.this, entries,Toast.LENGTH_LONG).show();
                                for (int i=0;i<Integer.parseInt(entries);i++) {
                                    JsonArray jsonArray = data.getAsJsonArray("results");
                                    JsonObject jsonObject = (JsonObject) jsonArray.get(i);
                                    JsonObject components = jsonObject.getAsJsonObject("components");
                                    String type = components.get("_type").toString();
                                    JsonObject geometry = jsonObject.getAsJsonObject("geometry");
                                    String latitude = geometry.get("lat").toString();
                                    String longitude = geometry.get("lng").toString();
                                    JsonObject timestamp = data.getAsJsonObject("timestamp");
                                    time = timestamp.get("created_http").toString();

                                    type = type.replaceAll("^\"|\"$", "");
                                    if (type.equals("restaurant")||type.equals("fast_food")){
                                        String add = jsonObject.get("formatted").toString();
                                        String count = Integer.toString(i+1);
                                        myList.add(count);
                                        myList.add("Address: " + add);
                                        myList.add("Latitude: " +latitude);
                                        myList.add("Longitude: " + longitude);

                                    }
                                }
                                myDB.insertData(name,time);
                                if (myList.isEmpty()){
                                    Toast.makeText(MainActivity.this, "Not found",Toast.LENGTH_LONG).show();
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,myList);
                                listView.setAdapter(arrayAdapter);
                            }

                        });

//                Toast.makeText(MainActivity.this, city,Toast.LENGTH_LONG).show();
            }

//                latitude.setText(lat);
//                longitude.setText(lng);

        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,history.class);
                startActivity(intent);
            }
        });



    }
}