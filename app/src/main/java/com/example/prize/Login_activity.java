package com.example.prize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Login_activity extends AppCompatActivity {

    TextView login_button;
    EditText email_login;
    EditText password_login;
    TextView register_login;
    public Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //get ids
       email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        register_login = findViewById(R.id.register_login);
        login_button = findViewById(R.id.login_button);





        //onclicklistener for Login_button
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //after click on login
                String email = email_login.getText().toString();
                String password = password_login.getText().toString();

            }
        });

        //onclicklistener for Register_button.
        register_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moving to Register_activity
                Intent Intent = new Intent(Login_activity.this, Register_activity.class);
                startActivity(Intent);

            }
        });



    }
}