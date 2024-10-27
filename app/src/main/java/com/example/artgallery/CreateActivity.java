package com.example.artgallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artgallery.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.artgallery.Model.Post;

import java.util.Objects;

public class CreateActivity extends AppCompatActivity {
    private static final String TAG = "CreateActivity";
    private static final int PICK_PHOTO_CODE = 1234;
    public static final String EXTRA_USERNAME = "username";

    private User signedInUser;
    private Uri photoUri;
    private FirebaseFirestore firestoreDb;
    private StorageReference storageReference;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_create);
//
//        storageReference = FirebaseStorage.getInstance().getReference();
//        firestoreDb = FirebaseFirestore.getInstance();
//
//        firestoreDb.collection("users")
//                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
//                .get()
//                .addOnSuccessListener(userSnapshot -> {
//                    signedInUser = userSnapshot.toObject(User.class);
//                    Log.i(TAG, "Signed-in user: " + signedInUser);
//                })
//                .addOnFailureListener(exception -> Log.i(TAG, "Failure fetching signed-in user", exception));
//
//        findViewById(R.id.btnPickImage).setOnClickListener(view -> {
//            Log.i(TAG, "Opening image picker on device");
//            Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
//            imagePickerIntent.setType("image/*");
//            if (imagePickerIntent.resolveActivity(getPackageManager()) != null) {
//                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE);
//            }
//        });
//
//        findViewById(R.id.btnSubmit).setOnClickListener(view -> handleSubmitButtonClick());
//    }

    ////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestoreDb = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User is signed in, proceed to fetch user data
            firestoreDb.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(userSnapshot -> {
                        signedInUser = userSnapshot.toObject(User.class);
                        Log.i(TAG, "Signed-in user: " + signedInUser);
                    })
                    .addOnFailureListener(exception ->
                            Log.i(TAG, "Failure fetching signed-in user", exception)
                    );
        } else {
            // No signed-in user, redirect to login
            Log.i(TAG, "No signed-in user found. Redirecting to login.");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        findViewById(R.id.btnPickImage).setOnClickListener(view -> {
            Log.i(TAG, "Opening image picker on device");
            Intent imagePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imagePickerIntent.setType("image/*");
            if (imagePickerIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE);
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(view -> handleSubmitButtonClick());
    }
//////////////////////////

    private void handleSubmitButtonClick() {
        if (photoUri == null) {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
            return;
        }
        String description = ((EditText) findViewById(R.id.etDescription)).getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (signedInUser == null) {
            Toast.makeText(this, "No signed-in user, please wait", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btnSubmit).setEnabled(false);
        Uri photoUploadUri = photoUri;
        StorageReference photoReference = storageReference.child("images/" + System.currentTimeMillis() + "-photo.jpg");

        // Upload photo to Firebase Storage
        photoReference.putFile(photoUploadUri)
                .continueWithTask(photoUploadTask -> {
                    Log.i(TAG, "Uploaded bytes: " + Objects.requireNonNull(photoUploadTask.getResult()).getBytesTransferred());
                    return photoReference.getDownloadUrl();
                })
                .continueWithTask(downloadUrlTask -> {
                    Post post = new Post(
                            description,
                            Objects.requireNonNull(downloadUrlTask.getResult()).toString(),
                            System.currentTimeMillis(),
                            signedInUser
                    );
                    return firestoreDb.collection("posts").add(post);
                })
                .addOnCompleteListener(postCreationTask -> {
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    if (!postCreationTask.isSuccessful()) {
                        Log.e(TAG, "Exception during Firebase operations", postCreationTask.getException());
                        Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ((EditText) findViewById(R.id.etDescription)).setText("");
                    ((ImageView) findViewById(R.id.imageView)).setImageResource(0);
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                    Intent profileIntent = new Intent(this, ProfileActivity.class);
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser.getUsername());
                    startActivity(profileIntent);
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            photoUri = data.getData();
            Log.i(TAG, "photoUri: " + photoUri);
            if (photoUri != null) {
                ((ImageView) findViewById(R.id.imageView)).setImageURI(photoUri);
            }
        } else {
            Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show();
        }
    }
}
