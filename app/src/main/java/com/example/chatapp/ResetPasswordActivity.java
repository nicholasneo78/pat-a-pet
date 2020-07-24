package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity when the "Reset Password" is pressed
 * Uses Firebase auth to reset user's password
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */
public class ResetPasswordActivity extends AppCompatActivity {

    /**
     * The text-field for user to input their email
     */
    EditText send_email;

    /**
     * The reset button
     */
    Button btn_reset;

    FirebaseAuth firebaseAuth;

    /**
     * Creates the ResetPassword UI
     * <ol>
     *     <li>Sends a reset-password email to the specified email</li>
     *     <li>Returns the user to login once successful</li>
     * </ol>
     * @param savedInstanceState Contains the data that it is most recently applied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick event when the reset button is clicked
             * <ol>
             *     <li>Sends the user a reset-password email courtesy of firebaseAuth</li>
             *     <li>Checks for completion of the task before sending the user back to login</li>
             * </ol>
             * @param v The current view
             */
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        /**
                         * If the method is successful, goes back to Login
                         * Else, prints an error message
                         * @param task The task that is supposed to complete
                         */
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
