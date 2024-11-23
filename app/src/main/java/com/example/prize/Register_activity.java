package com.example.prize;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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
    DBHelper DB;
    SQLiteDatabase db;
    boolean trust = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find ids
        full_name = findViewById(R.id.fullname_register);
        email = findViewById(R.id.email_register);
        phone_num = findViewById(R.id.phone_register);
        password = findViewById(R.id.password_register);
        repeat_password = findViewById(R.id.password_repeat);
        register_button = findViewById(R.id.register_button);
        back_register = findViewById(R.id.back_register);
        advance_button = findViewById(R.id.advance_buttom);

        DB = new DBHelper(this);

        // Temporary button
        advance_button.setOnClickListener(v -> {
            Intent intent = new Intent(Register_activity.this, Activity_listview.class);
            startActivity(intent);
        });

        // Registration button
        register_button.setOnClickListener(v -> registerUser());

        // Back button
        back_register.setOnClickListener(v -> {
            Intent intent = new Intent(Register_activity.this, Login_activity.class);
            startActivity(intent);
        });


    }

    private void registerUser() {
        trust = true;

        if (TextUtils.isEmpty(full_name.getText().toString())) {
            full_name.setError("You must fill your name");
            trust = false;
        }

        if (TextUtils.isEmpty(repeat_password.getText().toString())) {
            repeat_password.setError("You must repeat the password");
            trust = false;
        }

        if (TextUtils.isEmpty(phone_num.getText().toString())) {
            phone_num.setError("You must fill your phone number");
            trust = false;
        } else if (!TextUtils.isDigitsOnly(phone_num.getText().toString())) {
            phone_num.setError("Invalid phone number");
            trust = false;
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("You must fill the password");
            trust = false;
        } else if (!password.getText().toString().equals(repeat_password.getText().toString())) {
            password.setError("Passwords are not identical");
            password.setText("");
            repeat_password.setError("Passwords are not identical");
            repeat_password.setText("");
            trust = false;
        } else if (password.getText().toString().length() < 8) {
            password.setError("Password must be at least 8 characters");
            password.setText("");
            repeat_password.setText("");
            trust = false;
        }

        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("You must fill your email");
            trust = false;
        } else if (!EmailUtils.isValidEmail(email.getText().toString())) {
            email.setError("Invalid email address");
            email.setText("");
            trust = false;
        }

        if (trust) {
            User user = new User(
                    full_name.getText().toString(),
                    email.getText().toString(),
                    phone_num.getText().toString(),
                    password.getText().toString()
            );

            boolean checkInsertData = DB.insertUserdata(user);
            Intent intent = new Intent(Register_activity.this, Login_activity.class);
            if (checkInsertData) {
                Toast.makeText(Register_activity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            } else {
                Toast.makeText(Register_activity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
            startActivity(intent);
        }
    }

}

