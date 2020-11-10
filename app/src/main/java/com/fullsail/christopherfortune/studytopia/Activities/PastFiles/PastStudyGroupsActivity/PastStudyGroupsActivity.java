package com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastStudyGroupsActivity;

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
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastStudyGroupDetailsActivity.PastStudyGroupDetailsActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.PastStudyGroupsAdapter.PastStudyGroupsAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.PastStudyGroupDetailsFragment.PastStudyGroupDetailsFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.PastStudyGroupsFragment.PastStudyGroupsFragment;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
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

public class PastStudyGroupsActivity extends AppCompatActivity implements PastStudyGroupsFragment.PastStudyGroupsInterface, PastStudyGroupDetailsFragment.PastStudyGroupDetailsInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private ListView pastStudyGroupsListView;
    private ArrayList<StudyGroup> pastStudyGroupsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_study_groups);

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
            DatabaseReference pastStudyGroupsDatabaseRef = mFirebaseDatabase.getReference();

            // Set the database to have a ValueEventListener to display the games data to the user
            pastStudyGroupsDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("pastStudyGroups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    pastStudyGroupsArrayList.clear();

                    // Loop through the data snapshot
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        // Get the StudyGroup data
                        StudyGroup studyGroup = data.getValue(StudyGroup.class);

                        // Make sure studyGroup isn't null
                        if (studyGroup != null) {

                            // Get the study group data to create a StudyGroup object
                            // get the data to create a study group object
                            String studyGroupCreatorImgUrl = studyGroup.getStudyGroupCreatorImgUrl();
                            String studyGroupName = studyGroup.getStudyGroupName();
                            String studyGroupSubject = studyGroup.getStudyGroupSubject();
                            String studyGroupCompleteAddressAddress = studyGroup.getStudyGroupCompleteAddress();
                            String studyGroupAddress = studyGroup.getStudyGroupAddress();
                            String studyGroupCity = studyGroup.getStudyGroupCity();
                            String studyGroupState = studyGroup.getStudyGroupState();
                            double studyGroupLat = studyGroup.getStudyGroupLat();
                            double studyGroupLong = studyGroup.getStudyGroupLong();
                            String studyGroupDate = studyGroup.getStudyGroupDate();
                            String studyGroupTime = studyGroup.getStudyGroupTime();
                            int attendeeCount = studyGroup.getStudyGroupAttendeeCount();
                            boolean isOnMap = studyGroup.isOnMap();
                            String creatorUsername = studyGroup.getCreatorUsername();
                            String studyGroupId = studyGroup.getStudyGroupId();
                            int studyGroupHour = studyGroup.getStudyGroupHour();
                            int studyGroupMinute = studyGroup.getStudyGroupMinute();
                            int studyGroupDay = studyGroup.getStudyGroupDay();
                            int studyGroupMonth = studyGroup.getStudyGroupMonth();
                            int studyGroupYear = studyGroup.getStudyGroupYear();

                            // Store the userData to the userProfileData
                            pastStudyGroupsArrayList.add(new StudyGroup(studyGroupCreatorImgUrl,
                                    studyGroupName,
                                    studyGroupSubject,
                                    studyGroupCompleteAddressAddress,
                                    studyGroupAddress,
                                    studyGroupCity,
                                    studyGroupState,
                                    studyGroupLat,
                                    studyGroupLong,
                                    studyGroupDate,
                                    studyGroupTime,
                                    attendeeCount,
                                    isOnMap,
                                    creatorUsername,
                                    studyGroupId,
                                    studyGroupHour,
                                    studyGroupMinute,
                                    studyGroupDay,
                                    studyGroupMonth,
                                    studyGroupYear));
                        }
                    }

                    displayPastStudyGroups();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            pastStudyGroupsDatabaseRef.keepSynced(true);
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
                                Intent mapIntent = new Intent(PastStudyGroupsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PastStudyGroupsActivity.this);
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
                        Intent studyGroupIntent = new Intent(PastStudyGroupsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(PastStudyGroupsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(PastStudyGroupsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(PastStudyGroupsActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(PastStudyGroupsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(PastStudyGroupsActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Past Study Groups</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.past_study_groups_frame, PastStudyGroupsFragment.newInstance(), PastStudyGroupsFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(PastStudyGroupsActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.done_menu_button:
                getSupportFragmentManager().beginTransaction().replace(R.id.past_study_groups_frame, PastStudyGroupsFragment.newInstance(), PastStudyGroupsFragment.TAG).commit();
                break;
        }
        return true;
    }



    @Override
    public void viewStudyGroupFlashcards() {

    }

    @Override
    public void doneViewing() {
        getSupportFragmentManager().beginTransaction().replace(R.id.past_study_groups_frame, PastStudyGroupsFragment.newInstance(), PastStudyGroupsFragment.TAG).commit();
    }

    @Override
    public void passPastStudyGroupsListView(ListView pastStudyGroupsListView) {

        // Store the list view to display the past study groups to the user
        this.pastStudyGroupsListView = pastStudyGroupsListView;
    }

    @Override
    public void viewStudyGroupChosen(int studyGroupChosen) {

        // Intent to send the user to the past study group details activity
        Intent pastStudyGroupDetailsIntent = new Intent(this, PastStudyGroupDetailsActivity.class);

        StudyGroup studyGroup = pastStudyGroupsArrayList.get(studyGroupChosen);

        pastStudyGroupDetailsIntent.putExtra("studyGroupChosen", studyGroup);

        // Start the study group chosen activity with the intent created above
        startActivity(pastStudyGroupDetailsIntent);
    }

    private void displayPastStudyGroups(){

        // Create PastStudyGroupsAdapter to display the past study groups to the user in the list view
        PastStudyGroupsAdapter pastStudyGroupsAdapter = new PastStudyGroupsAdapter(this, R.layout.past_study_groups_row, pastStudyGroupsArrayList);


        pastStudyGroupsListView.setAdapter(pastStudyGroupsAdapter);
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
