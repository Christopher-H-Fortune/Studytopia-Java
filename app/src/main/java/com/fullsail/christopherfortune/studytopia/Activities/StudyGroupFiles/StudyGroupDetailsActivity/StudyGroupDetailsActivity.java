package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupDetailsActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.CalendarContract;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.AttendeeListAdapter.AttendeeListAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.Attendees.Attendees;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroupJoined.StudyGroupJoined;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupAttendeeListFragment.StudyGroupAttendeesListFragment;
import com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupChosenMapFragment.StudyGroupChosenMapFragment;
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
import java.util.Calendar;
import java.util.TimeZone;

public class StudyGroupDetailsActivity extends AppCompatActivity implements StudyGroupAttendeesListFragment.StudyGroupAttendeeListInterface {

    private DrawerLayout drawerLayout;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    public StudyGroup studyGroupChosen;
    private ArrayList<Attendees> attendeesArrayList = new ArrayList<>();
    private ListView attendeeListView;
    private int totalStudyGroupCount;
    private FirebaseUser user;
    private String studyGroupJoined = "";
    private boolean requestingUpdates = false;
    private static final int REQUEST_CALENDER_PERMISSIONS = 0x09090;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_details);

        // Get the intent passed to the activity
        Intent startingIntent = getIntent();

        // If the starting intent has data
        if (startingIntent != null) {

            // Get the study group object started
            studyGroupChosen = (StudyGroup) startingIntent.getSerializableExtra("studyGroupChosen");
        }

        // Set the instance of the Firebase auth and database
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Check if the user has granted us permission to write to their calender to save the study group date to their calender
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED && !requestingUpdates){

            System.out.println("Access Granted");

        // If we don't have access to write to their calender, ask for it
        } else {

            // Set the requestingUpdates to true
            requestingUpdates = true;

            // Ask the user for permission to write to their calender
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_CALENDAR}, REQUEST_CALENDER_PERMISSIONS);
        }

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if (user != null) {

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

                            totalStudyGroupCount = studyGroupCount;
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

        DatabaseReference studyGroupJoinedReference = mFirebaseDatabase.getReference();
        studyGroupJoinedReference.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("currentStudyGroup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studyGroupJoined = (String) dataSnapshot.getValue();
                System.out.println(studyGroupJoined);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set the database reference using the mFirebaseDatabase
        DatabaseReference attendeeDatabaseReference = mFirebaseDatabase.getReference("/studyGroups/studyGroupsToDisplay/" + studyGroupChosen.getStudyGroupId() + "/attendees");

        // Set the database to have a ValueEventListener to display the attendees data to the user
        attendeeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the attendee data
                    Attendees attendees = data.getValue(Attendees.class);

                    // Make sure attendee isn't null
                    if (attendees != null) {

                        // Get the data to create an attendee object
                        String attendeeUsername = attendees.getUsername();
                        String attendeeImageUrl = attendees.getUserProfileImage();

                        // Add the attendee object to the attendee array list
                        attendeesArrayList.add(new Attendees(attendeeUsername, attendeeImageUrl));
                    }
                }

                // Call the updateAttendeeList method to display the attendees to the user
                updateAttendeeList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        attendeeDatabaseReference.keepSynced(true);


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

                        if (networkInfo != null) {

                            if (networkInfo.isConnected()) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent mapIntent = new Intent(StudyGroupDetailsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupDetailsActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupDetailsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupDetailsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupDetailsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupDetailsActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupDetailsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupDetailsActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + studyGroupChosen.getStudyGroupName() + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the map to show the study group location to the user
        getFragmentManager().beginTransaction().replace(R.id.chosen_group_map_frame, StudyGroupChosenMapFragment.newInstance(), StudyGroupChosenMapFragment.TAG).commit();

        // Get the views to display the study group details to the user
        TextView studyGroupAddressTxtVw = findViewById(R.id.study_group_chosen_detail_address_txt_vw);
        TextView studyGroupSubjectTxtVw = findViewById(R.id.study_group_chosen_detail_subject_chosen_txt_vw);
        TextView studyGroupDateTimeTxtVw = findViewById(R.id.study_group_chosen_detail_date_time_txt_vw);
        TextView studyGroupAttendeeCountTxtVw = findViewById(R.id.study_group_chosen_detail_attendees_count_txt_vw);
        TextView studyGroupCreatorTxtVw = findViewById(R.id.study_group_chosen_detail_creator_txt_vw);

        // Display the study group details to the user
        studyGroupAddressTxtVw.setText(getResources().getString(R.string.study_group_address, studyGroupChosen.getStudyGroupAddress(), studyGroupChosen.getStudyGroupCity(), studyGroupChosen.getStudyGroupState()));
        studyGroupSubjectTxtVw.setText(studyGroupChosen.getStudyGroupSubject());
        studyGroupDateTimeTxtVw.setText(getResources().getString(R.string.study_group_date_time, studyGroupChosen.getStudyGroupDate(), studyGroupChosen.getStudyGroupTime()));
        studyGroupAttendeeCountTxtVw.setText(getResources().getString(R.string.study_group_max_attendee_count, studyGroupChosen.getStudyGroupAttendeeCount()));
        studyGroupCreatorTxtVw.setText(getResources().getString(R.string.study_group_creator_username, studyGroupChosen.getCreatorUsername()));

        // Display the StudyGroupAttendeesListFragment to the user so they can view the attendees of the study group
        getSupportFragmentManager().beginTransaction().replace(R.id.attendee_list_frame, StudyGroupAttendeesListFragment.newInstance(), StudyGroupAttendeesListFragment.TAG).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If the request code is to allow us to write to the users calender
        if(requestCode == REQUEST_CALENDER_PERMISSIONS){

            requestingUpdates = false;

            // If the user granted access to write to their calender
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED && !requestingUpdates){

                System.out.println("Access Granted to write to calender.");
            }
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
                if (userProfileData != null) {

                    // Display the users profile pic, first and last name, and email
                    userProfileImageSmrtImage.setImageUrl(userProfileData.getImageUrl());
                    userProfileImageSmrtImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profileIntent = new Intent(StudyGroupDetailsActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.join_study_group:

                // call the addUserToStudyGroup method to join the user to the study group
                addUserToStudyGroup();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.join_group_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void passListView(ListView attendeeListView) {

        // Store the attendeeListView to display the attendees to the user
        this.attendeeListView = attendeeListView;
    }

    private void updateAttendeeList() {

        // Create an AttendeeListAdapter to display the attendees to the user
        AttendeeListAdapter attendeeListAdapter = new AttendeeListAdapter(this, R.layout.attendee_list_row, attendeesArrayList);

        // Set the adapter created above to the attendeeListView
        attendeeListView.setAdapter(attendeeListAdapter);
    }

    private void addUserToStudyGroup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        AlertDialog alertDialog;
        if (studyGroupJoined != null) {

            if (studyGroupJoined.equals(studyGroupChosen.getStudyGroupId())) {

                // Display an alert dialog to ask the user to enter a username
                alertDialogBuilder.setTitle("Already In This Study Group");
                alertDialogBuilder.setMessage("You are currently already attending this study group.");
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else {

                saveStudyGroupData();

            }
        } else {

            saveStudyGroupData();
        }

    }

    private void saveStudyGroupData() {

        // Create an attendees object from the user profile data
        Attendees userToSave = new Attendees(userProfileData.getUsername(), userProfileData.getImageUrl());

        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        // Store the user to the study group in the database
        databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(studyGroupChosen.getStudyGroupId()).child("attendees").child(user.getUid()).setValue(userToSave);

        // Get the current user signed in
        final FirebaseUser user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if (user != null) {

            StudyGroupJoined studyGroupJustJoined = new StudyGroupJoined(studyGroupChosen.getStudyGroupName(), studyGroupChosen.getStudyGroupId(), studyGroupChosen.getStudyGroupDate());

            // Set the user's currently joined study group
            databaseReference.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("currentStudyGroup").child(studyGroupChosen.getStudyGroupId()).setValue(studyGroupJustJoined).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    long calID = 3;
                    long startMillis, endMillis;
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(studyGroupChosen.getStudyGroupYear(), studyGroupChosen.getStudyGroupMonth(), studyGroupChosen.getStudyGroupDay(), studyGroupChosen.getStudyGroupHour(), studyGroupChosen.getStudyGroupMinute());
                    startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(studyGroupChosen.getStudyGroupYear(), studyGroupChosen.getStudyGroupMonth(), studyGroupChosen.getStudyGroupDay(), 23, 59);
                    endMillis = endTime.getTimeInMillis();

                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.DTEND, endMillis);
                    values.put(CalendarContract.Events.TITLE, studyGroupChosen.getStudyGroupName() + " Study Group");
                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
                    if (ActivityCompat.checkSelfPermission(StudyGroupDetailsActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                        // Ask the user to allow us to write to their calender
                        ActivityCompat.requestPermissions(StudyGroupDetailsActivity.this, new String[] {Manifest.permission.WRITE_CALENDAR}, REQUEST_CALENDER_PERMISSIONS);
                        return;

                    } else {

                        //TODO: Rework the calender permissions to work correctly

                        // Add the date to the users calender
                        cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    }

                    Intent studyGroupIntent = new Intent(StudyGroupDetailsActivity.this, StudyGroupActivity.class);
                    startActivity(studyGroupIntent);
                }
            });

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
