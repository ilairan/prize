package com.example.prize;

import android.content.SharedPreferences;  // נוסיף תמיכה ב-SharedPreferences
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class SettingsFragment extends Fragment {

    TextView backButton;
    TextView scoreTextView;  // נניח שיש TextView להצגת ניקוד

    DBHelper dbHelper;
    SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";  // שם קובץ ה-SharedPreferences
    private static final String KEY_USERNAME = "username"; // מפתח לשם המשתמש

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // אתחול כפתורים ואלמנטים
        backButton = view.findViewById(R.id.Back_buttom);
        scoreTextView = view.findViewById(R.id.scoreTextView);  // נניח שיש ID כזה בטופס שלך

        // אתחול DBHelper ו-SharedPreferences
        dbHelper = new DBHelper(getContext());
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, getContext().MODE_PRIVATE);

        // שליפת שם המשתמש מה-SharedPreferences
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        if (username != null && scoreTextView != null) {
            int score = dbHelper.getScore(username);  // שליפת הניקוד מהמסד
            scoreTextView.setText("Score: " + score);  // הצגת הניקוד
        }

        // מאזין ללחצן חזרה
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.popBackStack();  // חזרה אחורה במסך

                // החזרת התפריט הראשי
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

