package com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity;

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
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumSubjectChannelsActivity.ForumSubjectChannelActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.FlashcardTestSubjectAdapter.FlashcardTestSubjectListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Subjects.Subjects;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.ForumsSubjectListFragment.ForumsSubjectListFragment;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
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

public class ForumsSubjectListActivity extends AppCompatActivity implements ForumsSubjectListFragment.ForumsSubjectListInterface{

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private ArrayList<Subjects> subjectsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_subject_list);

        subjectsArrayList.add(new Subjects("Topic of the Day", R.drawable.totd));
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
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        FirebaseUser user = mAuth.getCurrentUser();

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

        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                                Intent mapIntent = new Intent(ForumsSubjectListActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForumsSubjectListActivity.this);
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
                    case R.id.nav_study_group:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent studyGroupIntent = new Intent(ForumsSubjectListActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(ForumsSubjectListActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(ForumsSubjectListActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(ForumsSubjectListActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(ForumsSubjectListActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(ForumsSubjectListActivity.this, LoginSignupActivity.class);
                        startActivity(logoutIntent);
                }
                return true;
            }
        });
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Forums Subjects</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.forums_list_frame, ForumsSubjectListFragment.newInstance(), ForumsSubjectListFragment.TAG).commit();
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
                        Intent profileIntent = new Intent(ForumsSubjectListActivity.this, UserProfileActivity.class);
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
    public void viewForumRoom(int subjectChosen) {

        String subjectChosenString = subjectsArrayList.get(subjectChosen).getSubjectString().toLowerCase();

        if(subjectChosenString.equals("topic of the day")){

            Intent topicOfTheDayForumIntent = new Intent(this, TopicOfTheDayActivity.class);
            startActivity(topicOfTheDayForumIntent);

        } else {

            Intent forumSubjectChannelIntent = new Intent(this, ForumSubjectChannelActivity.class);
            forumSubjectChannelIntent.putExtra("subjectChosen", subjectChosenString);
            startActivity(forumSubjectChannelIntent);
        }
    }

    @Override
    public void passSubjectView(ListView subjectListView) {

        if(subjectListView != null){

            FlashcardTestSubjectListAdapter flashcardTestSubjectListAdapter = new FlashcardTestSubjectListAdapter(this, R.layout.subject_row, subjectsArrayList);
            subjectListView.setAdapter(flashcardTestSubjectListAdapter);
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
