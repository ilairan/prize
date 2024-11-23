package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Login_activity extends AppCompatActivity {

    TextView login_button;
    EditText email_login;
    EditText password_login;
    TextView register_login;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get ids
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        register_login = findViewById(R.id.register_login);
        login_button = findViewById(R.id.login_button);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // OnClickListener for login_button
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // After click on login
                String email = email_login.getText().toString();
                String password = password_login.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login_activity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login_activity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean found = false;

                List<User> users = dbHelper.getAllUsers();
                for (User user : users) {
                    if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Toast.makeText(Login_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    // Navigate to the next activity or perform actions upon successful login
                    Intent intent = new Intent(Login_activity.this, MainActivity.class); // Adjust as needed
                    startActivity(intent);
                } else {
                    Toast.makeText(Login_activity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // OnClickListener for register_login
        register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Moving to Register_activity
                Intent intent = new Intent(Login_activity.this, Register_activity.class);
                startActivity(intent);
            }
        });
    }
}
