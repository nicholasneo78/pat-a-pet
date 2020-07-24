package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

/**
 * Activity when the Register button is pressed
 * This activity allows users to register as a caretaker or petOwner
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Text fields for users to input
     */
    MaterialEditText username, email, password, location;
    EditText about_me, experience, why_caretaker;

    /**
     * Buttons for users to press
     * Register and RadioButton
     */
    Button btn_register;
    RadioButton owner_button;

    /**
     * Checks if user chose caretaker option
     * Default: User is an owner
     */
    Boolean caretaker = false;

    /**
     * TextView variables to display headings
     */
    TextView text_experience, text_why_caretaker;

    /**
     * Instance of Firebase for authentication
     */
    FirebaseAuth auth;

    /**
     * Reference of the Firebase Database
     */
    DatabaseReference reference;

    /**
     * Called when activity is starting
     * Sets up the layout with the various text-fields
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                             down then this Bundle contains the data it most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Initialize variables to layout*/
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        location = findViewById(R.id.location);
        btn_register = findViewById(R.id.btn_register);
        about_me = findViewById(R.id.about_me);
        experience = findViewById(R.id.experience);
        why_caretaker = findViewById(R.id.why_caretaker);
        text_experience = findViewById(R.id.text_experience);
        text_why_caretaker = findViewById(R.id.text_why_caretaker);
        owner_button = findViewById(R.id.owner);

        //checks the caretaker button
        owner_button.setChecked(true);

        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            /**
             * The listener does the following:
             * <ol>
             *     <li>Checks if any fields are empty</li>
             *     <li>Checks that password size is bigger than 6 characters</li>
             *     <li>If the conditions are met, runs the register function</li>
             * </ol>
             * @param view The current view of the activity
             */
            @Override
            public void onClick(View view) {
                String txt_username = Objects.requireNonNull(username.getText()).toString();
                String txt_email = Objects.requireNonNull(email.getText()).toString();
                String txt_password = Objects.requireNonNull(password.getText()).toString();
                String txt_location = Objects.requireNonNull(location.getText()).toString();
                String txt_aboutme = about_me.getText().toString();
                String txt_experience = experience.getText().toString();
                String txt_why_caretaker = why_caretaker.getText().toString();
                System.out.println("On click pressed - Register");

                if (caretaker) {
                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) ||
                            TextUtils.isEmpty(txt_location)) {
                        Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else if (txt_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        register(txt_username, txt_email, txt_password, txt_aboutme, txt_experience, txt_why_caretaker, txt_location);
                    }
                }

                else {
                    if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) ||
                            TextUtils.isEmpty(txt_location)) {
                        Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else if (txt_password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    } else {
                        register(txt_username, txt_email, txt_password, txt_aboutme, txt_experience, txt_why_caretaker, txt_location);
                    }
                }
            }
        });
    }

    /**
     * When called:
     * <ol>
     *     <li>Stores in Firebase Database</li>
     *     <li>Checks if user is a caretaker or pet owner and stores it</li>
     *     <li>Checks for completion of task before proceeding to the main activity</li>
     * </ol>
     * @param username The username applied by the user
     * @param email The email used by the user
     * @param password The password by the user
     */
    private void register(final String username, String email, String password, final String aboutMe,
                          final String experience, final String whyCaretaker, final String location) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    /**
                     * Checks if creating the database is successful
                     * If successful, will bring the user to the MainActivity
                     * @param task The current given task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("Go into register class");
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            System.out.println("Go into reference class");
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());
                            hashMap.put("aboutme", aboutMe);
                            hashMap.put("experience", experience);
                            hashMap.put("whyCaretaker", whyCaretaker);
                            hashMap.put("isCaretaker", caretaker.toString());
                            hashMap.put("location", location);

                            System.out.println("Loaded data");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                /**
                                 * Once task is successfully completed, activity will end
                                 * The user is then brought to the main activity
                                 * @param task The current task applied
                                 */
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT ).show();
                        }
                    }
                });

    }

    /**
     * Checks if either the caretaker or pet owner button is clicked
     * Pet owners will not need to put extra information like:
     * <ol>
     *     <li>Caretakers has to input experience</li>
     *     <li>Caretakers has to input reason for being a caretaker</li>
     * </ol>
     * @param view The current view of the RadioButton
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.caretaker:
                if (checked) {
                    caretaker = true;
                    text_experience.setVisibility(View.VISIBLE);
                    experience.setVisibility(View.VISIBLE);
                    text_why_caretaker.setVisibility(View.VISIBLE);
                    why_caretaker.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.owner:
                if (checked) {
                    caretaker = false;
                    text_experience.setVisibility(View.GONE);
                    experience.setVisibility(View.GONE);
                    text_why_caretaker.setVisibility(View.GONE);
                    why_caretaker.setVisibility(View.GONE);
                }
                break;
        }
    }
}
