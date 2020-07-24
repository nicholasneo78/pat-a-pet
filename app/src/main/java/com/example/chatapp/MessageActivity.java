package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity when a specified chat conversation is clicked
 * Controls all the chat messages to be displayed &
 * allows users to view the profile of the person they're chatting
 * @author Aleem
 * @version 1.0
 * @since 1.0
 */
public class MessageActivity extends AppCompatActivity {

    /**
     * A variable to hold the profile image layout
     */
    CircleImageView profile_image;

    /**
     * Textfield to display the username
     */
    TextView username;

    /**
     * The current authenticated user
     */
    FirebaseUser fuser;

    /**
     * A reference to the Firebase Database
     */
    DatabaseReference reference;

    /**
     * The button for user to send the message
     */
    ImageButton btn_send;

    /**
     * Text field for user to input and send
     */
    EditText text_send;

    /**
     * The adapter to display the messages on recycler view
     */
    MessageAdapter messageAdapter;

    /**
     * The list chats
     */
    List<Chat> mchat;

    /**
     * The recycler view to display the chat
     */
    RecyclerView recyclerView;

    /**
     * The current intent with tagged with other information
     */
    Intent intent;

    /**
     * A listener to check if receiver has seen the message
     */
    ValueEventListener seenListener;

    /**
     * The id of the user who's chatting with
     */
    String userid;

    /**
     * API service for notification token
     */
    APIService apiService;

    /**
     * A boolean to check if notification has been sent
     */
    boolean notify = false;


    /**
     * Called when the activity is starting
     * Creates the conversation UI
     * <ol>
     *     <li>Consists of the toolbar</li>
     *     <li>Displays the conversations with the user</li>
     *     <li>Allows user to send message</li>
     * </ol>
     * @param savedInstanceState Contains the data that it is most recently applied
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

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
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            /**
             * Sends the message when the send button is clicked
             * Ensures that there is a message being sent (No empty text fields)
             * Sets the textbox back to empty once sent
             * @param v The current view
             */
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if(!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Checks for any data changes & updates the UI
             * Reads the chats in the database & displays it to recyclerview
             * @param dataSnapshot An instance containing data from Firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessages(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    /**
     * Checks if the receiver has seen the message
     * Adds a listener to:
     * <ol>
     *     <li>Checks that the user has seen the message</li>
     *     <li>Double checks the id of the receiver</li>
     *     <li>Displays seen on the message</li>
     *     <li>Updates the database</li>
     * </ol>
     * @param userid The id of the sender
     */
    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * A method to allow the sending of message
     * <ol>
     *     <li>Saves the message in a hashmap</li>
     *     <li>Stores the message in the Database</li>
     * </ol>
     * @param sender The id of the sender
     * @param receiver The id of the receiver
     * @param message The message sent
     */
    private void sendMessage(String sender, final String receiver, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fuser.getUid())
                .child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Sets the id to "id" child if the data does not exist
             * @param dataSnapshot The referenced child node
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //bug fix for the no chatlist
        final DatabaseReference chatReceiverRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiver)
                .child(fuser.getUid());

        chatReceiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Sets the id to "id" child if the data does not exist
             * @param dataSnapshot The referenced child node
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatReceiverRef.child("id").setValue(fuser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Sends a notification if a message is sent
             * @param dataSnapshot An instance of data from Firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * To send a notification to the receiver
     * Gets the token of the receiver and sends a query
     * @param receiver The receiver id
     * @param username The username of the sender
     * @param message The message sent
     */
    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            /**
             * Sends the notification to sender when a new message is sent
             * @param dataSnapshot An instance of data from Firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                /**
                                 * Checks if sending of Notification is successful
                                 * @param call The notification call
                                 * @param response The response received
                                 */
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * When called, the function does the following:
     * <ol>
     *     <li>Read any conversation messages stored in database</li>
     *     <li>Using the message adapter, display it on Recycler View</li>
     * </ol>
     * @param myid The current user id
     * @param userid The id of the sender
     * @param imageurl The image URL of the receiver
     */
    private void readMessages(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Reads any chats in the database
             * Displays it using the adapter
             * @param dataSnapshot The child node of the referenced Database
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Stringify the username of the current user
     * @param userid The userid of the current user
     */
    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    /**
     * Updates the status of the user to the database
     * @param status The current status of the user
     */
    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    /**
     * Sets status to online if user is on the application
     */
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    /**
     * Sets the status to offline is user exits or pauses the application
     * Also sets the currentUser to none
     */
    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    /**
     * When the profile on the toolbar is clicked,
     * brings up the profile of that user through ProfileActivity
     * @param view The current view of the activity
     */
    public void onProfileClicked(View view) {
        Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
        intent.putExtra("userid", userid);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
