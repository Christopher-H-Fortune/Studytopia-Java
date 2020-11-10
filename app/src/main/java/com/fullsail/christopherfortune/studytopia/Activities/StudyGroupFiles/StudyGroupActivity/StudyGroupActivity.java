package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity;

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
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StdyGrpFlashcardTestQuestionsActivity.StdyGrpFlashcardTestQuestionsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupFlascardTestCreationActivity.StudyGroupFlashcardTestCreationActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupFlashcardTestActivity.StudyGroupFlashcardTestActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.AttendeeListAdapter.AttendeeListAdapter;
import com.fullsail.christopherfortune.studytopia.Adapters.StudyGroupTestListAdapter.StudyGroupTestListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Attendees.Attendees;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupFlashcardTest.StudyGroupFlashcardTest;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupAttendeeListFragment.StudyGroupAttendeesListFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupTestListFragment.StudyGroupTestListFragment;
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

public class StudyGroupActivity extends AppCompatActivity implements StudyGroupTestListFragment.StudyGroupInterface, StudyGroupAttendeesListFragment.StudyGroupAttendeeListInterface {

    private DrawerLayout drawerLayout;
    private FirebaseDatabase mFirebaseDatabase;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private String studyGroupId;
    private StudyGroup studyGroupChosen;
    private ListView testListView;
    private ListView attendeeListView;
    private ArrayList<Attendees> attendeesArrayList = new ArrayList<>();
    private ArrayList<StudyGroupFlashcardTest> studyGroupFlashcardTests = new ArrayList<>();
    private FirebaseUser user;
    private int studyGroupCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group);

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
                            studyGroupCount = userData.getStudyGroupCount();
                            boolean isPremium = userData.isPremium();

                            // Store the userData to the userProfileData
                            userProfileData = new UserData(firsName, lastName, userName, email, imageUrl, imageName, flashcardTestCount, flashcardCount, studyGroupCount, isPremium);

                            studyGroupCount = userData.getStudyGroupCount();
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
            DatabaseReference studyGroupDatabaseRef = mFirebaseDatabase.getReference();

            // Set the database to have a ValueEventListener to display the study groups data to the user
            studyGroupDatabaseRef.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("currentStudyGroup").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // If the data snapshot has data
                    if(dataSnapshot.getValue() != null){

                        // Get the study group joined id from the the users profile
                        studyGroupId = dataSnapshot.getValue().toString();

                        // Call the getStudyGroup method passing the id obtained from the database
                        getStudyGroup(studyGroupId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
            studyGroupDatabaseRef.keepSynced(true);
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
                                Intent mapIntent = new Intent(StudyGroupActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Study Group</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the StudyGroupTestListFragment to the user so they can view the study groups tests
        getFragmentManager().beginTransaction().replace(R.id.study_group_test_list_frame, StudyGroupTestListFragment.newInstance(), StudyGroupTestListFragment.TAG).commit();

        // Display the StudyGroupAttendeesListFragment to the user so they can view the study groups attendees
        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_attendee_list_frame, StudyGroupAttendeesListFragment.newInstance(), StudyGroupAttendeesListFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(StudyGroupActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.leave_group:

                // Ask the user if they are sure they want to leave the study group
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Leave Study Group");
                alertDialogBuilder.setMessage("To leave the study group tap leave. To stay in the study group, tap cancel.");
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setPositiveButton(
                        "Leave",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if(user != null){

                                    // Database reference to remove the current study group from the database
                                    final DatabaseReference databaseReference = mFirebaseDatabase.getReference();

                                    databaseReference.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("currentStudyGroup").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(StudyGroupActivity.this, "Study Group Left", Toast.LENGTH_SHORT).show();

                                            studyGroupCount += 1;

                                            databaseReference.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("pastStudyGroups").child(studyGroupChosen.getStudyGroupId()).setValue(studyGroupChosen);
                                            databaseReference.child("users").child(user.getUid()).child("userProfile").child("studyGroupCount").setValue(studyGroupCount);
                                            databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupChosen.getStudyGroupId()).child("attendees").child(user.getUid()).removeValue();
                                        }
                                    });

                                    // Send the user to the study group map screen
                                    Intent studyGroupMapIntent = new Intent(StudyGroupActivity.this, StudyGroupMapActivity.class);
                                    startActivity(studyGroupMapIntent);
                                }
                            }
                        });

                alertDialogBuilder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // close the dialog
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void leaveStudyGroup() {

    }

    @Override
    public void createFlashcards() {

        // Intent to send the user to create a flashcard test for the study group
        Intent createStudyGroupTestIntent = new Intent(this, StudyGroupFlashcardTestCreationActivity.class);

        // Attack the current studyGroupId to the intent created above
        createStudyGroupTestIntent.putExtra("studyGroupId", studyGroupId);

        // Start the StudyGroupFlashcardTestCreationActivity to
        startActivity(createStudyGroupTestIntent);
    }

    @Override
    public void takeFlashcardTest(int testChosen) {

        StudyGroupFlashcardTest studyGroupFlashcardTest = studyGroupFlashcardTests.get(testChosen);

        // Send the user to create the questions
        Intent flashcardTestIntent = new Intent(this, StudyGroupFlashcardTestActivity.class);

        // Attach the testId of the test created above to the intent to the StdyGrpFlashcardTestQuestionsActivity
        flashcardTestIntent.putExtra("testId", studyGroupFlashcardTest.getFlashcardTestId());
        flashcardTestIntent.putExtra("studyGroupId", studyGroupId);
        flashcardTestIntent.putExtra("studyGroupTest", studyGroupFlashcardTest);

        // Start the StdyGrpFlashcardTestQuestionsActivity with the intent created above
        startActivity(flashcardTestIntent);
    }

    @Override
    public void editFlashcardTest(int testChosen) {

        StudyGroupFlashcardTest studyGroupFlashcardTest = studyGroupFlashcardTests.get(testChosen);

        // Send the user to create the questions
        Intent flashcardTestQuestionIntent = new Intent(this, StdyGrpFlashcardTestQuestionsActivity.class);

        // Attach the testId of the test created above to the intent to the StdyGrpFlashcardTestQuestionsActivity
        flashcardTestQuestionIntent.putExtra("testId", studyGroupFlashcardTest.getFlashcardTestId());
        flashcardTestQuestionIntent.putExtra("studyGroupId", studyGroupId);
        flashcardTestQuestionIntent.putExtra("studyGroupTest", studyGroupFlashcardTest);

        // Start the StdyGrpFlashcardTestQuestionsActivity with the intent created above
        startActivity(flashcardTestQuestionIntent);
    }

    @Override
    public void passTestListView(ListView testListView) {

        // Pass the test view to the
        this.testListView = testListView;
    }

    private void getStudyGroup(final String studyGroupId){

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/studyGroups/studyGroupsToDisplay");

        // Set the database to have a ValueEventListener to display the games data to the user
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the userData data
                    StudyGroup studyGroup = data.getValue(StudyGroup.class);

                    // Make sure studyGroupChosen isn't null
                    if (studyGroup != null) {

                        // If the study group id is equal to the study group the user joined
                        if(studyGroup.getStudyGroupId().equals(studyGroupId)){

                            // Get the data from the database to create a study group object
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

                            // Create a study group object with the data obtained above
                            studyGroupChosen = new StudyGroup(studyGroupCreatorImgUrl, studyGroupName, studyGroupSubject, studyGroupCompleteAddressAddress, studyGroupAddress,
                                    studyGroupCity, studyGroupState,studyGroupLat, studyGroupLong, studyGroupDate, studyGroupTime, attendeeCount,isOnMap, creatorUsername, studyGroupId, studyGroupHour, studyGroupMinute, studyGroupDay, studyGroupMonth, studyGroupYear);
                        }
                    }
                }

                // Call the updateDataShown method to display the study group data to the user
                updateDataShown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabaseReference.keepSynced(true);
    }

    @Override
    public void passListView(ListView attendeeListView) {

        // store the attendee list view to update the data within the list view
        this.attendeeListView = attendeeListView;
    }

    private void updateDataShown(){

        if(studyGroupChosen != null){

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference attendeeDatabaseReference = mFirebaseDatabase.getReference("/studyGroups/studyGroupsToDisplay/" + studyGroupChosen.getStudyGroupId() + "/attendees");

            // Set the database to have a ValueEventListener to display the games data to the user
            attendeeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Loop through the data snapshot
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        // Get the attendee data
                        Attendees attendees = data.getValue(Attendees.class);

                        // Make sure attendee isn't null
                        if (attendees != null) {

                            // get the data to create an attendee object
                            String attendeeUsername = attendees.getUsername();
                            String attendeeImageUrl = attendees.getUserProfileImage();

                            attendeesArrayList.add(new Attendees(attendeeUsername, attendeeImageUrl));
                        }
                    }

                    // call the update attendee list to display the attendee's to the user
                    updateAttendeeList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            attendeeDatabaseReference.keepSynced(true);

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference studyGroupsTestsDatabaseReference = mFirebaseDatabase.getReference("/studyGroups/studyGroupsToDisplay/" + studyGroupChosen.getStudyGroupId() + "/studyGroupTests");

            studyGroupsTestsDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Loop through the data snapshot
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        // Get the attendee data
                        StudyGroupFlashcardTest tests = data.getValue(StudyGroupFlashcardTest.class);

                        // Make sure attendee isn't null
                        if (tests != null) {

                            // get the data to create an attendee object
                            String flashcardTestName = tests.getFlashcardTestName();
                            int questionCount = tests.getFlashcardCount();
                            String dateString = tests.getFlashcardCreationDate();
                            String testId = tests.getFlashcardTestId();
                            String testCreator = tests.getCreatorUsername();

                            studyGroupFlashcardTests.add(new StudyGroupFlashcardTest(flashcardTestName, questionCount, dateString, testId, testCreator));
                        }
                    }

                    updateTestList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            studyGroupsTestsDatabaseReference.keepSynced(true);

            // Get the users profile smart image view to display the creators profile picture
            SmartImageView studyGroupCreatorSmrtImg = findViewById(R.id.study_group_creator_smrt_img);

            // Display the creators profile image to the smart image declared above
            studyGroupCreatorSmrtImg.setImageUrl(studyGroupChosen.getStudyGroupCreatorImgUrl());

            // Get the study group creator text view to display the study group creator
            TextView studyGroupCreatedByTxtVw = findViewById(R.id.study_group_joined_creator_txt_vw);

            // Display the study group creator username to the user
            studyGroupCreatedByTxtVw.setText(getResources().getString(R.string.study_group_creator_username, studyGroupChosen.getCreatorUsername()));

            // Get the study group address text view to display the study group address
            TextView studyGroupAddressTxtVw = findViewById(R.id.study_group_joined_address_txt_vw);

            // Display the study group address to the user
            studyGroupAddressTxtVw.setText(getResources().getString(R.string.study_group_address, studyGroupChosen.getStudyGroupAddress(), studyGroupChosen.getStudyGroupCity(), studyGroupChosen.getStudyGroupState()));
        }
    }

    private void updateAttendeeList(){

        // AttendeeListAdapter to display the study group attendees to the user
        AttendeeListAdapter attendeeListAdapter = new AttendeeListAdapter(this, R.layout.attendee_list_row, attendeesArrayList);

        // Display the attendees to the attendee list view with the adapter created above
        attendeeListView.setAdapter(attendeeListAdapter);
    }

    private void updateTestList(){

        StudyGroupTestListAdapter testListAdapter = new StudyGroupTestListAdapter(this, R.layout.test_row, studyGroupFlashcardTests);

        testListView.setAdapter(testListAdapter);
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
