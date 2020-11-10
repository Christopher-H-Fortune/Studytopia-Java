package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StdyGrpFlashcardTestQuestionsActivity;

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

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.QuestionListAdapter.QuestionListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupFlashcardTest.StudyGroupFlashcardTest;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationEditFragment.FlashcardTestCreationEditFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.QuestionsListFragment.FlashcardTestQuestionsListFragment;
import com.fullsail.christopherfortune.studytopia.R;
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

public class StdyGrpFlashcardTestQuestionsActivity extends AppCompatActivity implements FlashcardTestQuestionsListFragment.FlashcardTestQuestionsListInterface, FlashcardTestCreationEditFragment.FlashcardTestCreationEditInterface {

    private DrawerLayout drawerLayout;
    private UserData userProfileData = new UserData();
    private FirebaseDatabase mFirebaseDatabase;
    private String uniqueTestId;
    private String studyGroupId;
    private StudyGroupFlashcardTest studyGroupFlashcardTest;
    private ListView questionListView;
    private ArrayList<Flashcards> flashcardsArrayList = new ArrayList<>();
    private ArrayList<Flashcards> flashcardsToSave = new ArrayList<>();
    private TextView questionNumberTxtVw;
    private int questionSelected = 1000;
    private int questionTracker = 1000;
    private boolean clearText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stdy_grp_flashcard_test_questions);

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("testId")){

            uniqueTestId = startingIntent.getStringExtra("testId");
            studyGroupId = startingIntent.getStringExtra("studyGroupId");
            studyGroupFlashcardTest = (StudyGroupFlashcardTest)startingIntent.getSerializableExtra("studyGroupTest");
        }

        // Get an instance of the firebase authentication and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        FirebaseUser user = mAuth.getCurrentUser();

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
            DatabaseReference studyGroupQuestionsRef = mFirebaseDatabase.getReference();

            studyGroupQuestionsRef.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(uniqueTestId).child("testQuestions").addValueEventListener(new ValueEventListener() {
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

                    displayQuestionsList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            studyGroupQuestionsRef.keepSynced(true);
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
                                Intent mapIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StdyGrpFlashcardTestQuestionsActivity.this);
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
                        Intent studyGroupIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Test Questions</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the questions list to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();
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
                    userProfileImageSmrtImage.setImageUrl(userProfileData.getImageUrl());userProfileImageSmrtImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profileIntent = new Intent(StdyGrpFlashcardTestQuestionsActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });

                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.add_question:

                // If the user can add another question to the test without going over the card count on the test
                if (flashcardsArrayList.size() < studyGroupFlashcardTest.getFlashcardCount()) {

                    // Display the FlashcardTestCreationEditFragment to the user so they can add another question
                    getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestCreationEditFragment.newInstance(), FlashcardTestCreationEditFragment.TAG).commit();

                    questionTracker = flashcardsArrayList.size() + 1;
                }
                return true;

            case R.id.delete_question:

                EditText questionEditText = findViewById(R.id.new_question_edt_txt);
                EditText answerEditText = findViewById(R.id.new_answer_edt_txt);

                questionEditText.setText(null);
                answerEditText.setText(null);

                flashcardsToSave.remove(questionSelected);

                // Display the FlashcardTestQuestionsListFragment to the user to view their questions
                getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

                saveQuestions();

                displayQuestionsList();

                questionTracker = 1000;
                questionSelected = 1000;

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passQuestionListView(ListView questionListView) {

        // Store the listView to display the test questions to the user
        this.questionListView = questionListView;
    }

    @Override
    public void questionSelection(int questionSelected) {

        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestCreationEditFragment.newInstance(), FlashcardTestCreationEditFragment.TAG).commit();

        // Set the question selected to the question chosen from the list view
        this.questionSelected = questionSelected;
    }

    @Override
    public void viewQuestionsList() {

        // Get the question and answer edit text
        EditText questionEditText = findViewById(R.id.new_question_edt_txt);
        EditText answerEditText = findViewById(R.id.new_answer_edt_txt);

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
            getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

            // Call the saveQuestions method to allow the user to save the questions to the database
            saveQuestions();

            questionTracker = 1000;
            questionSelected = 1000;

        // If the user didn't enter a question and answer
        } else {

            // Display the FlashcardTestQuestionsListFragment to the user to view their questions
            getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();

            // Call the saveQuestions method to save the current questions to the database
            saveQuestions();

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

        // If the user entered data and doesn't exceed the count of the max flashcards for the test
        if(!question.isEmpty() && !answer.isEmpty() && questionTracker + 1 <= studyGroupFlashcardTest.getFlashcardCount()){

            questionTracker += 1;

            // Create a flashcard object from the data the user entered
            Flashcards flashcard = new Flashcards(question, answer, flashcardsToSave.size() + 1);

            // Save the flashcard entered to the arrayList to display to the user
            flashcardsToSave.add(flashcard);

            questionEditView.animate().withLayer().rotationY(360).setDuration(550).withEndAction(new Runnable() {
                @Override
                public void run() {

                    // Display the question number to the user
                    String questionNumberString = "Question " + (flashcardsArrayList.size() + 1);
                    questionNumberTxtVw.setText(questionNumberString);

                    // Clear the question and answer edit text
                    questionEditText.setText(null);
                    answerEditText.setText(null);
                }
            }).start();
            
        } else if (questionTracker + 1 <= flashcardsToSave.size() && clearText){

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
        } else if (questionTracker + 1 <= studyGroupFlashcardTest.getFlashcardCount() && clearText){

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
        if(questionTracker - 1 >= 0) {

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
        testNameTextView.setText(studyGroupFlashcardTest.getFlashcardTestName());

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

                        // Ask the user to enter a Answer to save
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StdyGrpFlashcardTestQuestionsActivity.this);
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

                    if(questionEditText.getText().toString().trim().isEmpty()){

                        // Ask the user to enter a question to save
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StdyGrpFlashcardTestQuestionsActivity.this);
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

                    if(!questionEditText.getText().toString().trim().isEmpty() && !answerEditText.getText().toString().trim().isEmpty()){

                        flashcardsToSave.get(questionSelected).setAnswer(answerEditText.getText().toString().trim());
                        flashcardsToSave.get(questionSelected).setQuestion(questionEditText.getText().toString().trim());

                        // Call the saveQuestions method to save the current questions to the database
                        saveQuestions();

                        // Display the FlashcardTestQuestionsListFragment to the user to view their questions
                        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_test_question_list_frame, FlashcardTestQuestionsListFragment.newInstance(), FlashcardTestQuestionsListFragment.TAG).commit();
                    }
                }
            });
        }

    }

    private void displayQuestionsList(){

        // pass the data obtained from the database to the flashcardsToSave array
        flashcardsToSave = flashcardsArrayList;

        // Question list adapter to display the questions from the test selected
        QuestionListAdapter questionListAdapter = new QuestionListAdapter(this, R.layout.question_row, flashcardsToSave);

        // Set the adapter created above to the question list view
        questionListView.setAdapter(questionListAdapter);

        // NotifyDataSetChanged to the adapter created above
        questionListAdapter.notifyDataSetChanged();
    }

    private void saveQuestions(){

        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        // QuestionNumber variable to represent each question being saved to the database
        int questionNumber = 0;

        // If there are questions already stored in the database
        if(!flashcardsArrayList.isEmpty() && !flashcardsToSave.isEmpty()){

            // Remove all the previous questions entered and saved to the database before to prevent duplicates in the database collection
            databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(uniqueTestId).child("testQuestions").removeValue();

            // For each flashcard object in the flashcardsToSave array list
            for(Flashcards flashcard :flashcardsToSave){

                // Add one to the question number to represent each question without overwrites
                questionNumber += 1;

                // Set the question number string to save to the database
                String questionNumberString = "Question " + questionNumber;

                // Save the questions, answers, and question numbers to the study group test the user is editing
                databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(uniqueTestId).child("testQuestions").child(questionNumberString).setValue(flashcard);
            }

            // If there weren't questions already saved on the database
        } else {

            // For each flashcard object in the flashcardsToSave array list
            for(Flashcards flashcard :flashcardsToSave){

                // Add one to the question number to represent each question without overwrites
                questionNumber += 1;

                // Set the question number string to save to the database
                String questionNumberString = "Question " + questionNumber;

                // Save the questions, answers, and question numbers to the study group test the user is editing
                databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(uniqueTestId).child("testQuestions").child(questionNumberString).setValue(flashcard);
            }
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
