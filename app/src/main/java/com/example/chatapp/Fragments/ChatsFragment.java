package com.example.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.Chatlist;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Token;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment class that displays the chats stored in the database
 * Controls what to be displayed on the "Chats" tab
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */
public class ChatsFragment extends Fragment {

    /**
     * The recyclerView context
     */
    private RecyclerView recyclerView;

    /**
     * A variable to store a UserAdapter
     */
    private UserAdapter userAdapter;

    /**
     * A list of users received from Database
     */
    private List<User> mUsers;

    /**
     * The current authenticated user
     */
    private FirebaseUser fuser;

    /**
     * A reference of the Database in Firebase
     */
    private DatabaseReference reference;

    /**
     * A list of chatlist of a user
     */
    private List<Chatlist> usersList;

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment UI should be attached to
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state
     * @return Return the View for Chat Fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersList = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (fuser != null) {
            reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                /**
                 * Adds/Update existing & new Chatlist for a user to usersList
                 * @param dataSnapshot Child node of the Database from the reference
                 */
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    usersList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        usersList.add(chatlist);
                    }

                    chatList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            updateToken(FirebaseInstanceId.getInstance().getToken());
        }
        return view;

    }

    /**
     * Updates the notification token if received
     * @param token Updates the token stored in Database with this given token
     */
    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    /**
     * The function does the following:
     * <ol>
     *     <li>Adds the sender to mUsers list</li>
     *     <li>Creates an adapter to display the senders</li>
     * </ol>
     */
    //update chatList
    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Adds/Updates the users whom the current user has chatted with
             * @param dataSnapshot The child node of parent "Users" in the database
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList) {
                        if (user.getId().equals(chatlist.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

