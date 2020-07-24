package com.example.chatapp.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment class that displays the user's profile stored in the database
 * Allows user to change their profile pictures
 * Allows user to change their descriptions (Not implemented yet)
 * @author Aleem
 * @version 2.0
 * @since 1.0
 */
public class ProfileFragment extends Fragment {

    /**
     * Variable to store CircleImageView
     */
    private CircleImageView image_profile;

    /**
     * Variables to store the views from TextView
     */
    private TextView username, location, about_me, txt_experience, experience, txt_why_caretaker, why_caretaker;

    /**
     * A reference to the Database
     */
    private DatabaseReference reference;

    /**
     * The current authenticated user
     */
    private FirebaseUser fuser;

    /**
     * A reference to the Storage in Firebase
     */
    private StorageReference storageReference;

    /**
     * Constants to check the user's clicks
     */
    private static final int IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    /**
     * A variable to store imageUri
     */
    private Uri imageUri;

    /**
     * A variable to create a Task to upload image
     */
    private StorageTask uploadTask;

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment UI should be attached to
     * @param savedInstanceState Fragment is being re-constructed from a previous saved state
     * @return Return the View for Profile Fragment UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        image_profile = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        location = view.findViewById(R.id.location);

        //new
        about_me = view.findViewById(R.id.about_me);
        txt_experience = view.findViewById(R.id.txt_experience);
        experience = view.findViewById(R.id.experience);
        txt_why_caretaker = view.findViewById(R.id.txt_why_caretaker);
        why_caretaker = view.findViewById(R.id.why_caretaker);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                /**
                 * Updates the display when there's a data change in the Database
                 * @param dataSnapshot The child node from the reference
                 */
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (isAdded()) {
                        about_me.setText(user.getAboutMe());
                        username.setText(user.getUsername());
                        location.setText(user.getLocation());
                        if (user.getImageURL().equals("default")) {
                            image_profile.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getContext()).load(user.getImageURL()).into(image_profile);
                        }

                        if (user.getIsCaretaker().equals("true")) {
                            txt_experience.setVisibility(View.VISIBLE);
                            experience.setVisibility(View.VISIBLE);
                            txt_why_caretaker.setVisibility(View.VISIBLE);
                            why_caretaker.setVisibility(View.VISIBLE);
                            experience.setText(user.getExperience());
                            why_caretaker.setText(user.getWhyCaretaker());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            image_profile.setOnClickListener(new View.OnClickListener() {
                /**
                 * Runs when the image_profile of the user is clicked
                 * @param view The current view of the fragment
                 */
                @Override
                public void onClick(View view) {
                    openImage();
                }
            });
        }
        return view;
    }

    /**
     * Start IMAGE_REQUEST activity if the profile image is clicked
     */
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    /**
     * Get the file extensions in String of the image
     * @param uri Immutable URI reference
     * @return A String of the file extension of the image
     */
    private String getFileExtensions(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     * The function does the following:
     * <ol>
     *     <li>Allows the user to upload an image and store it in the Database</li>
     *     <li>Puts the imageURL from storage to the database</li>
     *     <li>Checks if user uploaded any picture to the database</li>
     * </ol>
     */
    private void uploadImage() {

        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); //testing
        builder.setView(R.layout.loading_dialog_layout);
        final AlertDialog progress_dialog = builder.create();
        progress_dialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtensions(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                /**
                 * Tries to upload the image to storage
                 * @param task The current task that it's assigned
                 * @return The downloadURL if upload is successful
                 * @throws Exception The task's exception
                 */
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                /**
                 * Once task is completed:
                 * <ol>
                 *     <li>Updates the database with the new imageURL</li>
                 *     <li>Else, tells the user that the task failed</li>
                 * </ol>
                 * @param task
                 */
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        progress_dialog.dismiss();

                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        progress_dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progress_dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when the upload activity exits
     * @param requestCode The integer request code originally supplied to startActivityForResult()
     * @param resultCode The integer result code returned by the child activity
     * @param data An intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData()!= null) {
            imageUri = data.getData();

            if(uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}
