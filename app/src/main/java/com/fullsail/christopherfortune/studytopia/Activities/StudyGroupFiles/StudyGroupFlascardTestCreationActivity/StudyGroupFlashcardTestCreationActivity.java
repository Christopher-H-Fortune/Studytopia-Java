package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupFlascardTestCreationActivity;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StdyGrpFlashcardTestQuestionsActivity.StdyGrpFlashcardTestQuestionsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupFlashcardTest.StudyGroupFlashcardTest;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.CreateStudyGroupFlashcardTestFragment.CreateStudyGroupFlashcardTestFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class StudyGroupFlashcardTestCreationActivity extends AppCompatActivity implements CreateStudyGroupFlashcardTestFragment.CreateStudyGroupFlashcardTestInterface {

    private DrawerLayout drawerLayout;
    private UserData userProfileData = new UserData();
    private FirebaseDatabase mFirebaseDatabase;
    private int questionCount;
    private String studyGroupId;
    private SeekBar flashcardCountSeekBar;
    private TextView maxFlashcardCountLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_flashcard_test_creation);

        // Get the starting intent of the activity
        Intent startingIntent = getIntent();

        // Check if the starting intent has an extra named studyGroupId
        if(startingIntent.hasExtra("studyGroupId")){

            studyGroupId = startingIntent.getStringExtra("studyGroupId");
        }

        // Get an instance of the firebase authentication and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        final FirebaseUser user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if(user != null) {

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + uId);

            // Set the database to have a ValueEventListener to display the games data to the user
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
                                Intent mapIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupFlashcardTestCreationActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, LoginSignupActivity.class);
                        startActivity(logoutIntent);
                        break;
                }
                return true;
            }
        });

        // Set the supportActionBar with the toolbar above
        setSupportActionBar(toolbar);

        // Create an actionBar object
        ActionBar actionbar = getSupportActionBar();

        // If the actionBar isn't null
        if (actionbar != null) {

            // Set the icon, title, and enable the actionBar to allow the user to view the navigation view
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Create Test</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the CreateStudyGroupFlashcardTestFragment to the user so they can
        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_flashcard_test_creation_frame, CreateStudyGroupFlashcardTestFragment.newInstance(), CreateStudyGroupFlashcardTestFragment.TAG).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            // Open the navigation drawer
            drawerLayout.openDrawer(GravityCompat.START);

            // Get the SmartImageView and text views to display profile data
            SmartImageView userProfileImageSmrtImage = findViewById(R.id.profile_image_nav_smart_image);
            TextView firstLastNameTxtVw = findViewById(R.id.first_last_name_nav_txt_vw);
            TextView emailTxtVw = findViewById(R.id.email_nav_txt_view);

            // If the current user data isn't null
            if (userProfileData != null) {

                // Display the users profile pic, first and last name, and email
                userProfileImageSmrtImage.setImageUrl(userProfileData.getImageUrl());
                userProfileImageSmrtImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, UserProfileActivity.class);
                        startActivity(profileIntent);
                    }
                });
                firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                emailTxtVw.setText(userProfileData.getEmail());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void continueToQuestionList(EditText flashcardTestNameEdtTxt) {

        // Get the flashcard test name the user entered
        String flashcardTestName = flashcardTestNameEdtTxt.getText().toString().trim();

        // Check if the user entered a test name
        if(flashcardTestName.equals("")){

            // Ask the user to enter a flashcard test name
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Empty Test Name");
            alertDialogBuilder.setMessage("To create a flashcard test, please enter a flashcard test name. ");
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

        if(flashcardTestName.length() < 3){

            // Ask the user to enter a longer flashcard test name
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Empty Test Name");
            alertDialogBuilder.setMessage("To create a flashcard test, please enter a longer flashcard test name. ");
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

        // If the user didn't select a count of questions for the flashcard test
        if(questionCount == 0){

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

        // If the user filled out the data correctly to create a flashcard test
        if(!flashcardTestName.equals("") && flashcardTestName.length() >= 3 && questionCount != 0){

            // Set a reference to our Firebase database
            DatabaseReference databaseReference = mFirebaseDatabase.getReference();

            // Get a unique test Id represent the test in the public flashcard tests
            final String uniqueTestId = databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).push().getKey();

            // Calendar instance to get the current date and time
            Calendar calendar = Calendar.getInstance();

            // Get the current time
            Date currentDate = calendar.getTime();

            // DateFormat object to format the date to the MM-dd-yyy HH:mm:ss pattern
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy", Locale.US);

            // Format the currentDate object to the pattern defined above as a string
            String dateString = dateFormat.format(currentDate);

            // Create the flashcard test to save to the database
            final StudyGroupFlashcardTest flashcardTestToSave = new StudyGroupFlashcardTest(flashcardTestName, questionCount, dateString, uniqueTestId, userProfileData.getUsername());

            // If the uniqueTestId isn't null
            if(uniqueTestId != null){

                // Store the studyGroup test to the firebase database
                databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(uniqueTestId).setValue(flashcardTestToSave).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Alert the user the test has been created
                        Toast.makeText(StudyGroupFlashcardTestCreationActivity.this, getString(R.string.test_created), Toast.LENGTH_SHORT).show();

                        // Send the user to create the questions
                        Intent flashcardTestQuestionIntent = new Intent(StudyGroupFlashcardTestCreationActivity.this, StdyGrpFlashcardTestQuestionsActivity.class);

                        // Attach the testId of the test created above to the intent to the StdyGrpFlashcardTestQuestionsActivity
                        flashcardTestQuestionIntent.putExtra("testId", uniqueTestId);
                        flashcardTestQuestionIntent.putExtra("studyGroupId", studyGroupId);
                        flashcardTestQuestionIntent.putExtra("studyGroupTest", flashcardTestToSave);

                        // Start the StdyGrpFlashcardTestQuestionsActivity with the intent created above
                        startActivity(flashcardTestQuestionIntent);
                    }
                });
            }
        }
    }

    @Override
    public void passQuestionCount(int questionCount) {

        // Store the question count from the flashcard count seekBar
        this.questionCount = questionCount;
    }

    @Override
    public void passSeekBarAndLabel(SeekBar flashcardCountSeekBar, TextView flashcardCountMaxLabel) {
        this.flashcardCountSeekBar = flashcardCountSeekBar;
        this.maxFlashcardCountLabel = flashcardCountMaxLabel;
    }

    private void displayAdsOrNoAds(){

        AdView mAdView = findViewById(R.id.adView);

        if(userProfileData.isPremium()){

            mAdView.setVisibility(View.GONE);

        } else {

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        if(userProfileData.isPremium()){

            flashcardCountSeekBar.setMax(50);
            maxFlashcardCountLabel.setText(R.string.fifty_flashcards);

        } else {

            flashcardCountSeekBar.setMax(25);
            maxFlashcardCountLabel.setText(R.string.twenty_five_flashcards);
        }
    }
}
