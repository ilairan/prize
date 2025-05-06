package com.example.prize;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    private SeekBar musicVolumeSeekBar, sfxVolumeSeekBar;  /* פסי ווליום */
    private Switch notificationsSwitch;  /* כפתור הדלקה/כיבוי להודעות */
    private TextView changeDetailsButton, logoutButton, backButton;  /* כפתורי פעולה */
    private DBHelper dbHelper;  /* גישה למסד הנתונים */
    private String userEmail;  /* המייל של המשתמש */
    private SoundEffectsManager soundEffectsManager;  /* מנהל אפקטים קוליים */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);  /* טעינת התצוגה */
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(getContext());  /* אתחול DBHelper */
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("username", null);  /* שליפת המייל של המשתמש */

        // קישור רכיבי ה-XML
        musicVolumeSeekBar = view.findViewById(R.id.musicVolumeSeekBar);
        sfxVolumeSeekBar = view.findViewById(R.id.sfxVolumeSeekBar);
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        changeDetailsButton = view.findViewById(R.id.changeDetailsButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        backButton = view.findViewById(R.id.Back_buttom);

        changeDetailsButton.setOnClickListener(v -> showChangeDetailsDialog());  /* כפתור שינוי פרטי משתמש */

        // טעינת ההגדרות מהמסד
        if (userEmail != null) {
            int musicVolume = dbHelper.getMusicVolume(userEmail);
            int sfxVolume = dbHelper.getSoundEffectsVolume(userEmail);
            boolean notificationsEnabled = dbHelper.getNotificationsEnabled(userEmail);

            musicVolumeSeekBar.setProgress(musicVolume);
            sfxVolumeSeekBar.setProgress(sfxVolume);
            notificationsSwitch.setChecked(notificationsEnabled);

            MusicService.setMusicVolume(musicVolume / 100f);  /* הפעלת עוצמת מוזיקה */

            soundEffectsManager = new SoundEffectsManager(getContext());  /* אתחול אפקטים קוליים */

            // שינוי עוצמת אפקטים קוליים בלייב
            sfxVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    dbHelper.setSoundEffectsVolume(userEmail, progress);  /* עדכון במסד */
                    soundEffectsManager.setSfxVolume(progress);  /* עדכון אפקטים בלייב */
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        // שינוי עוצמת מוזיקה בלייב
        musicVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dbHelper.setMusicVolume(userEmail, progress);  /* עדכון במסד */
                MusicService.setMusicVolume(progress / 100f);  /* עדכון בלייב */
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // שינוי מצב ההודעות
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.setNotificationsEnabled(userEmail, isChecked);
        });

        // התנתקות מהמערכת
        logoutButton.setOnClickListener(v -> {
            sharedPreferences.edit().remove("username").apply();  /* מחיקת המייל מהעדפות */
            startActivity(new Intent(getActivity(), Login_activity.class));  /* מעבר למסך התחברות */
            requireActivity().finish();  /* סגירת ה-Activity הנוכחי */
        });

        // חזרה לתפריט הראשי
        backButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), Main_menu.class)));
    }

    // תיבת דיאלוג לשינוי פרטי משתמש
    private void showChangeDetailsDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_details, null);
        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        EditText editPhone = dialogView.findViewById(R.id.editPhone);
        EditText editPassword = dialogView.findViewById(R.id.editPassword);

        User user = dbHelper.getUserByEmail(userEmail);  /* שליפת פרטי המשתמש */
        if (user != null) {
            editName.setText(user.getName());
            editEmail.setText(user.getEmail());
            editPhone.setText(user.getPhone_num());
            editPassword.setText(user.getPassword());
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Change User Details")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = editName.getText().toString();
                    String email = editEmail.getText().toString();
                    String phone = editPhone.getText().toString();
                    String password = editPassword.getText().toString();

                    dbHelper.updateUserdata(new User(name, email, phone, password));  /* עדכון במסד */

                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("username", email).apply();  /* עדכון העדפות */
                    userEmail = email;  /* עדכון מייל מקומי בפרגמנט */
                })
                .setNegativeButton("Cancel", null)
                .show(); // ← You were missing this line!

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (soundEffectsManager != null) soundEffectsManager.release();  /* שחרור אפקטים קוליים */
    }
}

