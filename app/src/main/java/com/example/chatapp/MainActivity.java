package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.Fragments.ProfileFragment;
import com.example.chatapp.Fragments.UsersFragment;
import com.example.chatapp.Fragments.VetFragment;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The MainActivity after login
 * <ol>
 *     <li>Holds all the fragments of the application</li>
 *     <li>Determines which fragments to be shown for which user</li>
 *     <li>Requests for GPS from the user</li>
 *     <li></li>
 * </ol>
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Image View to display the profile image
     */
    CircleImageView profile_image;

    /**
     * Text field to display username
     */
    TextView username;

    /**
     * The current authenticated user
     */
    FirebaseUser firebaseUser;

    /**
     * A reference to the database
     */
    DatabaseReference reference;

    /**
     * Authentication referenced from FirebaseAuth
     */
    FirebaseAuth mAuth;

    /**
     * Listens for any changes to authentication
     */
    FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * Constants for Request ID
     */
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 2;

    /**
     * A boolean to check if the current user is a caretaker
     */
    boolean isCaretaker;

    /**
     * Variables to check for unread messages & the index of the viewpager
     */
    int unread, tempItem = 0;

    /**
     * Called after onCreate()
     * Also does the following:
     * <ol>
     *     <li>Asks user's permission ot access location</li>
     *     <li>Adds a listener to the authentication for any changes</li>
     * </ol>
     */
    @Override
    protected void onStart() {
        super.onStart();
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION))) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Called when the activity is called
     * Creates the Main UI
     * <ol>
     *     <li>Creates the relevant tabs according to the user</li>
     *     <li>Updates the tab when there are changes to the database</li>
     *     <li>The number of unread messages will be displayed on the chat tab</li>
     * </ol>
     * @param savedInstanceState Contains the data it is most recently supplied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            /**
             * When there's a change in the authentication:
             * <ol>
             *     <li>Ensures that user is not 'null'</li>
             *     <li>Else, returns the user back to the login page</li>
             * </ol>
             * @param firebaseAuth A reference to the Firebase Authentication
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    firebaseUser = user;
                    String userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    if (reference.child("isCaretaker").getKey().equals("true")) {
                        reference.child("isCaretaker").setValue("true");
                        isCaretaker = true;
                    } else if (reference.child("isCaretaker").getKey().equals("false")){
                        reference.child("isCaretaker").setValue("false");
                        isCaretaker = false;
                    }

                    reference.addValueEventListener(new ValueEventListener() {
                        /**
                         * If there is datachange, set accordingly
                         * @param dataSnapshot Contains data from Firebase
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);

                            assert user != null;
                            username.setText(user.getUsername());

                            if (user.getIsCaretaker().equals("false")) {
                                isCaretaker = false;
                            } else if (user.getIsCaretaker().equals("true")){
                                isCaretaker = true;
                            }
                            if (user.getImageURL().equals("default")) {
                                profile_image.setImageResource(R.mipmap.ic_launcher);
                            } else {
                                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    final TabLayout tabLayout = findViewById(R.id.tab_layout);
                    final ViewPager viewPager = findViewById(R.id.view_pager);

                    reference = FirebaseDatabase.getInstance().getReference("Chats");
                    reference.addValueEventListener(new ValueEventListener() {
                        /**
                         * Updates the screen anytime there's a data change to the database
                         * @param dataSnapshot Contains data from Firebase
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            unread = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Chat chat = snapshot.getValue(Chat.class);
                                if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()) {
                                    unread++;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    reference = FirebaseDatabase.getInstance().getReference();
                    reference.addValueEventListener(new ValueEventListener() {
                        /**
                         * Updates the fragment with any changes to the database
                         * <ol>
                         *     <li>Displays Chat, Vet and Profile tab for caretakers</li>
                         *     <li>Displays Chat, Caretakers, Vet and Profile tab for owners</li>
                         * </ol>
                         * @param dataSnapshot The child node of the referenced Database
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                            if (unread == 0) {
                                viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                            } else {
                                viewPagerAdapter.addFragment(new ChatsFragment(), "(" + unread + ") Chats");
                            }


                            //if user is owner, display users fragment

                            if (!isCaretaker) {
                                viewPagerAdapter.addFragment(new UsersFragment(), "Caretakers");
                            }
                            viewPagerAdapter.addFragment(new VetFragment(), "Vets");
                            viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

                            if (!isCaretaker) {
                                viewPager.setAdapter(viewPagerAdapter);
                                viewPager.setCurrentItem(tempItem);
                                tabLayout.setupWithViewPager(viewPager);
                                tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_chat);
                                tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_users);
                                tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_vet);
                                tabLayout.getTabAt(3).setIcon(R.drawable.ic_tab_profile);
                                tempItem = viewPager.getCurrentItem();
                            }
                            else {
                                viewPager.setAdapter(viewPagerAdapter);
                                viewPager.setCurrentItem(tempItem);
                                tabLayout.setupWithViewPager(viewPager);
                                tabLayout.getTabAt(0).setIcon(R.drawable.ic_tab_chat);
                                tabLayout.getTabAt(1).setIcon(R.drawable.ic_tab_vet);
                                tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_profile);
                                tempItem = viewPager.getCurrentItem();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    //mAuth.signOut();
                    finish();
                    startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        };
    }



    /**
     * Creates an option menu
     * @param menu The specified menu to apply
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Checks if an item from the menu is selected
     * <ol>
     *     <li>Logout: Signouts from FirebaseAuth</li>
     * </ol>
     * @param item The menuitem that's created from menu
     * @return true if selected; false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                //testing offline/online status
                FirebaseAuth.getInstance().signOut();
                //finish();
                return true;
        }

        return false;
    }

    /**
     * An adapter to create a fragment to display
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        /**
         * Creates an adapter with the specified FragmentManager
         * @param fa The FragmentManager
         */
        ViewPagerAdapter(FragmentManager fa) {
            super(fa);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        /**
         * Gets the item from the specified position
         * @param position The index of the fragment
         * @return The specified fragment
         */
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        /**
         * Gets the size of the fragment array
         * @return An integer size
         */
        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * Adds a fragment to the arraylist
         * @param fragment The specified fragment
         * @param title The title of the fragment
         */
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        /**
         * Converts the String title to a CharSequence
         * @param position The index of the fragment
         * @return The converted String title
         */
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    /**
     * Checks The status of the user
     * @param status The specified status applied
     */
    private void status(String status) {
        if (firebaseUser != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);

            reference.updateChildren(hashMap);
        }
    }

    /**
     * Changes the status of the user to online when app is opened
     */
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    /**
     * Changes the status of the user to offline when app is closed or paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
