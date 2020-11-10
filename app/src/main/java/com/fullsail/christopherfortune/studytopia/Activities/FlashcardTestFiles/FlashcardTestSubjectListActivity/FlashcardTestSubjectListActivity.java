package com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity;

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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlaschardQuestionListActivity.FlashcardQuestionListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestCreationActivity.FlashcardTestCreationActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.Subjects.Subjects;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationEditFragment.FlashcardTestCreationEditFragment;
import com.fullsail.christopherfortune.studytopia.Adapters.FlashcardTestListAdapter.FlashcardTestListAdapter;
import com.fullsail.christopherfortune.studytopia.Adapters.FlashcardTestSubjectAdapter.FlashcardTestSubjectListAdapter;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestSubjectListFragment.FlashcardTestSubjectListFragment;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Fragments.SelectedFlashcardSubjectTestListFragment.SelectedFlashcardSubjectTestListFragment;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.TakeFlashcardTestActivity.TakeFlashcardTestActivity;
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

public class FlashcardTestSubjectListActivity extends AppCompatActivity implements FlashcardTestSubjectListFragment.FlashcardTestSubjectListInterface, SelectedFlashcardSubjectTestListFragment.selectedFlashcardSubjectTestListInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseUser user;
    private UserData userProfileData = new UserData();
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<Subjects> subjectsArrayList = new ArrayList<>();
    private ArrayList<FlashcardTest> flashcardTestsArrayList = new ArrayList<>();
    private ListView testListView;
    private String subjectChosenString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_test_subject_list);

        subjectsArrayList.add(new Subjects("Art", R.drawable.art));
        subjectsArrayList.add(new Subjects("Business Studies", R.drawable.archives));
        subjectsArrayList.add(new Subjects("Civics", R.drawable.civics));
        subjectsArrayList.add(new Subjects("Computer Science", R.drawable.laptop));
        subjectsArrayList.add(new Subjects("French", R.drawable.language));
        subjectsArrayList.add(new Subjects("Geography", R.drawable.geography));
        subjectsArrayList.add(new Subjects("Government", R.drawable.government));
        subjectsArrayList.add(new Subjects("History", R.drawable.history));
        subjectsArrayList.add(new Subjects("Mathematics", R.drawable.math));
        subjectsArrayList.add(new Subjects("Music", R.drawable.music));
        subjectsArrayList.add(new Subjects("Science", R.drawable.science));
        subjectsArrayList.add(new Subjects("Spanish", R.drawable.language));

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

            // Store the userData to the userProfileData
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
                                    Intent mapIntent = new Intent(FlashcardTestSubjectListActivity.this, StudyGroupMapActivity.class);
                                    startActivity(mapIntent);
                                }

                            } else {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardTestSubjectListActivity.this);
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
                            Intent studyGroupIntent = new Intent(FlashcardTestSubjectListActivity.this, StudyGroupActivity.class);
                            startActivity(studyGroupIntent);
                            break;
                        case R.id.nav_topic_of_the_day:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent topicOfDayIntent = new Intent(FlashcardTestSubjectListActivity.this, TopicOfTheDayActivity.class);
                            startActivity(topicOfDayIntent);
                            break;
                        case R.id.nav_forums:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent forumsSubjectListIntent = new Intent(FlashcardTestSubjectListActivity.this, ForumsSubjectListActivity.class);
                            startActivity(forumsSubjectListIntent);
                            break;
                        case R.id.nav_public_flashcards:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent publicFlashcardsIntent = new Intent(FlashcardTestSubjectListActivity.this, PublicFlashcardsCategoryListActivity.class);
                            startActivity(publicFlashcardsIntent);
                            break;
                        case R.id.nav_flashcards:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent flashcardsListIntent = new Intent(FlashcardTestSubjectListActivity.this, FlashcardTestSubjectListActivity.class);
                            startActivity(flashcardsListIntent);
                            break;
                        case R.id.nav_logout:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            Intent logoutIntent = new Intent(FlashcardTestSubjectListActivity.this, LoginSignupActivity.class);
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
                actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Flashcard Tests Subjects</font>", Html.FROM_HTML_MODE_LEGACY));
            }

            // Display the FlashcardTestSubjectListFragment to the user to allow them to select a subject
            getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_subject_list_frame, FlashcardTestSubjectListFragment.newInstance(), FlashcardTestSubjectListFragment.TAG).commit();

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
                            Intent profileIntent = new Intent(FlashcardTestSubjectListActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.add_test:

                // Intent to send the user to the FlashcardTestSubjectListActivity to create a flashcard test
                Intent flashcardTestCreationIntent = new Intent(FlashcardTestSubjectListActivity.this, FlashcardTestCreationActivity.class);

                // Put the subject chosen as an extra to the intent created above
                flashcardTestCreationIntent.putExtra("subjectChosen", subjectChosenString);

                if(userProfileData.isPremium() && userProfileData.getFlashcardTestCount() < 50){

                    // Start the FlashcardTestSubjectListActivity with the intent created above
                    startActivity(flashcardTestCreationIntent);

                } else if(!userProfileData.isPremium() && userProfileData.getFlashcardTestCount() < 25){

                    // Start the FlashcardTestSubjectListActivity with the intent created above
                    startActivity(flashcardTestCreationIntent);

                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("Can't Create Another Test");
                    alertDialogBuilder.setMessage("To create another flashcard test, you need to delete a previous flashcard test.");
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
            case R.id.add_question:

                // Display the FlashcardTestCreationEditFragment to allow the user to add their question
                getSupportFragmentManager().beginTransaction().replace(R.id.flashcard_test_subject_list_frame, FlashcardTestCreationEditFragment.newInstance(), FlashcardTestCreationEditFragment.TAG).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passListView(ListView subjectListView) {

        if(subjectListView != null){

            FlashcardTestSubjectListAdapter flashcardTestSubjectListAdapter = new FlashcardTestSubjectListAdapter(this, R.layout.subject_row, subjectsArrayList);
            subjectListView.setAdapter(flashcardTestSubjectListAdapter);
        }
    }

    @Override
    public void subjectChosen(int position) {

        // Get the subject chosen from the list view
        subjectChosenString = subjectsArrayList.get(position).getSubjectString().toLowerCase();

        // Display the SelectedFlashcardSubjectTestListFragment to the flashcard_test_subject_list_frame63
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.flashcard_test_subject_list_frame, SelectedFlashcardSubjectTestListFragment.newInstance(), SelectedFlashcardSubjectTestListFragment.TAG).commit();

        actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + subjectsArrayList.get(position).getSubjectString().toLowerCase() + " Tests</font>"));

        // If the current user has user data
        if(user != null){

            // Get the users id to obtain the flashcard test from the database
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mFlashcardTestReference = mFirebaseDatabase.getReference("/users/" + uId + "/userProfile/" + uId + "/flashcardTests/subjects/" + subjectChosenString);
            mFlashcardTestReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    flashcardTestsArrayList.clear();

                    // Loop through the data snapshot
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        // Get the flashcardTest data
                        FlashcardTest flashcardTest = data.getValue(FlashcardTest.class);

                        // Make sure flashcardTest  isn't null
                        if (flashcardTest != null) {

                            // If the test was created by the user
                            if(flashcardTest.getFlashcardTestCreatorId().equals(user.getUid())){

                                // Get the data to create a flashcardTest object
                                String flashcardTestName = flashcardTest.getFlashcardTestName();
                                int flashcardCount = flashcardTest.getFlashcardCount();
                                boolean testIsPublic = flashcardTest.isFlashcardPublic();
                                String flashcardCreationDate = flashcardTest.getFlashcardCreationDate();
                                String flashcardTestId = flashcardTest.getFlashcardTestId();
                                String flashcardTestCreatorId = flashcardTest.getFlashcardTestCreatorId();

                                // Add the flashcardTest object to the flashcardTestsArrayList
                                flashcardTestsArrayList.add(new FlashcardTest(flashcardTestName, flashcardCount, testIsPublic, flashcardCreationDate, flashcardTestId, flashcardTestCreatorId));
                            }
                        }
                    }

                    // Call the updateTestListMethod
                    updateTestList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            mFlashcardTestReference.keepSynced(true);
        }
    }

    @Override
    public void passList(ListView testListView) {

        // Pass the testListView to display the tests to the user
        this.testListView = testListView;
    }

    @Override
    public void takeTest(final int testChosen, View rowView) {

        final int testSelected = testChosen;
        PopupMenu popupMenu = new PopupMenu(this, rowView);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FlashcardTestSubjectListActivity.this);

                switch (item.getItemId()){
                    case R.id.take_test_selected:
                        // Ask the user if they want to take the test
                        alertDialogBuilder.setTitle("Take Test?");
                        alertDialogBuilder.setMessage("To take the test, tap take test. To view test list tap cancel.");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setPositiveButton(
                                "Take Test",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // Create a flashcardTest object from the test chosen in the list view
                                        FlashcardTest flashcardTestChosen = flashcardTestsArrayList.get(testSelected);

                                        // Intent to send the user to the TakeFlashcardTestActivity so they can take the test selected
                                        Intent viewTestQuestionsIntent = new Intent(FlashcardTestSubjectListActivity.this, TakeFlashcardTestActivity.class);

                                        // Pass the flashcard test chosen object and subject chosen string to the TakeFlashcardTestActivity
                                        viewTestQuestionsIntent.putExtra("flashcardTestChosen", flashcardTestChosen);
                                        viewTestQuestionsIntent.putExtra("subjectChosen", subjectChosenString);

                                        // Start the TakeFlashcardTestActivity with the intent created above
                                        startActivity(viewTestQuestionsIntent);
                                    }
                                });

                        alertDialogBuilder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                    case R.id.edit_flashcard_test:
                        // Ask the user if they are sure they want to edit the test
                        alertDialogBuilder.setTitle("Edit Test?");
                        alertDialogBuilder.setMessage("To edit the test, tap edit test. To view test list tap cancel.");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setPositiveButton(
                                "Edit Test",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // Get the flashcardTestChosen to pass to the QuestionListActivity
                                        FlashcardTest flashcardTestChosen = flashcardTestsArrayList.get(testSelected);

                                        // If the flashcard test chosen is public
                                        if(flashcardTestChosen.isFlashcardPublic()){

                                            // Intent to send the user to the FlashcardQuestionListActivity to allow them to view the questions of the selected test
                                            Intent viewTestQuestionsIntent = new Intent(FlashcardTestSubjectListActivity.this, FlashcardQuestionListActivity.class);

                                            // Pass the flashcard test chosen object and subject chosen string to the FlashcardQuestionListActivity
                                            viewTestQuestionsIntent.putExtra("publicFlashcardTestChosen", flashcardTestChosen);
                                            viewTestQuestionsIntent.putExtra("subjectChosen", subjectChosenString);

                                            // Start the FlashcardQuestionListActivity
                                            startActivity(viewTestQuestionsIntent);
                                        } else {

                                            // Intent to send the user to the FlashcardQuestionListActivity to allow them to view the questions of the selected test
                                            Intent viewTestQuestionsIntent = new Intent(FlashcardTestSubjectListActivity.this, FlashcardQuestionListActivity.class);

                                            // Pass the flashcard test chosen object and subject chosen string to the FlashcardQuestionListActivity
                                            viewTestQuestionsIntent.putExtra("flashcardTestChosen", flashcardTestChosen);
                                            viewTestQuestionsIntent.putExtra("subjectChosen", subjectChosenString);

                                            // Start the FlashcardQuestionListActivity
                                            startActivity(viewTestQuestionsIntent);
                                        }

                                    }
                                });

                        alertDialogBuilder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog editAlertDialog = alertDialogBuilder.create();
                        editAlertDialog.show();
                        break;
                    case R.id.delete_flashcard_test:

                        alertDialogBuilder.setTitle("Delete Test?");
                        alertDialogBuilder.setMessage("To delete the test, tap delete. To view test list tap cancel.");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setPositiveButton(
                                "Delete Test",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // Get the flashcardTestChosen to pass to the QuestionListActivity
                                        FlashcardTest flashcardTestChosen = flashcardTestsArrayList.get(testSelected);

                                        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();

                                        int flashcardTestCount = userProfileData.getFlashcardTestCount() - 1;

                                        int flashcardCount = userProfileData.getFlashCardCount() - flashcardTestChosen.getFlashcardCount();

                                        // If the flashcard test chosen is public
                                        if(flashcardTestChosen.isFlashcardPublic()){

                                            mDatabaseReference.child("publicTests").child("subjects").child(subjectChosenString).child(flashcardTestChosen.getFlashcardTestId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Toast.makeText(FlashcardTestSubjectListActivity.this, "Public Flashcard Test Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("subjects").child(subjectChosenString).child(flashcardTestChosen.getFlashcardTestId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FlashcardTestSubjectListActivity.this, "Private Flashcard Test Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child("flashcardTestCount").setValue(flashcardTestCount);
                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child("flashCardCount").setValue(flashcardCount);

                                        } else {

                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child(user.getUid()).child("flashcardTests").child("subjects").child(subjectChosenString).child(flashcardTestChosen.getFlashcardTestId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(FlashcardTestSubjectListActivity.this, "Private Flashcard Test Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child("flashcardTestCount").setValue(flashcardTestCount);
                                            mDatabaseReference.child("users").child(user.getUid()).child("userProfile").child("flashCardCount").setValue(flashcardCount);
                                        }

                                    }
                                });

                        alertDialogBuilder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog deleteAlertDialog = alertDialogBuilder.create();
                        deleteAlertDialog.show();

                        break;
                }
                return true;
            }
        });

        popupMenu.inflate(R.menu.flashcard_test_selected_menu);
        popupMenu.show();
    }

    private void updateTestList(){

        // Create a flashcardTestListAdapter to display to the flashcard tests to the user
        FlashcardTestListAdapter flashcardTestListAdapter = new FlashcardTestListAdapter(this, R.layout.test_row, flashcardTestsArrayList);

        // Set the adapter created above to the test list view
        testListView.setAdapter(flashcardTestListAdapter);
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
