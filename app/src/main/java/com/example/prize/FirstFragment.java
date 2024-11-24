package com.example.prize;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

public class FirstFragment extends Fragment {
    TextView backButton;
    @Nullable

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize the back button
        TextView backButton = view.findViewById(R.id.Back_buttom);

        // Set a click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use FragmentManager to pop the back stack
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();

                if(getActivity() instanceof Main_menu){
                    Main_menu mainMenu = (Main_menu) getActivity();
                    mainMenu.frameLayout.setVisibility(View.GONE);
                    mainMenu.settingsbutton.setVisibility(View.VISIBLE);
                    mainMenu.startbutton.setVisibility(View.VISIBLE);
                    mainMenu.button3.setVisibility(View.VISIBLE);
                    mainMenu.textView.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}