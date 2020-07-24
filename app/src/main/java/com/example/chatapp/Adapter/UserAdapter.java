package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.ProfileActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

/**
 * Adapter to display the users
 * This adapter allows users stored in the database to be displayed on RecyclerView
 * @author Aleem
 * @author Viet
 * @author Riki
 * @author Aavan
 * @author Nicholas
 * @version 2.0
 * @since 1.0
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /**
     * The current context of the adapter
     */
    private Context mContext;

    /**
     * A list of users to be displayed
     */
    private List<User> mUsers;

    /**
     * A boolean to check if user is online
     */
    private boolean ischat;

    private String theLastMessage;

    /**
     * Creates a UserAdapter with the specified context, users, ischat
     * @param mContext The current context
     * @param mUsers A list of users pulled from the Database
     * @param ischat Checks if the adapter is displaying on "chat" or "user" tab
     */
    public UserAdapter(Context mContext, List<User> mUsers, boolean ischat) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    /**
     * Creates a viewHolder of the parent & viewType
     * Sets the layout of the view to R.layout.user_item
     * @param parent The parent of the views
     * @param viewType The type of layout chosen
     * @return a new ViewHolder for RecyclerView
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    /**
     * Overrides onBindViewHolder of RecyclerView to display the users
     * Also does the following:
     * <ol>
     *     <li>Display the username to layout</li>
     *     <li>Display the image of the user</li>
     *     <li>Displays a green icon if user is online</li>
     *     <li>Displays the user's profile if clicked in "caretakers" tab</li>
     *     <li>Displays the Chat if clicked in "chats" tab</li>
     * </ol>
     * @param holder The current ViewHolder
     * @param position The index of the item from RecyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat) {
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if (ischat) {
            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ischat) {
                    Intent intent = new Intent(mContext, MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    mContext.startActivity(intent);
                }

                else {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.putExtra("userid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    /**
     * Gets the size of the user array
     * @return Returns the size of users
     */
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * ViewHolder class to display on RecyclerView
     * Stores each view in the layout to a variable
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_off = itemView.findViewById(R.id.img_off);
            img_on = itemView.findViewById(R.id.img_on);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    /**
     * Checks for last message. If have, displays it
     * @param userid userID received from FirebaseDatabase
     * @param last_msg the last message received from FirebaseDatabase
     */
    //last message check
    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            /**
             * Updates the lastMessage if there's a new message updated in the database
             * @param dataSnapshot A child node of "Chats" in the database
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    if ((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))) {
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
