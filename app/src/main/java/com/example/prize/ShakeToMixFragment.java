package com.example.prize;

import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ShakeToMixFragment extends Fragment {

    private static final double SHAKE_THRESHOLD = 10.0;
    private static final int SHAKE_DURATION = 1000; // 2 seconds
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;
    private Handler handler;
    private boolean isShaking;
    private TextView shakeMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shake_to_mix, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shakeMessage = view.findViewById(R.id.shake_message);
        shakeMessage.setText("SHAKE TO SHUFFLE");

        // Animate the text size
        animateTextSize();

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new Handler();
        isShaking = false;

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z);
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

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Not needed for this example
            }
        };

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void animateTextSize() {
        ValueAnimator animator = ValueAnimator.ofFloat(140f, 180f);
        animator.setDuration(1000); // 1 second duration for each cycle
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                shakeMessage.setTextSize(animatedValue);
            }
        });
        animator.start();
    }

    private final Runnable checkShakeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShaking) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new FirstFragment())
                        .commit();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        handler.removeCallbacks(checkShakeRunnable);
    }
}


