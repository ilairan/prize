package com.example.prize;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences; // שימוש ב-SharedPreferences
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShakeToMixFragment extends Fragment {

    private static final double SHAKE_THRESHOLD = 10.0; // רמת רגישות לרעידה
    private static final int SHAKE_DURATION = 1000;     // משך זיהוי הרעידה (מילישניות)

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;
    private Handler handler;
    private boolean isShaking;

    private TextView shakeMessage;
    private TextView continueShuffle;

    private SharedPreferences sharedPreferences; // משתנה ל-SharedPreferences
    private String username;
    private int betAmount;

    private static final String PREF_NAME = "user_prefs";  // שם הקובץ
    private static final String KEY_USERNAME = "username"; // מפתח לשם המשתמש

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shake_to_mix, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // אתחול SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(KEY_USERNAME, "defaultUser"); // שליפת שם המשתמש מה-SharedPreferences

        // קבלת פרמטרים שהועברו (הימור וניקוד)
        if (getArguments() != null) {
            betAmount = getArguments().getInt("betAmount", 10);  // הימור (ברירת מחדל 10)
        }

        Log.d("ShakeToMixFragment", "Username: " + username + ", BetAmount: " + betAmount);

        shakeMessage = view.findViewById(R.id.shake_message);         // הודעת "SHAKE TO SHUFFLE"
        continueShuffle = view.findViewById(R.id.continue_shuffle);   // כפתור המשך

        shakeMessage.setText("SHAKE TO SHUFFLE");

        // לחיצה על כפתור המשך (ללא רעידה)
        continueShuffle.setOnClickListener(v -> goToFirstFragment());

        animateShakeMessage();  // אנימציה לטקסט shakeMessage (לא לכפתור)

        // אתחול חיישנים
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();
        isShaking = false;

        setupSensorListener();  // רישום האזנה לחיישן
    }

    private void animateShakeMessage() {
        // אנימציית שינוי גודל הטקסט
        ValueAnimator animator = ValueAnimator.ofFloat(140f, 180f);
        animator.setDuration(1000);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            shakeMessage.setTextSize(animatedValue);
        });
        animator.start();
    }

    private void setupSensorListener() {
        // מאזין לשינויים במד תאוצה
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z); // חישוב תאוצה כוללת

                if (acceleration > SHAKE_THRESHOLD) { // זיהוי רעידה
                    if (!isShaking) {
                        isShaking = true;
                        handler.postDelayed(checkShakeRunnable, SHAKE_DURATION); // ממתין לאישור
                    }
                } else {
                    isShaking = false;
                    handler.removeCallbacks(checkShakeRunnable);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final Runnable checkShakeRunnable = () -> {
        if (isShaking) {
            goToFirstFragment(); // מעבר למשחק אם התבצעה רעידה
        }
    };

    private void goToFirstFragment() {
        // פתיחת FirstFragment עם נתוני המשתמש וההימור
        FirstFragment firstFragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString("username", username);    // שם המשתמש
        args.putInt("betAmount", betAmount);     // סכום ההימור
        firstFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, firstFragment)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        // שחרור המשאבים של החיישן כשהמסך מושהה
        sensorManager.unregisterListener(sensorEventListener);
        handler.removeCallbacks(checkShakeRunnable);
    }
}






