package com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastFlashcardTestsActivity.PastFlashcardTestsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastStudyGroupsActivity.PastStudyGroupsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.GooglePay.GooglePay;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.EditProfleFragment.EditProfileFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.UserProfileFragment.UserProfileFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.image.SmartImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class UserProfileActivity extends AppCompatActivity implements UserProfileFragment.UserProfileInterface, EditProfileFragment.EditProfileInterface {

    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private FirebaseUser user;
    private SmartImageView profileImageChosen;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private final int IMAGE_REQUEST = 0x01010;
    private Uri imageUri;
    private String TAG = "UserProfileActivity.TAG";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            //mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Set the instance of the Firebase auth and database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if(user != null){

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + uId);

            // Set the database to have a ValueEventListener to display the games data to the user
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Loop through the data snapshot
                    for(DataSnapshot data : dataSnapshot.getChildren()){

                        // Get the userData data
                        UserData userData = data.getValue(UserData.class);

                        // Make sure userData isn't null
                        if (userData != null) {

                            // Get the user profile data to create a UserData object
                            String firsName = userData.getFirstName();
                            String lastName = userData.getLastName();
                            String userName = userData.getUsername();
                            String email = userData.getEmail();
                            String imageUrl = userData.getImageUrl();
                            String imageName = userData.getImageName();
                            int flashcardTestCount = userData.getFlashcardTestCount();
                            int flashcardCount = userData.getFlashCardCount();
                            int studyGroupCount = userData.getStudyGroupCount();
                            boolean isPremium = userData.isPremium();

                            // Store the userData to the userProfileData
                            userProfileData = new UserData(firsName, lastName, userName, email, imageUrl, imageName, flashcardTestCount, flashcardCount, studyGroupCount, isPremium);
                        }
                    }

                    // Call the displayProfile Data method to display the profile data to the user
                    displayProfileData();
                    displayAdsOrNoAds();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mDatabaseReference.keepSynced(true);

        }

        // get the drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Get the toolbar and navigation view
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set an ItemSelectedListener to the navigationView obtained above
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_study_group_map:
                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = null;
                        if (connectivityManager != null) {
                            networkInfo = connectivityManager.getActiveNetworkInfo();
                        }

                        if(networkInfo != null){

                            if(networkInfo.isConnected()){
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent mapIntent = new Intent(UserProfileActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserProfileActivity.this);
                            alertDialogBuilder.setTitle("No Internet Connection");
                            alertDialogBuilder.setMessage("To view Study Group Map, please obtain internet connection to continue.");
                            alertDialogBuilder.setCancelable(true);
                            alertDialogBuilder.setPositiveButton(
                                    "Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        break;
                    case R.id.nav_study_group:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent studyGroupIntent = new Intent(UserProfileActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(UserProfileActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(UserProfileActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(UserProfileActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(UserProfileActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(UserProfileActivity.this, LoginSignupActivity.class);
                        startActivity(logoutIntent);
                }
                return true;
            }
        });

        // Set the supportActionBar with the toolbar above
        setSupportActionBar(toolbar);

        // Create an actionBar object
        actionbar = getSupportActionBar();

        // If the actionBar isn't null
        if (actionbar != null) {

            // Set the icon, title, and enable the actionBar to allow the user to view the navigation view
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Profile</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the userProfileFragment to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.user_profile_frame, UserProfileFragment.newInstance(), UserProfileFragment.TAG).commit();

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        mPaymentsClient = Wallet.getPaymentsClient(this, new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build());

        possiblyShowGooglePayButton();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mService != null) {
//            unbindService(mServiceConn);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // Open the navigation drawer
                drawerLayout.openDrawer(GravityCompat.START);

                // Get the SmartImageView and text views to display profile data
                SmartImageView userProfileImageSmrtImage = findViewById(R.id.profile_image_nav_smart_image);
                TextView firstLastNameTxtVw = findViewById(R.id.first_last_name_nav_txt_vw);
                TextView emailTxtVw = findViewById(R.id.email_nav_txt_view);

                // If the current user data isn't null
                if(userProfileData != null){

                    // Display the users profile pic, first and last name, and email
                    userProfileImageSmrtImage.setImageUrl(userProfileData.getImageUrl());
                    userProfileImageSmrtImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profileIntent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.edit_profile:

                // Display the EditProfileFragment to the user to allow them to edit their profile
                getSupportFragmentManager().beginTransaction().replace(R.id.user_profile_frame, EditProfileFragment.newInstance(), EditProfileFragment.TAG).commit();
                actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Edit Profile</font>", Html.FROM_HTML_MODE_LEGACY));
                return true;
            case R.id.save_profile_edits:

                // Call the saveProfileEdits method to save the edits made to the users profile
                saveProfileEdits();
                return true;

        }
        return true;
    }

    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result) {
                                // show Google as a payment option
                                mGooglePayButton = findViewById(R.id.google_pay_button);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestPayment(view);
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            // handle developer errors
                        }
                    }
                });
    }

    public void requestPayment(View view) {
        Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest();
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
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
                    Bitmap profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    profileImageChosen.setImageBitmap(profileImageBitmap);

                    profileImageChosen.setTag("picture set");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        DatabaseReference premiumReference = mFirebaseDatabase.getReference();

        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
//                        PaymentData paymentData = PaymentData.getFromIntent(data);
//                        String json = paymentData.toJson();
//                        // if using gateway tokenization, pass this token without modification
//                        String paymentMethodData = new JSONObject(json).getJSONObject(“paymentMethodData”);
//                        String paymentToken = paymentMethodData.get

                        premiumReference.child("users").child(user.getUid()).child("userProfile").child("premium").setValue(true);

                        break;
                    case Activity.RESULT_CANCELED:

                        premiumReference.child("users").child(user.getUid()).child("userProfile").child("premium").setValue(true);

                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        // Log the status for debugging.
                        // Generally, there is no need to show an error to the user.
                        // The Google Pay payment sheet will present any account errors.

                        premiumReference.child("users").child(user.getUid()).child("userProfile").child("premium").setValue(true);

                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
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
    public void viewPastStudyGroups() {

        // Intent to send the user to the pastStudyGroups activity to view their past study groups
        Intent pastStudyGroups = new Intent(this, PastStudyGroupsActivity.class);

        // Start the activity with the intent created above
        startActivity(pastStudyGroups);
    }

    @Override
    public void viewTestsTaken() {

        // Intent to send the user to the PastFlashcardTests Activity to view their past tests taken
        Intent pastTests = new Intent(this, PastFlashcardTestsActivity.class);

        // Start the activity with the intent created above
        startActivity(pastTests);
    }

    @Override
    public void viewsDisplayed() {
        // Set the database reference using the mFirebaseDatabase
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + user.getUid());

        // Set the database to have a ValueEventListener to display the games data to the user
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for(DataSnapshot data : dataSnapshot.getChildren()){

                    // Get the userData data
                    UserData userData = data.getValue(UserData.class);

                    // Make sure userData isn't null
                    if (userData != null) {

                        // Get the user profile data to create a UserData object
                        String firsName = userData.getFirstName();
                        String lastName = userData.getLastName();
                        String userName = userData.getUsername();
                        String email = userData.getEmail();
                        String imageUrl = userData.getImageUrl();
                        String imageName = userData.getImageName();
                        int flashcardTestCount = userData.getFlashcardTestCount();
                        int flashcardCount = userData.getFlashCardCount();
                        int studyGroupCount = userData.getStudyGroupCount();
                        boolean isPremium = userData.isPremium();

                        // Store the userData to the userProfileData
                        userProfileData = new UserData(firsName, lastName, userName, email, imageUrl, imageName, flashcardTestCount, flashcardCount, studyGroupCount, isPremium);
                    }
                }

                // Call the displayProfile Data method to display the profile data to the user
                displayProfileData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReference.keepSynced(true);
    }

    private void displayProfileData(){

        // Get the profile views to display the profile Data
        SmartImageView profileSmartImage = findViewById(R.id.user_profile_image_smart_img_vw);
        TextView profileFirstNameTxtVw = findViewById(R.id.first_name_profile_txt_view);
        TextView profileLastNameTxtVw = findViewById(R.id.last_name_profile_text_view);
        TextView profileEmailTxtVw = findViewById(R.id.email_profile_text_view);
        TextView flashcardTestCountTxtVw = findViewById(R.id.flashcard_test_count_text_view);
        TextView flashcardCountTxtVw = findViewById(R.id.flashcard_count_text_view);
        TextView studyGroupCountTxtVw = findViewById(R.id.groups_attended_count_text_view);

        // Display the profile image, first and last name, and email
        profileSmartImage.setImageUrl(userProfileData.getImageUrl());
        profileFirstNameTxtVw.setText(userProfileData.getFirstName());
        profileLastNameTxtVw.setText(userProfileData.getLastName());
        profileEmailTxtVw.setText(userProfileData.getEmail());

        // Convert the flashcard test Count to a string
        String flashcardTestCountString = Integer.toString(userProfileData.getFlashcardTestCount());

        // Display the total flashcard test count
        flashcardTestCountTxtVw.setText(flashcardTestCountString);

        // Convert the flashcard count to a string
        String flashcardCountString = Integer.toString(userProfileData.getFlashCardCount());

        // Display the total flashcard count
        flashcardCountTxtVw.setText(flashcardCountString);

        // Convert the study group count to a string
        String studyGroupCountString = Integer.toString(userProfileData.getStudyGroupCount());

        // Display the total study groups counts
        studyGroupCountTxtVw.setText(studyGroupCountString);
    }

    private void saveProfileEdits(){

        boolean passwordUpdated = false;

        // Create a databaseReference object to save the profile changes
        final DatabaseReference userProfileDatabaseRef = mFirebaseDatabase.getReference();

        // Get the password the user entered
        String password = passwordEditText.getText().toString().trim();

        // Get the email the user entered
        String email = emailEditText.getText().toString().trim();

        // Get the username the user entered
        String username = usernameEditText.getText().toString().trim();

        // Get the last name the user entered
        String lastName = lastNameEditText.getText().toString().trim();

        // Get the first name the user entered
        String firstName = firstNameEditText.getText().toString().trim();

        // If the password entered isn't null and is at least 6 characters in length
        if(!password.equals("") && password.length() >= 6){

            // If the current user isn't null
            if(user != null){

                // Update the users password with the password entered
                user.updatePassword(password);
                passwordUpdated = true;
            }

        }

        // If the email entered is not empty and valid
        if(!email.equals("") && Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            // If the current user isn't null
            if(user != null){

                // Update the users email with the email entered
                user.updateEmail(email);
                userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("email").setValue(email);
            }
        }

        if(!username.isEmpty()){

            userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("username").setValue(username);
        }

        if(!lastName.isEmpty() && lastName.length() >= 3){

            userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("lastName").setValue(lastName);
        }

        if(!firstName.isEmpty() && firstName.length() >= 3){

            userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("firstName").setValue(firstName);
        }

        // If the user entered all the data to update their user profile
        if(!profileImageChosen.getTag().equals("no image")){

            final StorageReference imageReference = mStorageReference.child("profileImages/" + userProfileData.getImageName());

            imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(UserProfileActivity.this, "Old Profile Picture Deleted", Toast.LENGTH_SHORT).show();

                    final String pictureIdentifier = UUID.randomUUID().toString();

                    final StorageReference newImageReference = mStorageReference.child("profileImages/" + pictureIdentifier);

                    newImageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(UserProfileActivity.this, "New Profile Picture Uploaded", Toast.LENGTH_SHORT).show();

                            newImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {

                                   String urlString = uri.toString();

                                   Log.i(TAG, urlString);
                                   userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("imageUrl").setValue(urlString);


                               }
                           });

                            userProfileDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("imageName").setValue(pictureIdentifier);
                        }
                    });
                }
            });

        }

        if(passwordUpdated){

            Intent logoutIntent = new Intent(UserProfileActivity.this, LoginSignupActivity.class);
            startActivity(logoutIntent);
            mAuth.signOut();

        } else {

            // Display the UserProfileFragment to the user to see the new profile data
            getSupportFragmentManager().beginTransaction().replace(R.id.user_profile_frame, UserProfileFragment.newInstance(), UserProfileFragment.TAG).commit();

            // Set the action bar title to Profile
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Profile</font>", Html.FROM_HTML_MODE_LEGACY));
        }
    }

    @Override
    public void passViews(EditText firstNameEditText, EditText lastNameEditText, EditText usernameEditText, EditText emailEditText, EditText passwordEditText, SmartImageView editProfileImageSmartImg) {
        // Pass the views the set the data to display at a later time
        profileImageChosen = editProfileImageSmartImg;
        this.firstNameEditText = firstNameEditText;
        this.lastNameEditText = lastNameEditText;
        this.usernameEditText = usernameEditText;
        this.emailEditText = emailEditText;
        this.passwordEditText = passwordEditText;

        // Display the users first and last name, username, and email to their corresponding views
        profileImageChosen.setImageUrl(userProfileData.getImageUrl());
        firstNameEditText.setText(userProfileData.getFirstName());
        lastNameEditText.setText(userProfileData.getLastName());
        usernameEditText.setText(userProfileData.getUsername());
        emailEditText.setText(userProfileData.getEmail());
    }

    @Override
    public void editProfileImage() {

        requestPermissions();
    }

    private void displayAdsOrNoAds(){

        AdView mAdView = findViewById(R.id.adView);

        if(userProfileData.isPremium()){

            mAdView.setVisibility(View.GONE);

        } else {

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }
}
