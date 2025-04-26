package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Activity_listview extends AppCompatActivity {
    ArrayList<String> list_listview = new ArrayList<>();
    ListView basic_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        //set up list_listview
        list_listview.add("Poker");
        list_listview.add("Blackjack");
        list_listview.add("Roulette");

        // Set up for basic_list
        ListView basic_list = findViewById(R.id.basic_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list_listview);
        basic_list.setAdapter(adapter);

        //onClicklistener for basic_list items
        basic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = list_listview.get(position);
                // Handle the click action (e.g., show a toast with the selected item)
                Toast.makeText(Activity_listview.this, "Clicked: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

