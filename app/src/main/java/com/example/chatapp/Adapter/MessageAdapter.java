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
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Adapter to display the messages
 * This adapter allows messages stored in the database to be displayed on RecyclerView
 * @author Aleem
 * @author Viet
 * @author Riki
 * @author Aavan
 * @author Nicholas
 * @version 2.0
 * @since 1.0
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    /**
     * Constants to determine if message is to be displayed on left or right
     */
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    /**
     * The current context of the adapter
     */
    private Context mContext;

    /**
     * A list of chats to be displayed
     */
    private List<Chat> mChat;

    /**
     * A url to display the sender's image on the Chat View
     */
    private String imageurl;

    /**
     * Creates a MessageAdapter object with the specified context, chats and image.
     * @param mContext The current context of the application
     * @param mChat A list of chats from the Database
     * @param imageurl A URL for the specific profile
     */
    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    /**
     * Overrides RecyclerView ViewHolder to contain the current parameters
     * Differentiates the view to different layout depending on the viewType:
     * <ol>
     *     <li>If receiver's chat, sets the layout to display right</li>
     *     <li>If sender's chat, sets the layout to display left</li>
     * </ol>
     * @param parent The parent class
     * @param viewType An integer to determine if the chat is from receiver or sender
     * @return The ViewHolder specific to the MessageAdapter
     */
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    /**
     * Overrides onBindViewHolder of RecyclerView to display the messages
     * Also does the following:
     * <ol>
     *     <li>Display the sender's image with the chat</li>
     *     <li>Checks the status of the last text (if it has been read)</li>
     * </ol>
     * @param holder The current ViewHolder
     * @param position The index of the message from mChat
     */
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if (imageurl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size()-1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    /**
     * Gets the size of the chat list
     * @return Size of mChat
     */
    @Override
    public int getItemCount() {
        return mChat.size();
    }

    /**
     * ViewHolder class to display on RecyclerView
     * Stores each views in the layout to a variable
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageView profile_image;

        public TextView txt_seen;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    /**
     * Differentiates the chats:
     * <ol>
     *     <li>If receiver, returns display right</li>
     *     <li>If sender, returns display left</li>
     * </ol>
     * @param position Index of the chat
     * @return Either display the message on the left or right
     */
    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

