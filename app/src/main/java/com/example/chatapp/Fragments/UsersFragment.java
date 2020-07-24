package com.example.chatapp.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment class that displays the users stored in the database
 * <ol>
 *     <li>Sorts the users to display only caretakers</li>
 *     <li>Users are able to search among the users</li>
 * </ol>
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */
public class UsersFragment extends Fragment {

    /**
     * The recyclerView context
     */
    private RecyclerView recyclerView;

    /**
     * A variable to create an Adapter suited to the fragment
     */
    private UserAdapter userAdapter;

    /**
     * A list of users from the database
     */
    private List<User> mUsers;

    /**
     * A variable to store the view of the EditText
     */
    private EditText search_users;

    /**
     * Called to have the fragment instantiate its user interface view
     * <ol>
     *     <li>Reads and displays the users</li>
     *     <li>Updates the view according to the search</li>
     * </ol>
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment UI should be attached to
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state
     * @return Return the View for User Fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_users, container, false);

       recyclerView = view.findViewById(R.id.recycler_view);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

       mUsers = new ArrayList<>();

       readUsers();

       search_users = view.findViewById(R.id.search_users);
       search_users.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

           }

           /**
            * When there's a text change to the search, search & displays the user(s)
            * @param charSequence The current charSequence input on the search
            */
           @Override
           public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchUsers(charSequence.toString().toLowerCase());
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });

       return view;
    }

    /**
     * Search through users in the database and updates the RecyclerView
     * @param s The search text
     */
    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            /**
             * Checks if user is a caretaker & the it's not the current user
             * Displays the list of users based on the search
             * @param dataSnapshot The child node from the reference
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;
                    if (user.getIsCaretaker().equals("true") && !user.getId().equals(fuser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Displays the caretakers on the RecyclerView
     */
    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

            reference.addValueEventListener(new ValueEventListener() {
                /**
                 * Checks if user is a caretaker & the it's not the current user
                 * Display all the caretakers from the database
                 * @param dataSnapshot The child node from the reference
                 */
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (search_users.getText().toString().equals("")) {
                        mUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);

                            assert user != null;
                            assert firebaseUser != null;
                            if (user.getIsCaretaker().equals("true") && !user.getId().equals(firebaseUser.getUid())) {
                                mUsers.add(user);
                            }
                        }

                        userAdapter = new UserAdapter(getContext(), mUsers, false);
                        recyclerView.setAdapter(userAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
