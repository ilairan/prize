package com.example.prize;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.prize.databinding.ActivityMainMenuBinding;

public class Main_menu extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainMenuBinding binding;


    FrameLayout frameLayout;
    TextView settingsbutton;
    TextView startbutton;
    TextView button3;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        frameLayout = findViewById(R.id.frameLayout);
        settingsbutton = findViewById(R.id.Settingsbutton);
        startbutton = findViewById(R.id.startbutton);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.main_menu);

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new SettingsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
                frameLayout.setVisibility(View.VISIBLE);
                settingsbutton.setVisibility(View.GONE);
                startbutton.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

            }
        });

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new SecondFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
                frameLayout.setVisibility(View.VISIBLE);
                settingsbutton.setVisibility(View.GONE);
                startbutton.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

            }

        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                fragment = new SecondFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
                frameLayout.setVisibility(View.VISIBLE);
                settingsbutton.setVisibility(View.GONE);
                startbutton.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

            }
        });

    }
}