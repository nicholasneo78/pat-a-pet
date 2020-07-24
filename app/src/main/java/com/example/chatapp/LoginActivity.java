package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Activity when the Login button is pressed
 * This Activity allows the user who already registers an account to log into the app, using Firebase for authentication.
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */

public class LoginActivity extends AppCompatActivity {
    
    /**
    * The Text Field for email and password 
    */
    MaterialEditText email, password; 

    /** 
    * Login button 
    */
    Button btn_login;

    /**
    * Instance of Firebase for authentication
    */
    FirebaseAuth auth;

    /**
    * For user to click on in case they failed to login
    */
    TextView forgot_password;

   
    /**
    * Called when the activity is starting
    * This function set up the appropriate listeners for each listeners (btn_login and forgot_password)
    *@param savedInstanceState If the activity is being re-initialized after previously being shut
    *                            down then this Bundle contains the data it most recently supplied
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Instatiate Firebase */
        auth = FirebaseAuth.getInstance();

        /*Store each views in the layout to a variable */
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);

        /**
        * The forgot_password listener does the following:
        *<ol>
        *<li>Start the ResetPasswordActivity</li>
        *</ol>
        */
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        /**
        * The btn_login listener does the following:
        *<ol>
        *<li>Check for empty entries using TextUtils</li>
        *<li>If all fields are filled, send the credentials to Firebase by invoking Firebase instance's signInWithEmailAndPassword()</li>
        *<li>Listen for authentication result using onCompleteListener</li>
        *<li>If authentication is successful, start Main Activity</li>
        *<li>Else display err message via Toast</li>
        *</ol>         
         */
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }) ;
    }


}
