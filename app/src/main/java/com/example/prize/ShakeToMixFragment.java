package com.example.prize;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
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

    private static final double SHAKE_THRESHOLD = 8.0; /* הורדתי את סף הרגישות כדי שיהיה קל יותר לזהות רעידות */
    private static final int SHAKE_DURATION = 500; /* קיצרתי את זמן האישור לזיהוי רעידה */

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;
    private Handler handler;
    private boolean isShaking;

    private TextView shakeMessage, continueShuffle;

    private SharedPreferences sharedPreferences;
    private String username;
    private int betAmount;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shake_to_mix, container, false);  /* טעינת תצוגת הפרגמנט */
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(KEY_USERNAME, "defaultUser");  /* שליפת שם המשתמש */

        if (getArguments() != null) {
            betAmount = getArguments().getInt("betAmount", 10);  /* שליפת סכום ההימור */
        }

        Log.d("ShakeToMixFragment", "Username: " + username + ", BetAmount: " + betAmount);

        shakeMessage = view.findViewById(R.id.shake_message);
        continueShuffle = view.findViewById(R.id.continue_shuffle);

        shakeMessage.setText("SHAKE TO SHUFFLE");  /* טקסט ראשי */

        continueShuffle.setOnClickListener(v -> goToFirstFragment());  /* מעבר ידני לפרגמנט המשחק */

        animateShakeMessage();  /* אנימציה לטקסט */

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();
        isShaking = false;

        setupSensorListener();  /* אתחול האזנה לחיישן */
    }

    private void animateShakeMessage() {
        /* אנימציה לגודל הטקסט */
        ValueAnimator animator = ValueAnimator.ofFloat(140f, 160f);  /* הקטנתי את טווח הגדלים */
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
        /* האזנה לחיישן תאוצה */
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;  /* החסרת כוח הכבידה לשיפור הדיוק */

                if (acceleration > SHAKE_THRESHOLD) {
                    if (!isShaking) {
                        isShaking = true;
                        handler.postDelayed(checkShakeRunnable, SHAKE_DURATION);
                    }
                } else {
                    isShaking = false;
                    handler.removeCallbacks(checkShakeRunnable);
                }
            }

            @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final Runnable checkShakeRunnable = () -> {
        if (isShaking) {
            goToFirstFragment();  /* מעבר לפרגמנט המשחק */
        }
    };

    private void goToFirstFragment() {
        FirstFragment firstFragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("betAmount", betAmount);  /* שליחת סכום ההימור */
        firstFragment.setArguments(args);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, firstFragment)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        /* ביטול רישום החיישן */
        sensorManager.unregisterListener(sensorEventListener);
        handler.removeCallbacks(checkShakeRunnable);
    }
}







