package com.fullsail.christopherfortune.studytopia.Activities.LeaderboardFiles.StudyGroupLeaderboardActivity;

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
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.LeaderboardAdapter.LeaderboardAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Leaderboard.Leaderboard;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.FlashCardTestLeaderboardFragment.FlashcardTestLeaderboardFragment;
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

public class StudyGroupLeaderboardActivity extends AppCompatActivity implements FlashcardTestLeaderboardFragment.FlashcardTestLeaderboardInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private ListView leaderboardListView;
    private ArrayList<Leaderboard> leaderboardArrayList = new ArrayList<>();
    private String testId = "";
    private String studyGroupId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_leaderboard);

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("testId") && startingIntent.hasExtra("studyGroupId")){

            testId = startingIntent.getStringExtra("testId");
            studyGroupId = startingIntent.getStringExtra("studyGroupId");
        }

        // Get an instance of the firebase authentication and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

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

            if(!studyGroupId.isEmpty() && !testId.isEmpty()){

                DatabaseReference leaderboardDatabaseReference = mFirebaseDatabase.getReference();

                leaderboardDatabaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupId).child("studyGroupTests").child(testId).child("leaderboard").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Loop through the data snapshot
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            // Get the leaderboard data
                            Leaderboard leaderboard = data.getValue(Leaderboard.class);

                            // Make sure leaderboard isn't null
                            if (leaderboard != null) {

                                String usersUserName = leaderboard.getUsersUsername();
                                String usersProfileImageUrl = leaderboard.getUsersProfileImageUrl();
                                long timeInMinutes = leaderboard.getTimeInMinutes();
                                long timeInSeconds = leaderboard.getTimeInSeconds();
                                String dateTaken = leaderboard.getDateTaken();
                                String timeTaken = leaderboard.getTimeTaken();

                                leaderboardArrayList.add(new Leaderboard(usersUserName, usersProfileImageUrl, timeInMinutes, timeInSeconds, dateTaken, timeTaken));
                            }
                        }

                        displayStudyGroupLeaderboard();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                leaderboardDatabaseReference.keepSynced(true);
            }
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
                                Intent mapIntent = new Intent(StudyGroupLeaderboardActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupLeaderboardActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupLeaderboardActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupLeaderboardActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupLeaderboardActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupLeaderboardActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupLeaderboardActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupLeaderboardActivity.this, LoginSignupActivity.class);
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
        if (actionbar != null) {

            // Set the icon, title, and enable the actionBar to allow the user to view the navigation view
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Leaderboard</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the FlashcardTestFragment to the user so they view the flashcard test leaderboard
        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_leaderboard_frame, FlashcardTestLeaderboardFragment.newInstance(), FlashcardTestLeaderboardFragment.TAG).commit();
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
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.done_menu_button:

                Intent studyGroupIntent = new Intent(this, StudyGroupActivity.class);
                startActivity(studyGroupIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passLeaderboardListView(ListView leaderboardListView) {
        this.leaderboardListView = leaderboardListView;
    }

    private void displayStudyGroupLeaderboard(){

        LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(this, R.layout.learderboard_row, leaderboardArrayList);
        leaderboardListView.setAdapter(leaderboardAdapter);
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
