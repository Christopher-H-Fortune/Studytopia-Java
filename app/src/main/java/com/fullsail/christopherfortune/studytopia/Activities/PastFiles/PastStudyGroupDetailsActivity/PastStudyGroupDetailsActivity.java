package com.fullsail.christopherfortune.studytopia.Activities.PastFiles.PastStudyGroupDetailsActivity;

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
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.AttendeeListAdapter.AttendeeListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Attendees.Attendees;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupAttendeeListFragment.StudyGroupAttendeesListFragment;
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

public class PastStudyGroupDetailsActivity extends AppCompatActivity implements StudyGroupAttendeesListFragment.StudyGroupAttendeeListInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private StudyGroup studyGroupChosen;
    private ArrayList<Attendees> attendeesArrayList = new ArrayList<>();
    private ListView attendeeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_study_group_details);

        // Get the starting intent of the activity
        Intent startingIntent = getIntent();

        // If the starting intent has an extra named studyGroupChosen
        if(startingIntent.hasExtra("studyGroupChosen")){

            // Get the study group
            studyGroupChosen = (StudyGroup)startingIntent.getSerializableExtra("studyGroupChosen");
        }

        // Set the instance of the Firebase auth and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        final FirebaseUser user = mAuth.getCurrentUser();

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
        DatabaseReference studyGroupDatabaseRef = mFirebaseDatabase.getReference();

        // Set the database to have a ValueEventListener to display the study groups data to the user
        studyGroupDatabaseRef.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupChosen.getStudyGroupId()).child("attendees").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the attendees data
                    Attendees attendees = data.getValue(Attendees.class);

                    // Make sure attendees isn't null
                    if (attendees != null) {

                        // Get the user profile data to create attendees UserData object
                        String username = attendees.getUsername();
                        String userProfilePicUrl = attendees.getUserProfileImage();

                        // Store the attendees to the userProfileData
                        attendeesArrayList.add(new Attendees(username, userProfilePicUrl));
                    }

                }

                // Call the displayAttendees method to display the attendees of the study group to the user
                displayAttendees();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        studyGroupDatabaseRef.keepSynced(true);

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
                                Intent mapIntent = new Intent(PastStudyGroupDetailsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PastStudyGroupDetailsActivity.this);
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
                        Intent studyGroupIntent = new Intent(PastStudyGroupDetailsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(PastStudyGroupDetailsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(PastStudyGroupDetailsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(PastStudyGroupDetailsActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(PastStudyGroupDetailsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(PastStudyGroupDetailsActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + studyGroupChosen.getStudyGroupName() + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        SmartImageView creatorSmrtImgVw = findViewById(R.id.past_study_group_creator_smrt_img);
        TextView creatorUsernameTxtVw = findViewById(R.id.past_study_group_joined_creator_txt_vw);
        TextView studyGroupAddress = findViewById(R.id.past_study_group_joined_address_txt_vw);

        creatorSmrtImgVw.setImageUrl(studyGroupChosen.getStudyGroupCreatorImgUrl());
        creatorUsernameTxtVw.setText("Created By:\n" + studyGroupChosen.getCreatorUsername());
        studyGroupAddress.setText(getResources().getString(R.string.study_group_address, studyGroupChosen.getStudyGroupAddress(), studyGroupChosen.getStudyGroupCity(), studyGroupChosen.getStudyGroupState()));

        getSupportFragmentManager().beginTransaction().replace(R.id.past_study_group_attendee_list_frame, StudyGroupAttendeesListFragment.newInstance(), StudyGroupAttendeesListFragment.TAG).commit();

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
                        Intent profileIntent = new Intent(PastStudyGroupDetailsActivity.this, UserProfileActivity.class);
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
    public void passListView(ListView attendeeListView) {
        this.attendeeListView = attendeeListView;
    }

    private void displayAttendees(){

        AttendeeListAdapter attendeeListAdapter = new AttendeeListAdapter(this, R.layout.attendee_list_row, attendeesArrayList);
        attendeeListView.setAdapter(attendeeListAdapter);
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
