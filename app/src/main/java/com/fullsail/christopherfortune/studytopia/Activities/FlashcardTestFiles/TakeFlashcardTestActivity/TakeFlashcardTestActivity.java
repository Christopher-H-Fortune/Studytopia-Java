package com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.TakeFlashcardTestActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LeaderboardFiles.FlaschcardTestLeaderboard.FlashcardTestLeaderboardActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardsResults.FlashcardsResults;
import com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard.Leaderboard;
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

public class TakeFlashcardTestActivity extends AppCompatActivity implements FlashcardTestFragment.FlashcardTestInterface, FlashcardTestResultsFragment.FlashcardTestResultsInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserData userProfileData = new UserData();
    private FlashcardTest testChosen;
    private String subjectChosenString;
    private ArrayList<Flashcards> flashcardsToTestArrayList = new ArrayList<>();
    private TextView questionTextView;
    private TextView questionNumberTextView;
    private EditText questionAnswerEditText;
    private Chronometer countUpChronometer;
    private int currentQuestion;
    private int correctCount;
    private int wrongCount;
    private int answeredCount;
    private int skippedCount;
    private ArrayList<FlashcardsResults> flashcardNumbersCorrect = new ArrayList<>();
    private ArrayList<FlashcardsResults> flashcardNumbersWrong = new ArrayList<>();
    private boolean isAnswerBeingChecked = false;
    private double scorePercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_flashcard_test);

        // Variables to keep track of the users test results
        currentQuestion = 0;
        correctCount = 0;
        wrongCount = 0;
        answeredCount = 0;
        skippedCount = 0;

        // Get the starting intent of the activity
        Intent startingIntent = getIntent();

        // If the starting intent has extras named flashcardTestChosen & subjectChosen
        if(startingIntent.hasExtra("flashcardTestChosen") && startingIntent.hasExtra("subjectChosen")){

            // Get the Flashcard Test passed from the intent
            testChosen = (FlashcardTest) startingIntent.getSerializableExtra("flashcardTestChosen");

            // Get the subject chosen string from the intent
            subjectChosenString = startingIntent.getStringExtra("subjectChosen");
        }

        // Get an instance of the firebase authentication and database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

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

        if(testChosen !=  null && user != null){

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference questionsReference = mFirebaseDatabase.getReference("/users/" + uId + "/userProfile/" + uId + "/flashcardTests/subjects/" + subjectChosenString + "/" + testChosen.getFlashcardTestId() + "/questions");

            // Set the database to have a ValueEventListener to display the games data to the user
            questionsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                            // Add the data above to the flashcardsToTestArrayList
                            flashcardsToTestArrayList.add(new Flashcards(question, answer, questionNumber));
                        }
                    }

                    displayFirstQuestion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            questionsReference.keepSynced(true);
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
                                Intent mapIntent = new Intent(TakeFlashcardTestActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TakeFlashcardTestActivity.this);
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
                        Intent studyGroupIntent = new Intent(TakeFlashcardTestActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(TakeFlashcardTestActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(TakeFlashcardTestActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(TakeFlashcardTestActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(TakeFlashcardTestActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(TakeFlashcardTestActivity.this, LoginSignupActivity.class);
                        startActivity(logoutIntent);
                }
                return true;
            }
        });

        // Set the supportActionBar with the toolbar above
        setSupportActionBar(toolbar);

        // Create an actionBar object
        actionbar = getSupportActionBar();

        // If the actionBar & testChosen isn't null
        if (actionbar != null && testChosen != null) {

            // Set the icon, title, and enable the actionBar to allow the user to view the navigation view
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>"+ testChosen.getFlashcardTestName() +" Test</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the FlashcardTestFragment to the user so they can take the test selected
        getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_frame, FlashcardTestFragment.newInstance(), FlashcardTestFragment.TAG).commit();
    }

    /**
     *
     * @param item
     * @return
     */
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
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.done_menu_button:

                if(testChosen.isFlashcardPublic() && scorePercent == 100.0){
                    Intent flashcardTestLeaderboardIntent = new Intent(this, FlashcardTestLeaderboardActivity.class);
                    flashcardTestLeaderboardIntent.putExtra("testId", testChosen.getFlashcardTestId());
                    flashcardTestLeaderboardIntent.putExtra("subject", subjectChosenString);
                    startActivity(flashcardTestLeaderboardIntent);
                } else {
                    Intent flashcardTestIntent = new Intent(this, FlashcardTestSubjectListActivity.class);
                    startActivity(flashcardTestIntent);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkAnswer() {

        isAnswerBeingChecked = true;

        final View testView = findViewById(R.id.test_frame_layout);

        final Button nextQuestionButton = findViewById(R.id.test_next_question_button);
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

                    getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();

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
            return;

        } else {

            isAnswerBeingChecked = true;

            questionAnswerEditText.setVisibility(View.INVISIBLE);

            nextQuestionButton.setVisibility(View.VISIBLE);

            String answerEntered = questionAnswerEditText.getText().toString().trim().toLowerCase();
            final String correctAnswer = flashcardsToTestArrayList.get(currentQuestion).getAnswer().trim().toLowerCase();

            if(answerEntered.equals(correctAnswer)){
                correctCount += 1;
                answeredCount += 1;

                testView.animate().withLayer().rotationY(360).setDuration(850).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        flashcardNumbersCorrect.add(new FlashcardsResults(currentQuestion));
                        questionTextView.setText(getString(R.string.correct));
                        isAnswerBeingChecked = true;
                    }
                }).start();

            } else {

                wrongCount +=1;
                answeredCount += 1;

                testView.animate().withLayer().rotationY(-360).setDuration(850).withEndAction(new Runnable() {
                    @Override
                    public void run() {

                        flashcardNumbersWrong.add(new FlashcardsResults(currentQuestion));
                        questionTextView.setText(getResources().getString(R.string.wrong_answer, correctAnswer));
                        isAnswerBeingChecked = true;
                    }
                }).start();
            }
        }
    }

    public void skipAnswer() {

        final View testView = findViewById(R.id.test_frame_layout);

        currentQuestion += 1;

        if(currentQuestion < flashcardsToTestArrayList.size()){
            wrongCount += 1;
            skippedCount += 1;

            testView.animate().withLayer().rotationY(-360).setDuration(850).withEndAction(new Runnable() {
                @Override
                public void run() {
                    flashcardNumbersWrong.add(new FlashcardsResults(currentQuestion - 1));

                    questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());

                    questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));
                }
            }).start();

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

            getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();
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
        this.countUpChronometer.start();
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

        double scorePercent = ((double)correctCount/flashcardsToTestArrayList.size()) * 100;
        scorePercent = Math.round(scorePercent);

        TestResults testResults = new TestResults(testChosen.getFlashcardTestName(), dateTimeString, dateString, timeString, correctCount, wrongCount, answeredCount, skippedCount);

        if(user != null){
            // Get the users ID
            final String uId = user.getUid();

            int correctQuestionNumber = 0;
            int wrongQuestionNumber = 0;

            // store the results to the database
            databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).setValue(testResults);

            for(FlashcardsResults flashcardsResults: flashcardNumbersCorrect){

                correctQuestionNumber += 1;

                String flashcardNumber = "Flashcard " + correctQuestionNumber;

                databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).child("correctFlashcards").child(flashcardNumber).setValue(flashcardsResults);
            }

            for(FlashcardsResults flashcardsResults: flashcardNumbersWrong){
                wrongQuestionNumber += 1;

                String flashcardNumber = "Flashcard " + wrongQuestionNumber;

                databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).child("wrongFlashcards").child(flashcardNumber).setValue(flashcardsResults);
            }

            if(testChosen.isFlashcardPublic() && scorePercent == 100.0){

                Leaderboard leaderboardResult = new Leaderboard(userProfileData.getUsername(), userProfileData.getImageUrl(),timeInMinutes, timeInSeconds, dateString, timeString);

                databaseReference.child("publicTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("leaderboard").child(user.getUid()).setValue(leaderboardResult);
            }
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
