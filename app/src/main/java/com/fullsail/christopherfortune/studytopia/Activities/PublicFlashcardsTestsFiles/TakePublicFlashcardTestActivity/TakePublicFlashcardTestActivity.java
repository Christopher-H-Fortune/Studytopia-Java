package com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.TakePublicFlashcardTestActivity;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.LeaderboardFiles.FlaschcardTestLeaderboard.FlashcardTestLeaderboardActivity;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardsResults.FlashcardsResults;
import com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard.Leaderboard;
import com.fullsail.christopherfortune.studytopia.DataModels.PublicFlashcardTests.PublicFlashcardTests;
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

public class TakePublicFlashcardTestActivity extends AppCompatActivity implements FlashcardTestFragment.FlashcardTestInterface, FlashcardTestResultsFragment.FlashcardTestResultsInterface {

    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private DrawerLayout drawerLayout;
    private UserData userProfileData = new UserData();
    private ArrayList<Flashcards> flashcardsToTestArrayList = new ArrayList<>();
    private TextView questionTextView;
    private TextView questionNumberTextView;
    private EditText questionAnswerEditText;
    private Chronometer countUpChronometer;
    String subjectChosen;
    PublicFlashcardTests testChosen;
    private int currentQuestion;
    private int correctCount;
    private int wrongCount;
    private int answeredCount;
    private int skippedCount;
    private double scorePercent;
    private ArrayList<FlashcardsResults> flashcardQuestionsCorrect = new ArrayList<>();
    private ArrayList<FlashcardsResults> flashcardQuestionsWrong = new ArrayList<>();
    private boolean isTestInDatabase;
    private boolean isAnswerBeingChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_public_flashcard_test);

        currentQuestion = 0;
        correctCount = 0;
        wrongCount = 0;
        answeredCount = 0;
        skippedCount = 0;

        // Get the starting intent of the activity
        Intent startingIntent = getIntent();

        // If the starting intent has an extra named testChosen & subjectChosen
        if(startingIntent.hasExtra("testChosen") & startingIntent.hasExtra("subjectChosen")){

            // Get the subject string and test object passed from the intent
            testChosen = (PublicFlashcardTests)startingIntent.getSerializableExtra("testChosen");
            subjectChosen = startingIntent.getStringExtra("subjectChosen");
        }

        // Get an instance of the firebase authentication and database
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

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference userTestsRef = mFirebaseDatabase.getReference();

            userTestsRef.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("subjects").child(subjectChosen).child(testChosen.getFlashcardTestId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Get the flashcard test question data
                    FlashcardTest flashcardTests = dataSnapshot.getValue(FlashcardTest.class);

                    // Make sure flashcardTests isn't null
                    if (flashcardTests != null) {

                        isTestInDatabase = true;

                    } else {

                        isTestInDatabase = false;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            userTestsRef.keepSynced(true);
        }

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference publicFlashcardQuestionsRef = mFirebaseDatabase.getReference();

        publicFlashcardQuestionsRef.child("publicTests").child("subjects").child(subjectChosen).child(testChosen.getFlashcardTestId()).child("questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through each object obtained
                for(DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the flashcard test question data
                    Flashcards flashcardQuestions = data.getValue(Flashcards.class);

                    // Make sure flashcardQuestions isn't null
                    if (flashcardQuestions != null) {

                        // Get the data to create a flashcard object
                        String question = flashcardQuestions.getQuestion();
                        String answer = flashcardQuestions.getAnswer();
                        int questionNumber = flashcardQuestions.getQuestionNumber();

                        // Add the PublicFlashcardTests to the publicFlashcardTestsArrayList
                        flashcardsToTestArrayList.add(new Flashcards(question, answer, questionNumber));
                    }
                }

                if(flashcardsToTestArrayList.size() != 0){
                    displayFirstQuestion();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        publicFlashcardQuestionsRef.keepSynced(true);

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
                                Intent mapIntent = new Intent(TakePublicFlashcardTestActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TakePublicFlashcardTestActivity.this);
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
                        Intent studyGroupIntent = new Intent(TakePublicFlashcardTestActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(TakePublicFlashcardTestActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(TakePublicFlashcardTestActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(TakePublicFlashcardTestActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsIntent = new Intent(TakePublicFlashcardTestActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(TakePublicFlashcardTestActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + testChosen.getFlashcardTestName() + " Test</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the PublicFlashcardsFragment to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.public_flashcards_test_taking_frame, FlashcardTestFragment.newInstance(), FlashcardTestFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(TakePublicFlashcardTestActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.done_menu_button:

                if(testChosen.isFlashcardPublic() && scorePercent == 100.0){

                    Intent flashcardTestLeaderboardIntent = new Intent(this, FlashcardTestLeaderboardActivity.class);
                    flashcardTestLeaderboardIntent.putExtra("testId", testChosen.getFlashcardTestId());
                    flashcardTestLeaderboardIntent.putExtra("subject", subjectChosen);
                    startActivity(flashcardTestLeaderboardIntent);

                } else if (testChosen.isFlashcardPublic() && scorePercent < 100.0){

                    Intent publichFlashCardTest = new Intent(this, PublicFlashcardsActivity.class);
                    startActivity(publichFlashCardTest);

                } else if (!testChosen.isFlashcardPublic()){

                    Intent flashCardTestSubjectsIntent = new Intent(this, FlashcardTestSubjectListActivity.class);
                    startActivity(flashCardTestSubjectsIntent);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkAnswer() {

        isAnswerBeingChecked = true;

        // Get the next question button to allow the user to navigate to the next question
        final Button nextQuestionButton = findViewById(R.id.test_next_question_button);

        // On click listener for the next button to allow the user to display the next question
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Add One to the current question
                currentQuestion += 1;

                // If there are no questions left for the user to
                if(currentQuestion >= flashcardsToTestArrayList.size()){

                    countUpChronometer.stop();

                    long timerCount = SystemClock.elapsedRealtime() - countUpChronometer.getBase();

                    long totalTimeInSeconds = timerCount / 1000;

                    long timeInMinutes = totalTimeInSeconds / 60;

                    long secondsLeft = totalTimeInSeconds % 60;

                    saveFlashcardTestResults(timeInMinutes, secondsLeft);

                    // Display the test results to the user
                    getSupportFragmentManager().beginTransaction().replace(R.id.public_flashcards_test_taking_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();

                    LinearLayout skipCheckImageLayout = findViewById(R.id.check_skip_image_layout);
                    LinearLayout skipCheckButtonLayout = findViewById(R.id.check_skip_button_layout);

                    skipCheckImageLayout.setVisibility(View.INVISIBLE);
                    skipCheckButtonLayout.setVisibility(View.INVISIBLE);

                    // If there are questions left for the user to answer
                } else {

                    // Display the next question to the user
                    questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());

                    isAnswerBeingChecked = false;

                    // Hide the next button to display the answer edit text
                    nextQuestionButton.setVisibility(View.INVISIBLE);

                    // Display the answer edit text to allow the user to enter the answer for the
                    questionAnswerEditText.setVisibility(View.VISIBLE);

                    // Clear the answer edit text field
                    questionAnswerEditText.setText(null);

                    // Display the new question number to the user
                    questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));
                }
            }
        });

        // If the user didn't enter an answer
        if(questionAnswerEditText.getText().toString().trim().equals("")){

            // Ask the user to enter an answer
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

        // If the user entered an answer to check
        } else {

            isAnswerBeingChecked = true;

            // Hide the answer edit text to display the next question button
            questionAnswerEditText.setVisibility(View.INVISIBLE);

            // Display the next question button to the user
            nextQuestionButton.setVisibility(View.VISIBLE);

            // Get the user entered answer
            String answerEntered = questionAnswerEditText.getText().toString().trim().toLowerCase();

            // Get the correct answer(the answer assigned to the question when creating the test questions)
            String correctAnswer = flashcardsToTestArrayList.get(currentQuestion).getAnswer().trim().toLowerCase();

            // If the user entered the correct answer
            if(answerEntered.equals(correctAnswer)){

                // Add one to the correct and answered count
                correctCount += 1;
                answeredCount += 1;

                // Add the current question number to the flashcard questions correct array list
                flashcardQuestionsCorrect.add(new FlashcardsResults(currentQuestion));

                // Display to the user they got the question correct
                questionTextView.setText(getString(R.string.correct));

                isAnswerBeingChecked = true;

                // If the user entered the wrong answer
            } else {

                // Add one to the wrong and answered count
                wrongCount += 1;
                answeredCount += 1;

                // Add the question number to the flashcardQuestionsWrong array list
                flashcardQuestionsWrong.add(new FlashcardsResults(currentQuestion));

                // Display the user got the question wrong and display the correct answer to the user
                questionTextView.setText(getResources().getString(R.string.wrong_answer, correctAnswer));

                isAnswerBeingChecked = true;
            }
        }
    }

    public void skipAnswer() {

        // Add one to the current questions
        currentQuestion += 1;

        // If there are more questions for the user to answer
        if(currentQuestion < flashcardsToTestArrayList.size()){

            // Add one to the wrong and skipped count
            wrongCount += 1;
            skippedCount += 1;

            // Add the question number wrong to the flashcardQuestionsWrong array list
            flashcardQuestionsWrong.add(new FlashcardsResults(currentQuestion - 1));

            // Display the next question to the user to answer
            questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());

            // Display the question number of the next question displayed
            questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));

            // If there are no more questions for the user to
        } else if (currentQuestion == flashcardsToTestArrayList.size()){

            // Add one to the skipped and wrong count
            skippedCount += 1;
            wrongCount += 1;

            countUpChronometer.stop();

            long timerCount = SystemClock.elapsedRealtime() - countUpChronometer.getBase();

            long totalTimeInSeconds = timerCount / 1000;

            long timeInMinutes = totalTimeInSeconds / 60;

            long secondsLeft = totalTimeInSeconds % 60;

            // Add the question number wrong to the flashcardQuestionsWrong array list
            flashcardQuestionsWrong.add(new FlashcardsResults(currentQuestion - 1));

            // Call the saveFlashcardTestResults method to save the test results
            saveFlashcardTestResults(timeInMinutes, secondsLeft);

            // Display the flashcard test results to the user
            getSupportFragmentManager().beginTransaction().replace(R.id.public_flashcards_test_taking_frame, FlashcardTestResultsFragment.newInstance(), FlashcardTestResultsFragment.TAG).commit();

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

        // Display the first question to the user
        questionTextView.setText(flashcardsToTestArrayList.get(currentQuestion).getQuestion());

        // Display the question number to the user
        questionNumberTextView.setText(getResources().getString(R.string.question_number, currentQuestion + 1));
    }

    private void saveFlashcardTestResults(long timeInMinutes, long timeInSeconds){

        // Calendar object to get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Get the current date and time from the calendar object created above
        Date currentDate = calendar.getTime();

        // Format the current date and time to the MM-dd-yyy HH:mm:ss format
        DateFormat dateWithTimeFormat = new SimpleDateFormat("MM-dd-yyy HH:mm:ss", Locale.US);

        // Format the current time to the HH:mm:ss format
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

        // Format the current date to the MM-dd-yyy format
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy", Locale.US);

        // Store the dateWithTimeFormat, timeFormat, and dateFormat as a string
        final String dateTimeString = dateWithTimeFormat.format(currentDate);
        String timeString = timeFormat.format(currentDate);
        String dateString = dateFormat.format(currentDate);

        // Create a testResults object to save the results to the firebase database
        final TestResults testResults = new TestResults(testChosen.getFlashcardTestName(), dateTimeString, dateString, timeString, correctCount, wrongCount, answeredCount, skippedCount);

        // If the current user isn't null
        if(user != null){

            // If the test being taken has been created by the user
            if(userProfileData.getUsername().equals(testChosen.getFlashcardTestCreator())){

                savedToTestAlreadyPresent(dateTimeString, testResults, timeInMinutes, timeInSeconds, timeString, dateString);

            // If the test being taken hasn't been created by the user
            } else {

                // If the test the user just took is saved to their profile
                if(isTestInDatabase){

                    // Call the savedToTestAlreadyPresent method to save the test results to the database
                    savedToTestAlreadyPresent(dateTimeString, testResults, timeInMinutes, timeInSeconds, timeString, dateString);

                // If the test the user just took isn't saved to their profile
                } else {

                    // Create a FlashcardTest object from the testChosen PublicFlashcardTests object
                    FlashcardTest flashcardTestToCreate = new FlashcardTest(testChosen.getFlashcardTestName(),
                            testChosen.getFlashcardCount(),
                            testChosen.isFlashcardPublic(),
                            testChosen.getFlashcardCreationDate(),
                            testChosen.getFlashcardTestId(),
                            testChosen.getFlashcardTestCreator());

                    // Set a reference to our Firebase database
                    DatabaseReference databaseReference = mFirebaseDatabase.getReference();

                    // Store the flashcardTest object above to the database
                    databaseReference.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("subjects").child(subjectChosen).child(testChosen.getFlashcardTestId()).setValue(flashcardTestToCreate);

                    // Save the test results to the test created above
                    savedToTestAlreadyPresent(dateTimeString, testResults, timeInMinutes, timeInSeconds, timeString, dateString);
                }

            }
        }
    }

    private void savedToTestAlreadyPresent(String dateTimeString, TestResults testResults, long timeInMinutes, long timeInSeconds, String timeString, String dateString){

        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        // Get the users ID
        final String uId = user.getUid();

        // Correct and wrong question number markers
        int correctQuestionNumber = 0;
        int wrongQuestionNumber = 0;

        // Store the results to the database
        databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).setValue(testResults);

        for(FlashcardsResults flashcardsResults: flashcardQuestionsCorrect){

            correctQuestionNumber += 1;

            String flashcardNumber = "Flashcard " + correctQuestionNumber;

            databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).child("correctFlashcards").child(flashcardNumber).setValue(flashcardsResults);
        }

        for(FlashcardsResults flashcardsResults: flashcardQuestionsWrong){
            wrongQuestionNumber += 1;

            String flashcardNumber = "Flashcard " + wrongQuestionNumber;

            databaseReference.child("users").child(uId).child("userProfile").child(uId).child("flashcardTests").child("testTaken").child(dateTimeString).child("wrongFlashcards").child(flashcardNumber).setValue(flashcardsResults);
        }

        double scorePercent = ((double)correctCount/flashcardsToTestArrayList.size()) * 100;
        scorePercent = Math.round(scorePercent);

        if(scorePercent == 100.0){

            Leaderboard leaderboardResult = new Leaderboard(userProfileData.getUsername(), userProfileData.getImageUrl(), timeInMinutes, timeInSeconds, dateString, timeString);

            databaseReference.child("publicTests").child("subjects").child(subjectChosen).child(testChosen.getFlashcardTestId()).child("leaderboard").child(user.getUid()).setValue(leaderboardResult);
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

        for(FlashcardsResults flashcardNumber: flashcardQuestionsCorrect){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            correctStringBuilder.append("\n").append(flashcardNumberString);
        }

        for(FlashcardsResults flashcardNumber: flashcardQuestionsWrong){
            int flashcard = flashcardNumber.getFlashcardNumber() + 1;

            String flashcardNumberString = "Question " + flashcard;

            wrongStringBuilder.append("\n").append(flashcardNumberString);
        }

        correctFlashcardEditText.setText(correctStringBuilder.toString());
        wrongFlashcardEditText.setText(wrongStringBuilder.toString());
    }
}
