package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupFlashcardTestActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LeaderboardFiles.StudyGroupLeaderboardActivity.StudyGroupLeaderboardActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardsResults.FlashcardsResults;
import com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard.Leaderboard;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupFlashcardTest.StudyGroupFlashcardTest;
import com.fullsail.christopherfortune.studytopia.DataModels.TestResults.TestResults;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestFragment.FlashcardTestFragment;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudyGroupFlashcardTestActivity extends AppCompatActivity implements FlashcardTestFragment.FlashcardTestInterface, FlashcardTestResultsFragment.FlashcardTestResultsInterface {

    private DrawerLayout drawerLayout;
    private FirebaseDatabase mFirebaseDatabase;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private  FirebaseUser user;
    private String testId;
    private String studyGroupId;
    private StudyGroupFlashcardTest studyGroupFlashcardTest;
    private int currentQuestion;
    private int correctCount;
    private int wrongCount;
    private int answeredCount;
    private int skippedCount;
    private TextView questionTextView;
    private TextView questionNumberTextView;
    private Chronometer countUpChronometer;
    private EditText questionAnswerEditText;
    private ArrayList<Flashcards> flashcardsToTestArrayList = new ArrayList<>();
    private ArrayList<FlashcardsResults> flashcardNumbersCorrect = new ArrayList<>();
    private ArrayList<FlashcardsResults> flashcardNumbersWrong = new ArrayList<>();
    private  double scorePercent;
    private boolean isAnswerBeingChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_flashcard_test);

        // Variables to keep track of the users test results
        currentQuestion = 0;
        correctCount = 0;
        wrongCount = 0;
        answeredCount = 0;
        skippedCount = 0;

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("testId")){
            testId = startingIntent.getStringExtra("testId");
            studyGroupId = startingIntent.getStringExtra("studyGroupId");
            studyGroupFlashcardTest = (StudyGroupFlashcardTest)startingIntent.getSerializableExtra("studyGroupTest");
        }

        // Set the instance of the Firebase auth and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if(user != null) {

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + uId);

            // Set the database to have a ValueEventListener to display the user data to the user
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

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference studyGroupQuestionsRef = mFirebaseDatabase.getReference();

        studyGroupQuestionsRef.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("testQuestions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear the arrayList to prevent duplicates
                flashcardsToTestArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the flashcardTest data
                    Flashcards flashcards = data.getValue(Flashcards.class);

                    // Make sure flashcardTest  isn't null
                    if (flashcards != null) {

                        // Get the data to create a flashcards object
                        String question = flashcards.getQuestion();
                        String answer = flashcards.getAnswer();
                        int questionNumber = flashcards.getQuestionNumber();

                        // Add the flashcard to the flashcardsArrayList
                        flashcardsToTestArrayList.add(new Flashcards(question, answer, questionNumber));
                    }
                }

                displayFirstQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        studyGroupQuestionsRef.keepSynced(true);

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
                                Intent mapIntent = new Intent(StudyGroupFlashcardTestActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupFlashcardTestActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupFlashcardTestActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupFlashcardTestActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupFlashcardTestActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupFlashcardTestActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupFlashcardTestActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupFlashcardTestActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Study Group Test</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_frame, FlashcardTestFragment.newInstance(), FlashcardTestFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(StudyGroupFlashcardTestActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.done_menu_button:

                if(scorePercent == 100.0){

                    Intent flashcardTestLeaderboardIntent = new Intent(this, StudyGroupLeaderboardActivity.class);
                    flashcardTestLeaderboardIntent.putExtra("testId", studyGroupFlashcardTest.getFlashcardTestId());
                    flashcardTestLeaderboardIntent.putExtra("studyGroupId", studyGroupId);
                    startActivity(flashcardTestLeaderboardIntent);

                } else {

                    Intent studyGroupIntent = new Intent(this, StudyGroupActivity.class);
                    startActivity(studyGroupIntent);
                }

                return true;
        }
        return true;
    }

    public void checkAnswer() {

        isAnswerBeingChecked = true;

        Button nextQuestionButton = findViewById(R.id.test_next_question_button);

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentQuestion += 1;

                if(currentQuestion >= flashcardsToTestArrayList.size()){

                    countUpChronometer.stop();

                    long timerCount = SystemClock.elapsedRealtime() - countUpChronometer.getBase();

                    long totalTimeInSeconds = timerCount / 1000;

                    long timeInMinutes = totalTimeInSeconds / 60;

                    long secondsLeft = totalTimeInSeconds % 60;

                    saveFlashcardTest(timeInMinutes, secondsLeft);

                    getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();

                } else {

                    isAnswerBeingChecked = false;

                    questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());
                    Button nextQuestionButton = findViewById(R.id.test_next_question_button);
                    nextQuestionButton.setVisibility(View.INVISIBLE);
                    EditText answerEditText = findViewById(R.id.test_answer_edt_txt);
                    answerEditText.setVisibility(View.VISIBLE);
                    answerEditText.setText(null);
                    questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));
                }
            }
        });

        if(questionAnswerEditText.getText().toString().trim().equals("")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Please Enter a Answer");
            alertDialogBuilder.setMessage("To check your answer, please enter an answer");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton(
                    "Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                            isAnswerBeingChecked = false;
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        } else {

            isAnswerBeingChecked = true;

            questionAnswerEditText.setVisibility(View.INVISIBLE);

            nextQuestionButton.setVisibility(View.VISIBLE);

            String answerEntered = questionAnswerEditText.getText().toString().trim().toLowerCase();
            String correctAnswer = flashcardsToTestArrayList.get(currentQuestion).getAnswer().trim().toLowerCase();

            if(answerEntered.equals(correctAnswer)){

                correctCount += 1;
                answeredCount += 1;
                flashcardNumbersCorrect.add(new FlashcardsResults(currentQuestion));
                questionTextView.setText(getString(R.string.correct));
                isAnswerBeingChecked = true;

            } else {

                wrongCount +=1;
                answeredCount += 1;
                flashcardNumbersWrong.add(new FlashcardsResults(currentQuestion));
                questionTextView.setText(getResources().getString(R.string.wrong_answer, correctAnswer));
                isAnswerBeingChecked = true;
            }
        }
    }

    public void skipAnswer() {
        currentQuestion += 1;

        if(currentQuestion < flashcardsToTestArrayList.size()){
            wrongCount += 1;
            skippedCount += 1;

            flashcardNumbersWrong.add(new FlashcardsResults(currentQuestion - 1));

            questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());

            questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));

        } else if (currentQuestion == flashcardsToTestArrayList.size()){
            skippedCount += 1;
            wrongCount += 1;

            countUpChronometer.stop();

            long timerCount = SystemClock.elapsedRealtime() - countUpChronometer.getBase();

            long totalTimeInSeconds = timerCount / 1000;

            long timeInMinutes = totalTimeInSeconds / 60;

            long secondsLeft = totalTimeInSeconds % 60;

            flashcardNumbersWrong.add(new FlashcardsResults(currentQuestion - 1));

            saveFlashcardTest(timeInMinutes, secondsLeft);

            getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();
        }
    }

    @Override
    public void passFlashcardTestView(TextView questionTextView, TextView questionNumberTextView, EditText questionAnswerEditText, Button checkAnswerButton, Button skipButton) {
        // Store the textViews to display the questions and their corresponding numbers to the user
        this.questionTextView = questionTextView;
        this.questionNumberTextView = questionNumberTextView;
        this.questionAnswerEditText = questionAnswerEditText;
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAnswerBeingChecked){
                    checkAnswer();
                }
            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAnswerBeingChecked){
                    skipAnswer();
                }
            }
        });
    }

    @Override
    public void startChronometer(Chronometer countUpChronometer) {

        this.countUpChronometer = countUpChronometer;
        countUpChronometer.start();
    }

    private void displayFirstQuestion(){
        questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());
        questionNumberTextView.setText(getResources().getString(R.string.question_number, flashcardsToTestArrayList.get(currentQuestion).getQuestionNumber()));
        questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));
    }

    private void saveFlashcardTest(long timeInMinutes, long timeInSeconds){
        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        Calendar calendar = Calendar.getInstance();

        Date currentDate = calendar.getTime();

        DateFormat dateWithTimeFormat = new SimpleDateFormat("MM-dd-yyy HH:mm:ss", Locale.US);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy", Locale.US);

        String dateTimeString = dateWithTimeFormat.format(currentDate);
        String timeString = timeFormat.format(currentDate);
        String dateString = dateFormat.format(currentDate);

        TestResults testResults = new TestResults(studyGroupFlashcardTest.getFlashcardTestName(), dateTimeString, dateString, timeString, correctCount, wrongCount, answeredCount, skippedCount);

        int correctQuestionNumber = 0;
        int wrongQuestionNumber = 0;

        // store the results to the database
        databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("testResults").child(dateTimeString).setValue(testResults);

        for(FlashcardsResults flashcardsResults: flashcardNumbersCorrect){

            correctQuestionNumber += 1;

            String flashcardNumber = "Flashcard " + correctQuestionNumber;

            databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("testResults").child(dateTimeString).child("correctFlashcards").child(flashcardNumber).setValue(flashcardsResults);
        }

        for(FlashcardsResults flashcardsResults: flashcardNumbersWrong){
            wrongQuestionNumber += 1;

            String flashcardNumber = "Flashcard " + wrongQuestionNumber;

            databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("testResults").child(dateTimeString).child("wrongFlashcards").child(flashcardNumber).setValue(flashcardsResults);
        }

        double scorePercent = ((double)correctCount/flashcardsToTestArrayList.size()) * 100;
        scorePercent = Math.round(scorePercent);

        if(scorePercent == 100.0){

            Leaderboard leaderboardResult = new Leaderboard(userProfileData.getUsername(), userProfileData.getImageUrl(), timeInMinutes, timeInSeconds, dateString, timeString);

            databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("leaderboard").child(user.getUid()).setValue(leaderboardResult);
        }
    }

    @Override
    public void passViews(PieChart correctWrongPieChart, PieChart answeredSkippedPieChart) {
        if(correctCount != 0 && wrongCount != 0){

            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(correctCount, "Correct"));
            correctWrongEntries.add(new PieEntry(wrongCount, getString(R.string.wrong)));

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

        if(wrongCount == 0){
            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(correctCount, "Correct"));

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

        if(correctCount == 0){
            List<PieEntry> correctWrongEntries = new ArrayList<>();
            correctWrongEntries.add(new PieEntry(wrongCount, getString(R.string.wrong)));

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

        if(skippedCount != 0 && answeredCount != 0){

            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(answeredCount, "Answered"));
            skippedAnsweredEntries.add(new PieEntry(skippedCount, "Skipped"));

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

        if(answeredCount == 0){
            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(skippedCount, "Skipped"));

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

        if(skippedCount == 0){
            List<PieEntry> skippedAnsweredEntries = new ArrayList<>();
            skippedAnsweredEntries.add(new PieEntry(answeredCount, "Answered"));

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

        scorePercent = ((double)correctCount/flashcardsToTestArrayList.size()) * 100;
        scorePercent = Math.round(scorePercent);

        percentageTextView.setText("Percentage Score: " + scorePercent + "%");

        EditText correctFlashcardEditText = findViewById(R.id.flashcard_correct_edit_text);
        EditText wrongFlashcardEditText = findViewById(R.id.flashcard_wrong_edit_text);

        StringBuilder correctStringBuilder = new StringBuilder();
        StringBuilder wrongStringBuilder = new StringBuilder();

        for(FlashcardsResults flashcardNumber: flashcardNumbersCorrect){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            correctStringBuilder.append("\n").append(flashcardNumberString);
        }

        for(FlashcardsResults flashcardNumber: flashcardNumbersWrong){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            wrongStringBuilder.append("\n").append(flashcardNumberString);
        }

        correctFlashcardEditText.setText(correctStringBuilder.toString());
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
