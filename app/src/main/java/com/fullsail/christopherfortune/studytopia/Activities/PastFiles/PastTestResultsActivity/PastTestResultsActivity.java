package com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastTestResultsActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardsResults.FlashcardsResults;
import com.fullsail.christopherfortune.studytopia.DataModels.TestResults.TestResults;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestResultsFragment.FlashcardTestResultsFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

import java.util.ArrayList;
import java.util.List;

public class PastTestResultsActivity extends AppCompatActivity implements FlashcardTestResultsFragment.FlashcardTestResultsInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseUser user;
    private UserData userProfileData = new UserData();
    private String subjectChosen;
    private String testIdChosen;
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<FlashcardsResults> correctQuestionsArrayList = new ArrayList<>();
    private ArrayList<FlashcardsResults> wrongQuestionsArrayList = new ArrayList<>();
    private TestResults testResultsChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_test_results);

        subjectChosen = "";
        testIdChosen = "";

        // Get the starting intent of the activity
        Intent startingIntent = getIntent();

        // If the intent has the subjectChosen and testChosen extras
        if(startingIntent.hasExtra("resultsChosen")){

            testResultsChosen = (TestResults)startingIntent.getSerializableExtra("resultsChosen");
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.past_flashcard_test_results_list_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();

        // Set the instance of the Firebase auth and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if (user != null) {

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
                                Intent mapIntent = new Intent(PastTestResultsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PastTestResultsActivity.this);
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
                        Intent studyGroupIntent = new Intent(PastTestResultsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(PastTestResultsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(PastTestResultsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(PastTestResultsActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(PastTestResultsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(PastTestResultsActivity.this, LoginSignupActivity.class);
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

            // Set the icon, title, and enable the actionBar to allow the user to view the navigation vie
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + testResultsChosen.getTestName() + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }
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
                        Intent profileIntent = new Intent(PastTestResultsActivity.this, UserProfileActivity.class);
                        startActivity(profileIntent);
                    }
                });
                firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                emailTxtVw.setText(userProfileData.getEmail());
            }
            return true;
        }
        return true;
    }

    @Override
    public void passViews(PieChart correctWrongPieChart, PieChart answeredSkippedPieChart) {
        if(testResultsChosen.getQuestionsRight() != 0 && testResultsChosen.getQuestionsWrong() != 0){

            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(testResultsChosen.getQuestionsRight(), "Correct"));
            correctWrongEntries.add(new PieEntry(testResultsChosen.getQuestionsWrong(), getString(R.string.wrong)));

            correctWrongPieChart.setDescription(null);

            PieDataSet correctWrongPieDataSet = new PieDataSet(correctWrongEntries, "Correct vs. Wrong");
            correctWrongPieDataSet.setDrawIcons(false);
            correctWrongPieDataSet.setSliceSpace(5f);
            correctWrongPieDataSet.setSelectionShift(8f);
            correctWrongPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.BLUE);
            colors.add(Color.RED);
            correctWrongPieDataSet.setColors(colors);

            PieData correctWrongPieData = new PieData(correctWrongPieDataSet);
            correctWrongPieData.setValueTextColor(android.R.color.black);
            correctWrongPieData.setValueTypeface(Typeface.DEFAULT);
            correctWrongPieData.setValueTextSize(12);

            correctWrongPieChart.setData(correctWrongPieData);
            correctWrongPieChart.animateXY(1400, 1400);
            correctWrongPieChart.setExtraOffsets(5, 10, 5, 5);
            correctWrongPieChart.setDrawRoundedSlices(true);
            correctWrongPieChart.invalidate();
        }

        if(testResultsChosen.getQuestionsWrong() == 0){
            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(testResultsChosen.getQuestionsRight(), "Correct"));

            correctWrongPieChart.setDescription(null);

            PieDataSet correctWrongPieDataSet = new PieDataSet(correctWrongEntries, "Correct vs. Wrong");
            correctWrongPieDataSet.setDrawIcons(false);
            correctWrongPieDataSet.setSliceSpace(5f);
            correctWrongPieDataSet.setSelectionShift(8f);
            correctWrongPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.BLUE);
            correctWrongPieDataSet.setColors(colors);

            PieData correctWrongPieData = new PieData(correctWrongPieDataSet);
            correctWrongPieData.setValueTextColor(android.R.color.black);
            correctWrongPieData.setValueTypeface(Typeface.DEFAULT);
            correctWrongPieData.setValueTextSize(12);

            correctWrongPieChart.setData(correctWrongPieData);
            correctWrongPieChart.animateXY(1400, 1400);
            correctWrongPieChart.setExtraOffsets(5, 10, 5, 5);
            correctWrongPieChart.setDrawRoundedSlices(true);
            correctWrongPieChart.invalidate();
        }

        if(testResultsChosen.getQuestionsRight() == 0){
            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(testResultsChosen.getQuestionsWrong(), getString(R.string.wrong)));

            correctWrongPieChart.setDescription(null);

            PieDataSet correctWrongPieDataSet = new PieDataSet(correctWrongEntries, "Correct vs. Wrong");
            correctWrongPieDataSet.setDrawIcons(false);
            correctWrongPieDataSet.setSliceSpace(5f);
            correctWrongPieDataSet.setSelectionShift(8f);
            correctWrongPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(Color.RED);
            correctWrongPieDataSet.setColors(colors);

            PieData correctWrongPieData = new PieData(correctWrongPieDataSet);
            correctWrongPieData.setValueTextColor(android.R.color.black);
            correctWrongPieData.setValueTypeface(Typeface.DEFAULT);
            correctWrongPieData.setValueTextSize(12);

            correctWrongPieChart.setData(correctWrongPieData);
            correctWrongPieChart.animateXY(1400, 1400);
            correctWrongPieChart.setExtraOffsets(5, 10, 5, 5);
            correctWrongPieChart.setDrawRoundedSlices(true);
            correctWrongPieChart.invalidate();
        }

        if(testResultsChosen.getSkippedCount() != 0 && testResultsChosen.getAnswerCount() != 0){

            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(testResultsChosen.getAnswerCount(), "Answered"));
            skippedAnsweredEntries.add(new PieEntry(testResultsChosen.getSkippedCount(), "Skipped"));

            answeredSkippedPieChart.setDescription(null);

            PieDataSet skippedAnsweredPieDataSet = new PieDataSet(skippedAnsweredEntries, "Answered vs. Skipped");
            skippedAnsweredPieDataSet.setDrawIcons(false);
            skippedAnsweredPieDataSet.setSliceSpace(5f);
            skippedAnsweredPieDataSet.setSelectionShift(8f);
            skippedAnsweredPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> skippedAnsweredColors = new ArrayList<>();
            skippedAnsweredColors.add(Color.MAGENTA);
            skippedAnsweredColors.add(Color.DKGRAY);
            skippedAnsweredPieDataSet.setColors(skippedAnsweredColors);

            PieData skippedAnsweredPieData = new PieData(skippedAnsweredPieDataSet);
            skippedAnsweredPieData.setValueTextColor(Color.BLACK);
            skippedAnsweredPieData.setValueTypeface(Typeface.DEFAULT);
            skippedAnsweredPieData.setValueTextSize(12);

            answeredSkippedPieChart.setData(skippedAnsweredPieData);
            answeredSkippedPieChart.animateXY(1400, 1400);
            answeredSkippedPieChart.setExtraOffsets(5, 10, 5, 5);
            answeredSkippedPieChart.setDrawRoundedSlices(true);
            answeredSkippedPieChart.invalidate();
        }

        if(testResultsChosen.getAnswerCount() == 0){
            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(testResultsChosen.getSkippedCount(), "Skipped"));

            answeredSkippedPieChart.setDescription(null);

            PieDataSet skippedAnsweredPieDataSet = new PieDataSet(skippedAnsweredEntries, "Answered vs. Skipped");
            skippedAnsweredPieDataSet.setDrawIcons(false);
            skippedAnsweredPieDataSet.setSliceSpace(5f);
            skippedAnsweredPieDataSet.setSelectionShift(8f);
            skippedAnsweredPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> skippedAnsweredColors = new ArrayList<>();
            skippedAnsweredColors.add(Color.DKGRAY);
            skippedAnsweredPieDataSet.setColors(skippedAnsweredColors);

            PieData skippedAnsweredPieData = new PieData(skippedAnsweredPieDataSet);
            skippedAnsweredPieData.setValueTextColor(Color.BLACK);
            skippedAnsweredPieData.setValueTypeface(Typeface.DEFAULT);
            skippedAnsweredPieData.setValueTextSize(12);

            answeredSkippedPieChart.setData(skippedAnsweredPieData);
            answeredSkippedPieChart.animateXY(1400, 1400);
            answeredSkippedPieChart.setExtraOffsets(5, 10, 5, 5);
            answeredSkippedPieChart.setDrawRoundedSlices(true);
            answeredSkippedPieChart.invalidate();
        }

        if(testResultsChosen.getSkippedCount() == 0){
            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(testResultsChosen.getAnswerCount(), "Answered"));

            answeredSkippedPieChart.setDescription(null);

            PieDataSet skippedAnsweredPieDataSet = new PieDataSet(skippedAnsweredEntries, "Answered vs. Skipped");
            skippedAnsweredPieDataSet.setDrawIcons(false);
            skippedAnsweredPieDataSet.setSliceSpace(5f);
            skippedAnsweredPieDataSet.setSelectionShift(8f);
            skippedAnsweredPieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            ArrayList<Integer> skippedAnsweredColors = new ArrayList<>();
            skippedAnsweredColors.add(Color.MAGENTA);
            skippedAnsweredPieDataSet.setColors(skippedAnsweredColors);

            PieData skippedAnsweredPieData = new PieData(skippedAnsweredPieDataSet);
            skippedAnsweredPieData.setValueTextColor(Color.BLACK);
            skippedAnsweredPieData.setValueTypeface(Typeface.DEFAULT);
            skippedAnsweredPieData.setValueTextSize(12);

            answeredSkippedPieChart.setData(skippedAnsweredPieData);
            answeredSkippedPieChart.animateXY(1400, 1400);
            answeredSkippedPieChart.setExtraOffsets(5, 10, 5, 5);
            answeredSkippedPieChart.setDrawRoundedSlices(true);
            answeredSkippedPieChart.invalidate();
        }

        TextView percentageTextView = findViewById(R.id.percentage_score_txt_vw);

        int questionCount = testResultsChosen.getQuestionsRight() + testResultsChosen.getQuestionsWrong();

        double scorePercent = ((double)testResultsChosen.getQuestionsRight()/questionCount) * 100;
        scorePercent = Math.round(scorePercent);

        percentageTextView.setText("Percentage Score: " + scorePercent + "%");

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference pastFlashcardTestCorrectQuestionsDatabaseRef = mFirebaseDatabase.getReference();

        // Get the test results from the test chosen
        pastFlashcardTestCorrectQuestionsDatabaseRef.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("testTaken").child(testResultsChosen.getDateAndTimeTaken()).child("correctFlashcards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Convert the data to a flashcardResults
                    FlashcardsResults correctFlashcards = data.getValue(FlashcardsResults.class);

                    // If the correctFlashcards has data in it
                    if(correctFlashcards != null){

                        // Get the correct question numbers
                        int flashcardNumber = correctFlashcards.getFlashcardNumber();

                        // Add the correct question number to the correct Questions array List
                        correctQuestionsArrayList.add(new FlashcardsResults(flashcardNumber));
                    }
                }

                updateCorrectList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pastFlashcardTestCorrectQuestionsDatabaseRef.keepSynced(true);

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference pastFlashcardTestWrongQuestionsDatabaseRef = mFirebaseDatabase.getReference();

        // Get the test results from the test chosen
        pastFlashcardTestWrongQuestionsDatabaseRef.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("testTaken").child(testResultsChosen.getDateAndTimeTaken()).child("wrongFlashcards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear the wrong question list to
                wrongQuestionsArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Convert the data to a flashcardResults
                    FlashcardsResults wrongFlashcards = data.getValue(FlashcardsResults.class);

                    // If the wrongFlashcards has data in it
                    if(wrongFlashcards != null){

                        // Get the wrong question numbers
                        int flashcardNumber = wrongFlashcards.getFlashcardNumber();

                        // Add the wrong question number to the correct Questions array List
                        wrongQuestionsArrayList.add(new FlashcardsResults(flashcardNumber));
                    }
                }

                updateWrongList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pastFlashcardTestWrongQuestionsDatabaseRef.keepSynced(true);
    }

    private void updateCorrectList(){

        EditText correctFlashcardEditText = findViewById(R.id.flashcard_correct_edit_text);

        StringBuilder correctStringBuilder = new StringBuilder();

        for(FlashcardsResults flashcardNumber: correctQuestionsArrayList){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            correctStringBuilder.append("\n").append(flashcardNumberString);
        }

        correctFlashcardEditText.setText(correctStringBuilder.toString());
    }

    private void updateWrongList(){

        EditText wrongFlashcardEditText = findViewById(R.id.flashcard_wrong_edit_text);

        StringBuilder wrongStringBuilder = new StringBuilder();

        for(FlashcardsResults flashcardNumber: wrongQuestionsArrayList){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            wrongStringBuilder.append("\n").append(flashcardNumberString);
        }

        wrongFlashcardEditText.setText(wrongStringBuilder.toString());
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
