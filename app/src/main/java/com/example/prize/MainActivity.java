package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView continue_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get ids
        continue_button = findViewById(R.id.continue_button);

     //   public boolean onCreateOptionsMenu(Menu menu) {
       //     getMenuInflater().inflate(R.menu.main, menu);
        //    return true;
        //}




        //onclicklistener for continue button
        continue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //after click on login move to Login_activity
                Intent Intent = new Intent(MainActivity.this, Login_activity.class);
                startActivity(Intent);

            }
        });





    }
}