package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Activity when a caretaker profile is clicked
 * This activity allows users to find more information about a particular Caretaker
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * A variable to hold the profile_image layout on the toolbar
     */
    CircleImageView profile_image;

    /**
     * Textfields for users to input
     */
    TextView profile_username, username, aboutme, experience, why_caretaker, txt_experience, txt_why_caretaker;

    /**
     * A variable to hold the profile image on the page
     */
    ImageView profileimage;

    /**
     * A reference to the Firebase Database
     */
    DatabaseReference reference;

    /**
     * Any additional information passed to the intent
     */
    Intent intent;

    /**
     * The userid passed through by the intent
     */
    String userid;

    /**
     * Called when activity is starting
     * Sets up the layout with various fields:
     * <ol>
     *     <li>Setups the toolbar with username & image</li>
     *     <li>Creates the layout of the caretaker chosen</li>
     *     <li>Allows user to chat with the caretaker</li>
     * </ol>
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /**
             * Goes back to Main if the back button is clicked
             * @param v The current view
             */
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        profile_image = findViewById(R.id.profile_image);
        profile_username = findViewById(R.id.profile_username);
        username = findViewById(R.id.username);
        aboutme = findViewById(R.id.about_me);
        experience = findViewById(R.id.experience);
        txt_experience = findViewById(R.id.txt_experience);
        why_caretaker = findViewById(R.id.why_caretaker);
        txt_why_caretaker = findViewById(R.id.txt_why_caretaker);
        profileimage = findViewById(R.id.profileimage);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Checks for any data changes & updates the UI
             * <ol>
             *     <li>Caretakers will have more information displayed for their profile</li>
             *     <li>Owners layout will be similar to their profile fragment counterpart</li>
             * </ol>
             * @param dataSnapshot An instance containing data from Firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                profile_username.setText(user.getUsername());
                username.setText(user.getUsername());
                aboutme.setText(user.getAboutMe());

                if (user.getIsCaretaker().equals("true")) {
                    txt_experience.setVisibility(View.VISIBLE);
                    txt_why_caretaker.setVisibility(View.VISIBLE);
                    experience.setVisibility(View.VISIBLE);
                    why_caretaker.setVisibility(View.VISIBLE);
                    experience.setText(user.getExperience());
                    why_caretaker.setText(user.getWhyCaretaker());
                }

                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                    profileimage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileimage);
                }

                //readMessages(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * If users clicked the chat icon:
     * <ol>
     *     <li>The MessageActivity will trigger</li>
     *     <li>The userid of the person that user is trying to chat will be passed through the intent</li>
     * </ol>
     * @param view The current view of the activity
     */
    public void onChatButtonClicked(View view) {
        Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
}
