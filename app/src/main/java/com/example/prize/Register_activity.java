package com.example.prize;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_activity extends AppCompatActivity {

    private CountDownTimer timer;
     TextView timer_view;
     EditText full_name;
     EditText email;
     EditText phone_num;
     EditText password;
     EditText repeat_password;
     TextView advance_button;

     TextView register_button;
     TextView back_register;
    ArrayList <User> user_details = new ArrayList<>();
    boolean trust = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find ids
        timer_view = findViewById(R.id.timer_view);
        full_name = findViewById(R.id.fullname_register);
        email = findViewById(R.id.email_register);
        phone_num = findViewById(R.id.phone_register);
        password = findViewById(R.id.password_register);
        repeat_password = findViewById(R.id.password_repeat);
        register_button = findViewById(R.id.register_button);
        back_register = findViewById(R.id.back_register);
        advance_button = findViewById(R.id.advance_buttom);


        //temporary
        advance_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register_activity.this, Activity_listview.class);
                startActivity(intent);
            }
        });





        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user register details check
                //check if password and repeat password are identical
                if (TextUtils.isEmpty(full_name.getText().toString())){
                    full_name.setError("You must fill your name");
                }

                if(TextUtils.isEmpty(repeat_password.getText().toString())) {
                    repeat_password.setError("You must repeat the password");
                    trust = false;
                }
                if(TextUtils.isEmpty(phone_num.getText().toString())) {
                    phone_num.setError("You must fill your phone number");
                    trust = false;
                } else if(!(TextUtils.isDigitsOnly(phone_num.getText().toString()))) {
                    phone_num.setError("invalid phone number");
                    trust = false;

                }
                if(TextUtils.isEmpty(password.getText().toString())){
                   password.setError("You must fill the password");
                    trust = false;

                }
                 else if (!(password.getText().toString().equals(repeat_password.getText().toString()))) {
                    password.setError("Passwords are not identical"); password.setText("");
                    repeat_password.setError("Passwords are not identical"); repeat_password.setText("");
                    trust = false;
                }
                 else if(!(password.getText().toString().length() > 7)){
                    password.setError("Password must be at least 8 characters"); password.setText("");
                    repeat_password.setText("");
                     trust = false;
                 }
                if(TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("You must fill your email");
                    trust = false;
                }
                 else if(!(EmailUtils.isValidEmail(email.getText().toString()))){
                    email.setError("Invalid email address");
                    email.setText("");
                     trust = false;
                 }
                if(trust) {
                    //fill the user data
                    user_details.add(new User(full_name.getText().toString(), email.getText().toString(),phone_num.getText().toString(), password.getText().toString()));
                    Intent intent = new Intent(Register_activity.this, Login_activity.class);
                    startActivity(intent);

                }















            }
        });
        back_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register_activity.this, Login_activity.class);
                startActivity(intent);

            }
        });




        // count down timer setup
        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update UI with remaining seconds
                long secondsRemaining = millisUntilFinished / 1000;
                timer_view.setText(""+ secondsRemaining);
            }

            @Override
            public void onFinish() {
                // Countdown completed, finish the activity
                finish();
                Intent intent = new Intent(Register_activity.this, Login_activity.class);
                startActivity(intent);
            }
        };

        // Start the countdown timer
        timer.start();
    }

    //reset timer if something touches the background
    public void onBackgrounwdClick(View view) {
        timer.cancel();
        timer.start();





    }




        @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer
        if (timer != null) {
            timer.cancel();
        }
    }
}
