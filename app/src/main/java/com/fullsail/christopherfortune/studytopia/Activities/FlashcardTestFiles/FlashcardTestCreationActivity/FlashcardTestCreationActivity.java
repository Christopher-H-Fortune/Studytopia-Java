package com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestCreationActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlaschardQuestionListActivity.FlashcardQuestionListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationFragment.FlashcardTestCreationFragment;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.PublicFlashcardTests.PublicFlashcardTests;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.image.SmartImageView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FlashcardTestCreationActivity extends AppCompatActivity implements FlashcardTestCreationFragment.FlashcardTestCreationInterface{

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private int flashcardCount = 0;
    private int totalFlashcardTestCount;
    private int totalFlashcardCount;
    private FirebaseUser user;
    private String subjectChosenString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test_creation);

        // Set the instance of the Firebase auth and database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("subjectChosen")){
            subjectChosenString = startingIntent.getStringExtra("subjectChosen");
        }

        // Make sure the user ins't null
        if (user != null) {

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + uId);

            // Set the database to have a ValueEventListener to display the users data to the user
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Loop through the data snapshot
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

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

                            // Store the total flashcard test, questions and study groups count
                            totalFlashcardTestCount = flashcardTestCount;
                            totalFlashcardCount = flashcardCount;
                        }
                    }

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
                                Intent mapIntent = new Intent(FlashcardTestCreationActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardTestCreationActivity.this);
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
                        Intent studyGroupIntent = new Intent(FlashcardTestCreationActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(FlashcardTestCreationActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(FlashcardTestCreationActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(FlashcardTestCreationActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(FlashcardTestCreationActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(FlashcardTestCreationActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Create Flashcard Test</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the FlashcardTestCreationFragment to the user so they can create a flashcard test
        getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_creation_frame, FlashcardTestCreationFragment.newInstance(), FlashcardTestCreationFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(FlashcardTestCreationActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.save_questions:
                return true;
            case R.id.cancel_test_creation:

                // Intent to send the user back to the FlashcardTestSubject list
                Intent flashcardsListIntent = new Intent(FlashcardTestCreationActivity.this, FlashcardTestSubjectListActivity.class);

                // Start the FlashcardTestSubjectListActivity with the intent created above
                startActivity(flashcardsListIntent);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passFlashcardCount(int flashcardCount) {

        // Store the count of the flashcardCount
        this.flashcardCount = flashcardCount;
    }

    @Override
    public void continueTestCreation(EditText flashcardTestNameEdtTxt, RadioButton publicTestRadioButton) {
        // If the user didn't enter a test name
        if(flashcardTestNameEdtTxt.getText().toString().trim().equals("")){

            // Ask the user to enter a flashcard test name
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Please Enter a Flashcard Test Name");
            alertDialogBuilder.setMessage("To create a flashcard test, please enter a valid flashcard test name. ");
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

        // If the user didn't choose a flashcard Count
        if(flashcardCount == 0){

            // Ask the user to choose a flashcard count
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Please Chose Flashcard Count");
            alertDialogBuilder.setMessage("To create a flashcard test, please select the count of the flashcards. ");
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

        // If the user entered all the data to create a flashcard test
        if(!flashcardTestNameEdtTxt.getText().toString().trim().equals("") && flashcardCount != 0 && !subjectChosenString.equals("Please Select a Subject")){

            // Set a reference to our Firebase database
            DatabaseReference databaseReference = mFirebaseDatabase.getReference();

            // Get the flashcard test name entered by the user
            String flashcardTestName = flashcardTestNameEdtTxt.getText().toString().trim();

            // boolean to represent if the test if public or private
            boolean isPublic = publicTestRadioButton.isChecked();

            // Calendar instance to get the current date and time
            Calendar calendar = Calendar.getInstance();

            // Get the current time
            Date currentDate = calendar.getTime();

            // DateFormat object to format the date to the MM-dd-yyy HH:mm:ss pattern
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy HH:mm:ss", Locale.US);

            // Format the currentDate object to the pattern defined above as a string
            String dateString = dateFormat.format(currentDate);

            // Get a unique test Id represent the test in the public flashcard tests
            String uniqueTestId = databaseReference.child("publicTests").child("subjects").child(subjectChosenString.toLowerCase()).push().getKey();

            // Create a flashcardTest object to save to the database
            FlashcardTest testBeingCreated = new FlashcardTest(flashcardTestName, flashcardCount, isPublic, dateString, uniqueTestId, user.getUid());

            totalFlashcardTestCount += 1;
            totalFlashcardCount += flashcardCount;

            // Get the current user signed in
            FirebaseUser user = mAuth.getCurrentUser();

            // If the user isn't null
            if(user != null){

                // Get the userID
                String userID = user.getUid();

                // If the test being created was chosen to be public
                if(isPublic){

                    // If the testId isn't null
                    if(uniqueTestId != null){

                        // Create a publicFlashcardTest object to save the database
                        PublicFlashcardTests publicFlashcardTest = new PublicFlashcardTests(uniqueTestId, flashcardTestName, flashcardCount, true, dateString, userProfileData.getUsername());

                        // Save the flashcard test to the public tests portion of the database
                        databaseReference.child("publicTests").child("subjects").child(subjectChosenString.toLowerCase()).child(uniqueTestId).setValue(publicFlashcardTest);

                        // Save the flashcard test to the users tests portion of the database
                        databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString.toLowerCase()).child(uniqueTestId).setValue(testBeingCreated);

                        // Save the new total flashcard test count
                        databaseReference.child("users").child(userID).child("userProfile").child("flashcardTestCount").setValue(totalFlashcardTestCount);

                        // Save the new total flashcard test count
                        databaseReference.child("users").child(userID).child("userProfile").child("flashCardCount").setValue(totalFlashcardCount);
                    }

                    // If the test is set to not be public
                } else {

                    // Save the flashcard test to the users tests portion of the database
                    databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString.toLowerCase()).child(uniqueTestId).setValue(testBeingCreated);

                    // Save the new total flashcard test count
                    databaseReference.child("users").child(userID).child("userProfile").child("flashcardTestCount").setValue(totalFlashcardTestCount);

                    // Save the new total flashcard test count
                    databaseReference.child("users").child(userID).child("userProfile").child("flashCardCount").setValue(totalFlashcardCount);
                }

                // Intent to send the user to the FlashcardTestSubjectListActivity
                Intent questionListIntent = new Intent(this, FlashcardQuestionListActivity.class);

                questionListIntent.putExtra("flashcardTestChosen", testBeingCreated);

                questionListIntent.putExtra("subjectChosen", subjectChosenString.toLowerCase());

                // Start the FlashcardTestSubjectListActivity with the intent created above
                startActivity(questionListIntent);
            }
        }
    }

    @Override
    public void passFlashcardCountSeekbar(SeekBar flashcardCountSeekBar, TextView flashcardMaxCountLabel) {

        if(userProfileData.isPremium()){

            flashcardCountSeekBar.setMax(50);
            flashcardMaxCountLabel.setText(R.string.fifty_flashcards);

        } else {

            flashcardCountSeekBar.setMax(25);
            flashcardMaxCountLabel.setText(R.string.twenty_five_flashcards);
        }
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
