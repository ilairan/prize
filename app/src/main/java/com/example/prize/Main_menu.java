package com.example.prize;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class Main_menu extends AppCompatActivity {

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
                loadFragment(new SettingsFragment());
                toggleVisibility(false);
            }
        });

        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FirstFragment());
                toggleVisibility(false);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ShakeToMixFragment());
                toggleVisibility(false);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void toggleVisibility(boolean showMainMenu) {
        frameLayout.setVisibility(showMainMenu ? View.GONE : View.VISIBLE);
        settingsbutton.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        startbutton.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        button3.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
        textView.setVisibility(showMainMenu ? View.VISIBLE : View.GONE);
    }
}
