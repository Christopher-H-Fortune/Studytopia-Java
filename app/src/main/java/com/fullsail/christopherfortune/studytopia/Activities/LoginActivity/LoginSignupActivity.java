package com.fullsail.christopherfortune.studytopia.Activities.LoginActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.Usernames.Usernames;
import com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.ForgotPasswordFragment.ForgotPasswordFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.LoginFragment.LoginFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.SignUpFragment.SignUpFragment;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class LoginSignupActivity extends AppCompatActivity implements LoginFragment.LoginFragmentInterface, SignUpFragment.SignUpFragmentInterface, ForgotPasswordFragment.ForgotPasswordInterface {

    private static final String TAG = "LoginSignupActivity";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private final int IMAGE_REQUEST = 0x01010;
    private Uri imageUri;
    private ImageButton profileImageChosen;
    private ArrayList<Usernames> usernamesArrayList = new ArrayList<>();
    private String urlString;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String verifyPassword;
    private boolean usernameIsUnique = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup_);
        setTitle("Login");

        // Initialize the application to Firebase
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");


        // Set the instance of the Firebase auth and database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

        mDatabaseReference.child("usernamesCreated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for(DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the usernames data
                    Usernames usernames = data.getValue(Usernames.class);

                    // Make sure usernames isn't null
                    if (usernames != null) {

                        String username = usernames.getUserName();

                        usernamesArrayList.add(new Usernames(username));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Display the LoginFragment to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame_layout, LoginFragment.newInstance(), LoginFragment.TAG).commit();
    }

    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_REQUEST);
            } catch (Exception e){

            }
        } else {
            selectProfileImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(this, data);
            cropRequest(imageUri);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap profileImageBitmap = null;
                    if (result != null) {
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    }

                    Log.i(TAG, "Bitmap Size: " + profileImageBitmap.getByteCount());

                    profileImageChosen.setImageBitmap(profileImageBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IMAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectProfileImage();
        } else {
            requestPermissions();
        }
    }

    public void selectProfileImage() {
        CropImage.startPickImageActivity(this);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void login(EditText emailEditText, EditText passwordEditText) {

        // String variables to get the data from the edit text and verify the data entered
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // ColorStateList to change the edit text color if the user need to update the fields of information required to create an account
        ColorStateList editTextColorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_light));

        // If there isn't an email entered or the email isn't valid
        if(email.isEmpty()) {

            emailEditText.setBackgroundTintList(editTextColorStateList);
            emailEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            emailEditText.setBackgroundTintList(editTextColorStateList);
            emailEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }

        // If a password isn't entered
        if(password.isEmpty()){

            passwordEditText.setBackgroundTintList(editTextColorStateList);
            passwordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }

        // If the password isn't long enough
        if(password.length() < 6){

            passwordEditText.setBackgroundTintList(editTextColorStateList);
            passwordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }

        // If the email and password isn't empty
        if(!password.isEmpty() && !email.isEmpty()){

            // Login the user with the email and password entered
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    // If login was successful
                    if(task.isSuccessful()){

                        // Display the message to the user
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();

                        // Get the current user signed in
                        FirebaseUser user = mAuth.getCurrentUser();

                        // If the user isn't null
                        if(user != null) {

                            // Intent to send the user to the publicFlashcardsActivity
                            Intent publicFlashCardsIntent = new Intent(LoginSignupActivity.this, PublicFlashcardsCategoryListActivity.class);

                            // Send the user to the public flashcards screen
                            startActivity(publicFlashCardsIntent);
                        }

                    // If the login wasn't successful
                    } else {

                        // If the exception isn't null
                        if(task.getException() != null){

                            //Display the message to the user
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void signUp() {
        // Display the SignUpFragment to the user to allow them to create an account
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame_layout, SignUpFragment.newInstance(), SignUpFragment.TAG).addToBackStack(null).commit();
    }

    @Override
    public void forgotPassword() {
        // Display the ForgotPasswordFragment to the user to allow them to reset their password
        getSupportFragmentManager().beginTransaction().replace(R.id.login_frame_layout, ForgotPasswordFragment.newInstance(), ForgotPasswordFragment.TAG).addToBackStack(null).commit();
    }


    @Override
    public void createAccount(ImageButton profilePicImageView, EditText firstNameEditText, EditText lastNameEditText, EditText usernameEditText, EditText emailEditText, EditText passwordEditText, EditText verifyPasswordEditText) {

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;

        // Temp store the profile data the user entered in the edit text fields
         firstName = firstNameEditText.getText().toString();
         lastName = lastNameEditText.getText().toString();
         username = usernameEditText.getText().toString();
         email = emailEditText.getText().toString();
         password = passwordEditText.getText().toString();
         verifyPassword = verifyPasswordEditText.getText().toString();

         // ColorStateList to change the edit text color if the user need to update the fields of information required to create an account
        ColorStateList editTextColorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_light));

        // If the password is too small
        if(password.equals(verifyPassword) && password.length() < 6){

            passwordEditText.setBackgroundTintList(editTextColorStateList);
            passwordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            verifyPasswordEditText.setBackgroundTintList(editTextColorStateList);
            verifyPasswordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            passwordEditText.setText(null);
            passwordEditText.setHint(Html.fromHtml("<small><small>Password must be at least 6 characters.</small></small>", Html.FROM_HTML_MODE_LEGACY));

            verifyPasswordEditText.setText(null);
            verifyPasswordEditText.setHint(Html.fromHtml("<small><small>Password must be at least 6 characters.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        // If the password is empty
        if(password.trim().isEmpty() || verifyPassword.trim().isEmpty()){

            passwordEditText.setBackgroundTintList(editTextColorStateList);
            passwordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            verifyPasswordEditText.setBackgroundTintList(editTextColorStateList);
            verifyPasswordEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            passwordEditText.setText(null);
            passwordEditText.setHint(Html.fromHtml("<small><small>A password is required to create an account.</small></small>", Html.FROM_HTML_MODE_LEGACY));
            verifyPasswordEditText.setText(null);
            verifyPasswordEditText.setHint(Html.fromHtml("<small><small>A password is required to create an account.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            emailEditText.setBackgroundTintList(editTextColorStateList);
            emailEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            emailEditText.setText(null);
            emailEditText.setHint(Html.fromHtml("<small><small>A valid email is required.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        // If the email is empty or not valid
        if(email.trim().isEmpty()){

            emailEditText.setBackgroundTintList(editTextColorStateList);
            emailEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            emailEditText.setText(null);
            emailEditText.setHint(Html.fromHtml("<small><small>An email is required.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        // If the username is empty
        if(username.trim().isEmpty()){

            usernameEditText.setBackgroundTintList(editTextColorStateList);
            usernameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            usernameEditText.setText(null);
            usernameEditText.setHint(Html.fromHtml("<small><small>Username is required.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(username.trim().length() < 3){

            usernameEditText.setBackgroundTintList(editTextColorStateList);
            usernameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            usernameEditText.setText(null);
            usernameEditText.setHint(Html.fromHtml("<small><small>Username must be at least 3 characters.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(usernamesArrayList.size() > 0){

            for(Usernames usernames: usernamesArrayList){

                if(username.trim().equals(usernames.getUserName())){

                    alertDialogBuilder.setTitle("Username Already Taken");
                    alertDialogBuilder.setMessage("To create an account, your username entered must be unique.");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    usernameIsUnique = false;

                    // If the user name is unique
                } else {

                    usernameIsUnique = true;
                }
            }
        } else {

            usernameIsUnique = true;
        }


        // If the last name is empty
        if(lastName.trim().isEmpty()){

            lastNameEditText.setBackgroundTintList(editTextColorStateList);
            lastNameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            lastNameEditText.setText(null);
            lastNameEditText.setHint(Html.fromHtml("<small><small>Last Name is required.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(lastName.trim().length() < 3){

            lastNameEditText.setBackgroundTintList(editTextColorStateList);
            lastNameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            lastNameEditText.setText(null);
            lastNameEditText.setHint(Html.fromHtml("<small><small>Last Name must be at least 3 characters.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

         // If the first name is empty
        if(firstName.trim().isEmpty()){

            firstNameEditText.setBackgroundTintList(editTextColorStateList);
            firstNameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            lastNameEditText.setText(null);
            lastNameEditText.setHint(Html.fromHtml("<small><small>First Name is required.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(firstName.trim().length() < 3){

            firstNameEditText.setBackgroundTintList(editTextColorStateList);
            firstNameEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            firstNameEditText.setText(null);
            firstNameEditText.setHint(Html.fromHtml("<small><small>First Name must be at least 3 characters.</small></small>", Html.FROM_HTML_MODE_LEGACY));
        }

        if(imageUri == null){

            alertDialogBuilder.setTitle("Select a Photo");
            alertDialogBuilder.setMessage("To create an account, you need to choose a photo for your profile.");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton(
                    "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        // If the user chose an image
        if(imageUri != null){

            final String pictureIdentifier = UUID.randomUUID().toString();

            // Get an instance of the FirebaseStorage at profileImages
            final StorageReference imageRef = mStorageReference.child("profileImages/" + pictureIdentifier);

            progressBar.setVisibility(View.VISIBLE);

            // upload the image chosen to the firebase storage
             imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Get the imageUrl after it was uploaded successfully
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlString = uri.toString();

                            // If the user entered in all the data to create an account
                            if(!email.isEmpty() && !password.isEmpty() && password.length() >= 6 && !verifyPassword.isEmpty() && verifyPassword.length() >= 6 && password.toLowerCase().trim().equals(verifyPassword.toLowerCase().trim()) && !firstName.isEmpty() && firstName.length() >= 3 && !lastName.isEmpty() && lastName.length() >= 3 && !username.isEmpty() && username.length() >= 3 && !urlString.isEmpty() && usernameIsUnique){

                                // Create a new UserData object from the data the user entered
                                final UserData userData = new UserData(firstName, lastName, username, email, urlString, pictureIdentifier,0, 0, 0, false);

                                // If all the conditionals are met, create the users account
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        // If the account was created
                                        if(task.isSuccessful()){

                                            // Ask the user to enter their password again to create an account
                                            Toast.makeText(getApplicationContext(), "Account Created! Welcome!", Toast.LENGTH_SHORT).show();

                                            // Sign in the newly created user
                                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    // If login was successful
                                                    if(task.isSuccessful()){

                                                        // Get the current user signed in
                                                        FirebaseUser user = mAuth.getCurrentUser();

                                                        // If the user isn't null
                                                        if(user != null){

                                                            // Get the userID
                                                            String userID = user.getUid();

                                                            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

                                                            String userNameId = UUID.randomUUID().toString();

                                                            Usernames usernameEntered = new Usernames(username);

                                                            mDatabaseReference.child("usernamesCreated").child(userNameId).setValue(usernameEntered);

                                                            // Set a reference to our Firebase database
                                                            DatabaseReference databaseReference = mFirebaseDatabase.getReference();

                                                            // Create the user's profile in the database using the user's id
                                                            databaseReference.child("users").child(userID).child("userProfile").setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                    // Send the user to the publicFlashcardsScreen
                                                                    Intent publicFlashcardSubjectsListIntent = new Intent(LoginSignupActivity.this, PublicFlashcardsCategoryListActivity.class);

                                                                    // Start the games list activity
                                                                    startActivity(publicFlashcardSubjectsListIntent);
                                                                }
                                                            });
                                                        }

                                                    // If there was an error in creating the account
                                                    } else {

                                                        // IF the exception isn't null
                                                        if(task.getException() != null){

                                                            // If the user already exists
                                                            if(task.getException() instanceof FirebaseAuthUserCollisionException){

                                                                // Display the message to the user
                                                                Toast.makeText(getApplicationContext(),"Account already created.", Toast.LENGTH_SHORT).show();

                                                                progressBar.setVisibility(View.INVISIBLE);

                                                                // Display message in error of creating user
                                                            } else {
                                                                // Display the message to the user
                                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                        // If the account wasn't created
                                        } else {

                                            // Ask the user to try again
                                            Toast.makeText(getApplicationContext(), "Sign-Up failed. Pleas Try again.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {

                                progressBar.setVisibility(View.INVISIBLE);

                                Log.i(TAG, "Conditional Worked Correctly");
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void choosePhoto(ImageButton profileImageBtn) {

        // Pass the image button to display the picture the user chose
        profileImageChosen = profileImageBtn;

        requestPermissions();
    }

    @Override
    public void sendResetLink(EditText emailEditText) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // ColorStateList to change the edit text color if the user need to update the fields of information required to create an account
        ColorStateList editTextColorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_light));

        // Get the email the user entered
        String email = emailEditText.getText().toString().trim();

        // If the email entered is a valid email
        if(!email.equals("") && Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            // Send a password reset link to the email the user entered
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    alertDialogBuilder.setTitle("Email Sent");
                    alertDialogBuilder.setMessage("Check your email entered for a password reset link from Study Group. After entering and saving the new password, you will be able to login with your new password.");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();

                                    getSupportFragmentManager().beginTransaction().replace(R.id.login_frame_layout, LoginFragment.newInstance(), LoginFragment.TAG).commit();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {

            emailEditText.setBackgroundTintList(editTextColorStateList);
            emailEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        }
    }
}
