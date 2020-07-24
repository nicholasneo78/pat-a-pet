package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The starting activity when the application is opened
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */
public class StartActivity extends AppCompatActivity {

    /**
     * Buttons for login and register
     */
    Button login, register;

    /**
     * The current user
     */
    FirebaseUser firebaseUser;

    /**
     * If there is a previous login, it will display straight to main activity
     * Otherwise, it'll prompt the user to either login or register
     */
    @Override
    protected void onStart() {
        super.onStart();
        //setContentView(R.layout.activity_start);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //checks for null users
        if (firebaseUser != null) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Creates the starting UI for the application
     * @param savedInstanceState Contains the data that it is most recently applied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts the LoginActivity if the login button is clicked
             * @param view The current view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts the RegisterActivity if the register button is clicked
             * @param view The current view
             */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }
}
