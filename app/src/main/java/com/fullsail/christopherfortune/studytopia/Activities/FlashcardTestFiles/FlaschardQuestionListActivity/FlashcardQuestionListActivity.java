package com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlaschardQuestionListActivity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.QuestionListAdapter.QuestionListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationEditFragment.FlashcardTestCreationEditFragment;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Fragments.QuestionsListFragment.FlashcardTestQuestionsListFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
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

import java.util.ArrayList;

public class FlashcardQuestionListActivity extends AppCompatActivity implements FlashcardTestQuestionsListFragment.FlashcardTestQuestionsListInterface, FlashcardTestCreationEditFragment.FlashcardTestCreationEditInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseUser user;
    private UserData userProfileData = new UserData();
    private FirebaseDatabase mFirebaseDatabase;
    private ListView questionListView;
    private ArrayList<Flashcards> flashcardsArrayList = new ArrayList<>();
    private ArrayList<Flashcards> publicFlashcardsArrayList = new ArrayList<>();
    private ArrayList<Flashcards> flashcardsToSave = new ArrayList<>();
    private FlashcardTest testChosen;
    private String subjectChosenString;
    private TextView questionNumberTxtVw;
    private int questionSelected = 1000;
    private int questionTracker = 1000;
    private boolean clearText = false;
    EditText questionEditText;
    EditText answerEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_question_list);

        // Get the starting intent
        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("flashcardTestChosen")){

            testChosen = (FlashcardTest) startingIntent.getSerializableExtra("flashcardTestChosen");
            subjectChosenString = startingIntent.getStringExtra("subjectChosen").toLowerCase();

        } else if (startingIntent.hasExtra("publicFlashcardTestChosen")){

            testChosen = (FlashcardTest) startingIntent.getSerializableExtra("publicFlashcardTestChosen");
            subjectChosenString = startingIntent.getStringExtra("subjectChosen").toLowerCase();
        }

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

            // If the user chose a test
            if(testChosen != null){

                // Get the test Name chosen
                String testId = testChosen.getFlashcardTestId();

                // Set the database reference using the mFirebaseDatabase
                DatabaseReference mFlashcardQuestionsReference = mFirebaseDatabase.getReference("/users/" + uId + "/userProfile/" + uId + "/flashcardTests/subjects/" + subjectChosenString + "/" + testId + "/questions");
                mFlashcardQuestionsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Clear the arrayList to prevent duplicates
                        flashcardsArrayList.clear();
                        flashcardsToSave.clear();

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
                                flashcardsArrayList.add(new Flashcards(question, answer, questionNumber));
                            }
                        }

                        // Call the displayQuestionListMethod to display the questions for the test selected
                        displayQuestionList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                mFlashcardQuestionsReference.keepSynced(true);

                // Set the database reference using the mFirebaseDatabase
                DatabaseReference mPublicFlashcardQuestionsReference = mFirebaseDatabase.getReference("/publicTests/subjects/" + subjectChosenString + "/" + testChosen.getFlashcardTestId() + "/questions");
                mPublicFlashcardQuestionsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Clear the publicFlashcardsArrayList to prevent duplicates
                        publicFlashcardsArrayList.clear();

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

                                // Add the Flashcards object to the publicFlashcardsArrayList
                                publicFlashcardsArrayList.add(new Flashcards(question, answer, questionNumber));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mPublicFlashcardQuestionsReference.keepSynced(true);
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
                    switch (menuItem.getItemId()) {
                        case R.id.nav_study_group_map:

                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = null;
                            if (connectivityManager != null) {
                                networkInfo = connectivityManager.getActiveNetworkInfo();
                            }

                            if(networkInfo != null){

                                if(networkInfo.isConnected()){
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                    Intent mapIntent = new Intent(FlashcardQuestionListActivity.this, StudyGroupMapActivity.class);
                                    startActivity(mapIntent);
                                }

                            } else {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardQuestionListActivity.this);
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
                            Intent studyGroupIntent = new Intent(FlashcardQuestionListActivity.this, StudyGroupActivity.class);
                            startActivity(studyGroupIntent);
                            break;
                        case R.id.nav_topic_of_the_day:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent topicOfDayIntent = new Intent(FlashcardQuestionListActivity.this, TopicOfTheDayActivity.class);
                            startActivity(topicOfDayIntent);
                            break;
                        case R.id.nav_forums:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent forumsSubjectListIntent = new Intent(FlashcardQuestionListActivity.this, ForumsSubjectListActivity.class);
                            startActivity(forumsSubjectListIntent);
                            break;
                        case R.id.nav_public_flashcards:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent publicFlashcardsIntent = new Intent(FlashcardQuestionListActivity.this, PublicFlashcardsCategoryListActivity.class);
                            startActivity(publicFlashcardsIntent);
                            break;
                        case R.id.nav_flashcards:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent flashcardsListIntent = new Intent(FlashcardQuestionListActivity.this, FlashcardTestSubjectListActivity.class);
                            startActivity(flashcardsListIntent);
                            break;
                        case R.id.nav_logout:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent logoutIntent = new Intent(FlashcardQuestionListActivity.this, LoginSignupActivity.class);
                            startActivity(logoutIntent);
                            break;
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
                actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Flashcard Test Questions</font>", Html.FROM_HTML_MODE_LEGACY));
            }

            // Display the FlashcardTestQuestionsListFragment to the user
            getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

        }
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
                            Intent profileIntent = new Intent(FlashcardQuestionListActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.add_question:

                // If the user can add another question to the test without going over the card count on the test
                if (flashcardsArrayList.size() < testChosen.getFlashcardCount()) {

                    // Display the FlashcardTestCreationEditFragment to the user so they can add another question
                    getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestCreationEditFragment.newInstance(), FlashcardTestCreationEditFragment.TAG).commit();

                    questionTracker = flashcardsArrayList.size() + 1;
                }
                return true;
            case R.id.save_questions:

                // Call the saveQuestions method to save the questions the user entered
                saveQuestions();
                return true;

            case R.id.delete_question:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passQuestionListView(ListView questionListView) {

        // Store the listView to display the data to the user
        this.questionListView = questionListView;
    }

    @Override
    public void questionSelection(int questionSelected) {

        // Display the FlashcardCreatingEditFragment to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestCreationEditFragment.newInstance(), FlashcardTestCreationEditFragment.TAG).commit();

        // Set the question selected to the question chosen from the list view
        this.questionSelected = questionSelected;
    }

    private void updateQuestionList(){

    }

    private void displayQuestionList(){

        // pass the data obtained from the database to the flashcardsToSave array
        flashcardsToSave = flashcardsArrayList;

        // Question list adapter to display the questions from the test selected
        QuestionListAdapter questionListAdapter = new QuestionListAdapter(this, R.layout.question_row, flashcardsToSave);

        // Set the adapter created above to the question list view
        questionListView.setAdapter(questionListAdapter);

        // NotifyDataSetChanged to the adapter created above
        questionListAdapter.notifyDataSetChanged();

    }

    @Override
    public void viewQuestionsList() {

        // Get the question and answer edit text
        this.questionEditText = findViewById(R.id.new_question_edt_txt);
        this.answerEditText = findViewById(R.id.new_answer_edt_txt);

        // Get the data the user entered to the edit text fields above
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        // If the question and answer isn't null
        if(!question.equals("") && !answer.equals("")){

            // Create a flashcard object from the user entered data
            Flashcards flashcard = new Flashcards(question, answer, flashcardsToSave.size() + 1);

            // Add the flashcard the user entered to the array list
            flashcardsToSave.add(flashcard);

            // Display the FlashcardTestQuestionsListFragment to the user to view their questions
            getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

            // Call the saveQuestions method to allow the user to save the questions to the database
            saveQuestions();

            // Call the updateQuestionList method to display the new questions entered
            updateQuestionList();

            questionTracker = 1000;
            questionSelected = 1000;

        // If the user didn't enter a question and answer
        } else {

            // Display the FlashcardTestQuestionsListFragment to the user to view their questions
            getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

            // Call the saveQuestions method to save the current questions to the database
            saveQuestions();

            // Call the updateQuestionList method to display the new questions entered
            updateQuestionList();

            questionTracker = 1000;
            questionSelected = 1000;
        }

    }

    @Override
    public void nextQuestion() {

        final View questionEditView = findViewById(R.id.question_edit_frame_layout);

        // Get the question and answer edit text
        final EditText questionEditText = findViewById(R.id.new_question_edt_txt);
        final EditText answerEditText = findViewById(R.id.new_answer_edt_txt);

        if(clearText){
            questionEditText.setText(null);
            answerEditText.setText(null);
        }

        // Get the data the user entered to the edit text fields above
        String question = questionEditText.getText().toString().trim();
        String answer = answerEditText.getText().toString().trim();

        if(!question.isEmpty() && !answer.isEmpty() && questionTracker + 1 <= testChosen.getFlashcardCount()){

            questionTracker += 1;

            // Create a flashcard object from the data the user entered
            Flashcards flashcard = new Flashcards(question, answer, flashcardsToSave.size() + 1);

            // Save the flashcard entered to the arrayList to display to the user
            flashcardsToSave.add(flashcard);

            questionEditView.animate().withLayer().rotationY(360).setDuration(550).withEndAction(new Runnable() {
                @Override
                public void run() {

                    // Display the question number to the user
                    String questionNumberString = "Question " + questionTracker;
                    questionNumberTxtVw.setText(questionNumberString);

                    // Clear the question and answer edit text
                    questionEditText.setText(null);
                    answerEditText.setText(null);
                }
            }).start();

        } else if (questionTracker + 1 <= flashcardsToSave.size() && clearText) {

            questionTracker += 1;

            questionEditView.animate().withLayer().rotationY(360).setDuration(550).withEndAction(new Runnable() {
                @Override
                public void run() {

                    // String to display the current question to the user
                    String questionNumberString = "Question " + (questionTracker);

                    // Display the current question number to the user
                    questionNumberTxtVw.setText(questionNumberString);

                    questionEditText.setText(flashcardsToSave.get(questionTracker - 1).getQuestion());
                    answerEditText.setText(flashcardsToSave.get(questionTracker - 1).getAnswer());
                }
            }).start();

        } else if (questionTracker + 1  <= testChosen.getFlashcardCount() && clearText) {

            questionTracker += 1;

            questionEditView.animate().withLayer().rotationY(360).setDuration(550).withEndAction(new Runnable() {
                @Override
                public void run() {
                    clearText = false;

                    questionEditText.setText(null);
                    answerEditText.setText(null);

                    // String to display the current question to the user
                    String questionNumberString = "Question " + (questionTracker);

                    // Display the current question number to the user
                    questionNumberTxtVw.setText(questionNumberString);
                }
            }).start();

        }
    }

    @Override
    public void previousQuestion() {

        final View questionEditView = findViewById(R.id.question_edit_frame_layout);

        // Get the question and answer edit text
        final EditText questionEditText = findViewById(R.id.new_question_edt_txt);
        final EditText answerEditText = findViewById(R.id.new_answer_edt_txt);

        // If there is a previous question to go to
        if(questionTracker - 1 >= 0){

            questionTracker -= 1;

            questionEditView.animate().withLayer().rotationY(-360).setDuration(550).withEndAction(new Runnable() {
                @Override
                public void run() {

                    // Display the question and answer to the user
                    questionEditText.setText(flashcardsToSave.get(questionTracker - 1).getQuestion());
                    answerEditText.setText(flashcardsToSave.get(questionTracker - 1).getAnswer());

                    // String to display the current question to the user
                    String questionNumberString = "Question " + (questionTracker);

                    // Display the current question number to the user
                    questionNumberTxtVw.setText(questionNumberString);

                    clearText = true;
                }
            }).start();

        } else {
            Toast.makeText(this, "No previous question to display.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void passTextView(TextView questionNumberTextView, TextView testNameTextView) {

        // Get the question and answer edit text
        final EditText questionEditText = findViewById(R.id.new_question_edt_txt);
        final EditText answerEditText = findViewById(R.id.new_answer_edt_txt);

        // String to display the current question number to the user
        String questionNumberString = "Question " + (flashcardsArrayList.size() + 1);

        // Display the current question number
        questionNumberTextView.setText(questionNumberString);

        // Store the current question number text view to update at other times with easy reference
        this.questionNumberTxtVw = questionNumberTextView;

        // Display the test name chosen
        testNameTextView.setText(testChosen.getFlashcardTestName());

        // If the user chose a question
        if(questionSelected != 1000){

            // Display the question number to the question chosen
            questionNumberTextView.setText(getResources().getString(R.string.question_number, flashcardsToSave.get(questionSelected).getQuestionNumber()));

            // Display the question and answer to the question chosen
            questionEditText.setText(flashcardsToSave.get(questionSelected).getQuestion());
            answerEditText.setText(flashcardsToSave.get(questionSelected).getAnswer());

            Button nextButton = findViewById(R.id.next_question_edit_button);
            Button previousButton = findViewById(R.id.previous_question_edit_button);
            Button saveEditButton = findViewById(R.id.view_question_list_button);

            nextButton.setVisibility(View.INVISIBLE);
            previousButton.setVisibility(View.INVISIBLE);
            saveEditButton.setText(R.string.save_question_edit);
            saveEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(answerEditText.getText().toString().trim().isEmpty()){

                        displayAnswerAlert();
                    }

                    if(questionEditText.getText().toString().trim().isEmpty()){

                        displayQuestionAlert();
                    }

                    if(!questionEditText.getText().toString().trim().isEmpty() && !answerEditText.getText().toString().trim().isEmpty()){

                        flashcardsArrayList.get(questionSelected).setAnswer(answerEditText.getText().toString().trim());
                        flashcardsArrayList.get(questionSelected).setQuestion(questionEditText.getText().toString().trim());

                        // Display the FlashcardTestQuestionsListFragment to the user to view their questions
                        getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

                        // Call the saveQuestions method to save the current questions to the database
                        saveQuestions();
                    }
                }
            });
        }
    }

    private void saveQuestions(){

        // Get the userID
        String userID = user.getUid();

        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        // QuestionNumber variable to represent each question being saved to the database
        int questionNumber = 0;

        // If there are questions already stored in the database
        if(flashcardsArrayList.size() > 0 && publicFlashcardsArrayList.size() > 0){

            // Remove all the previous questions entered and saved to the database before to prevent duplicates in the database collection
            databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").removeValue();

            databaseReference.child("publicTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").removeValue();

            // For each flashcard object in the flashcardsToSave array list
            for(Flashcards flashcard :flashcardsToSave){

                // Add one to the question number to represent each question without overwrites
                questionNumber += 1;

                // Set the question number string to save to the database
                String questionNumberString = "Question " + questionNumber;

                // If the test chosen is a public flashcard test
                if(testChosen.isFlashcardPublic()){

                    // Save the questions, answers, and question numbers to the users profile to the test selected on the database
                    databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard);

                    // Save the questions, answers, and question numbers to the public test selected on the database
                    databaseReference.child("publicTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard);

                // If the flashcard test isn't a public flashcard test
                } else {

                    // Save the questions, answers, and question numbers to the users profile to the test selected on the database
                    databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(FlashcardQuestionListActivity.this, "Question Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

        // If there weren't questions already saved on the database
        } else {

            // For each flashcard object in the flashcardsToSave array list
            for(Flashcards flashcard :flashcardsToSave){

                // Add one to the question number to represent each question without overwrites
                questionNumber += 1;

                // Set the question number string to save to the database
                String questionNumberString = "Question " + questionNumber;

                // If the test chosen is a public flashcard test
                if(testChosen.isFlashcardPublic()){

                    // Save the questions, answers, and question numbers to the users profile to the test selected on the database
                    databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard);

                    // Save the questions, answers, and question numbers to the public test selected on the database
                    databaseReference.child("publicTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard);

                // If the flashcard test isn't a public flashcard test
                } else {

                    // Save the questions, answers, and question numbers to the users profile to the test selected on the database
                    databaseReference.child("users").child(userID).child("userProfile").child(userID).child("flashcardTests").child("subjects").child(subjectChosenString).child(testChosen.getFlashcardTestId()).child("questions").child(questionNumberString).setValue(flashcard);

                }
            }
        }
    }

    private void displayQuestionAlert(){

        // Ask the user to enter a question to save
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardQuestionListActivity.this);
        alertDialogBuilder.setTitle("Please Enter A Question");
        alertDialogBuilder.setMessage("To Save your question edit, enter a question.");
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

    private void displayAnswerAlert(){

        // Ask the user to enter a Answer to save
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardQuestionListActivity.this);
        alertDialogBuilder.setTitle("Please Enter A Answer");
        alertDialogBuilder.setMessage("To Save your answer edit, enter a answer.");
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
