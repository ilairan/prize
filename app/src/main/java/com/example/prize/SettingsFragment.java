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

    private SeekBar musicVolumeSeekBar, sfxVolumeSeekBar;
    private Switch notificationsSwitch;
    private TextView changeDetailsButton, logoutButton, backButton;
    private DBHelper dbHelper;
    private String userEmail;
    private SoundEffectsManager soundEffectsManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DBHelper(getContext());
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("username", null);  // Using email as 'username'

        musicVolumeSeekBar = view.findViewById(R.id.musicVolumeSeekBar);
        sfxVolumeSeekBar = view.findViewById(R.id.sfxVolumeSeekBar);
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch);
        changeDetailsButton = view.findViewById(R.id.changeDetailsButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        backButton = view.findViewById(R.id.Back_buttom);

        changeDetailsButton.setOnClickListener(v -> showChangeDetailsDialog());
        // === Load settings ===
        if (userEmail != null) {
            int musicVolume = dbHelper.getMusicVolume(userEmail);
            int sfxVolume = dbHelper.getSoundEffectsVolume(userEmail);
            boolean notificationsEnabled = dbHelper.getNotificationsEnabled(userEmail);

            musicVolumeSeekBar.setProgress(musicVolume);
            sfxVolumeSeekBar.setProgress(sfxVolume);
            notificationsSwitch.setChecked(notificationsEnabled);

            // Apply music volume immediately when fragment opens
            MusicService.setMusicVolume(musicVolume / 100f);  // Convert to 0.0f - 1.0f

            soundEffectsManager = new SoundEffectsManager(getContext());  // Initialize

// Update this part:
            sfxVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (userEmail != null) {
                        dbHelper.setSoundEffectsVolume(userEmail, progress);
                        soundEffectsManager.setSfxVolume(progress);  // Update live
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

        }

        // === Listeners ===
        musicVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (userEmail != null) dbHelper.setMusicVolume(userEmail, progress);
                // Apply volume immediately to the music service
                MusicService.setMusicVolume(progress / 100f);  // Convert to 0.0f - 1.0f
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sfxVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (userEmail != null) dbHelper.setSoundEffectsVolume(userEmail, progress);
                // Apply sound effect volume globally (if needed)
                // You can call a similar static method for SFX here if you have one
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (userEmail != null) dbHelper.setNotificationsEnabled(userEmail, isChecked);
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");  // Clear auto-login
            editor.apply();
            startActivity(new Intent(getActivity(), Login_activity.class));
            requireActivity().finish();  // Close current activity
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Main_menu.class);
            startActivity(intent);
        });
    }

    private void showChangeDetailsDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_change_details, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);
        EditText editPhone = dialogView.findViewById(R.id.editPhone);
        EditText editPassword = dialogView.findViewById(R.id.editPassword);

        // Pre-fill user data from the database
        User user = dbHelper.getUserByEmail(userEmail);
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

                    // Update database
                    User updatedUser = new User(name, email, phone, password);
                    dbHelper.updateUserdata(updatedUser);

                    // Update shared preferences if email changed
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("username", email).apply();

                    userEmail = email;  // Update locally in fragment too
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (soundEffectsManager != null) {
            soundEffectsManager.release();
        }
    }

}
