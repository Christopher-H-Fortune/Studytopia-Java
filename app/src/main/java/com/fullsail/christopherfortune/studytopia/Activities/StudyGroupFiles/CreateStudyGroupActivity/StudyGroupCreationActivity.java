package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.CreateStudyGroupActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.Attendees.Attendees;
import com.fullsail.christopherfortune.studytopia.Fragments.CreateStudyGroupFragment.CreateStudyGroupFragment;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class StudyGroupCreationActivity extends AppCompatActivity implements CreateStudyGroupFragment.CreateStudyGroupInterface {

    private FirebaseDatabase mFirebaseDatabase;
    private  FirebaseUser user;
    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    private EditText studyGroupNameEdtTxt, studyGroupSubjectEdtTxt, studyGroupAddressEdtTxt, studyGroupCityEdtTxt, studyGroupStateEdtTxt, studyGroupDateEdtTxt, studyGroupTimeEdtTxt;
    private int month, dayOfMonth, year, hour, minute, attendeeCount, hourOfDayForCalendar;
    private static int CALENDAR_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_creation);

        // Set the instance of the Firebase auth and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        user = mAuth.getCurrentUser();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, CALENDAR_REQUEST_CODE);
        }

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
                                Intent mapIntent = new Intent(StudyGroupCreationActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupCreationActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupCreationActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupCreationActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupCreationActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupCreationActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupCreationActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupCreationActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Create Study Group</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the CreateStudyGroupFragment to the user so they can create a study group
        getSupportFragmentManager().beginTransaction().replace(R.id.study_group_creation_frame, CreateStudyGroupFragment.newInstance(), CreateStudyGroupFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(StudyGroupCreationActivity .this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.save:

                // Call the saveStudyGroup method to save the study group created
                saveStudyGroup();
                break;
        }
        return true;
    }

    @Override
    public void passViews(EditText studyGroupNameEdtTxt, EditText studyGroupSubjectEdtTxt, EditText studyGroupAddressEdtTxt, EditText studyGroupCityEdtTxt, EditText studyGroupStateEdtTxt, EditText studyGroupDateEdtTxt, EditText studyGroupTimeEdtTxt) {

        // store the views to obtain the data the user entered
        this.studyGroupNameEdtTxt = studyGroupNameEdtTxt;
        this.studyGroupSubjectEdtTxt = studyGroupSubjectEdtTxt;
        this.studyGroupAddressEdtTxt = studyGroupAddressEdtTxt;
        this.studyGroupCityEdtTxt = studyGroupCityEdtTxt;
        this.studyGroupStateEdtTxt = studyGroupStateEdtTxt;
        this.studyGroupDateEdtTxt = studyGroupDateEdtTxt;
        this.studyGroupTimeEdtTxt = studyGroupTimeEdtTxt;
    }

    @Override
    public void pickDate() {

        // Calendar object to get the current Month, Day, and year
        Calendar calendar = Calendar.getInstance();

        // Store the current Month, day, and year
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        year = calendar.get(Calendar.YEAR);

        // Display a DatePickerDialog to allow the user to select a date and set the date of the date picker with the values obtained above
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // Display the date the user chose
                studyGroupDateEdtTxt.setText(getResources().getString(R.string.date_chosen, month + 1, dayOfMonth, year));
                StudyGroupCreationActivity.this.month = month;
                StudyGroupCreationActivity.this.dayOfMonth = dayOfMonth;
                StudyGroupCreationActivity.this.year = year;

            }
        }, year, month, dayOfMonth);

        // Show the datePickerDialog to the user
        datePickerDialog.show();
    }

    @Override
    public void pickTime() {

        // Calendar object to get the current hour and minute
        Calendar calendar = Calendar.getInstance();

        // Get the current hour and minute
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        // Time picker dialog to allow the user to pick a time for the study group with it displaying the current time
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                // String to represent if its noon or morning
                String amOrPm;

                hourOfDayForCalendar = hourOfDay;

                // If the hour of day is less than 12
                if(hourOfDay < 12){

                    // set the time to am
                    amOrPm = "a.m.";

                // if the time is in the afternoon
                } else {

                    // Set the time to pm
                    amOrPm = "p.m.";
                    hourOfDay -= 12;
                }

                String minuteString;

                if(minute < 10){

                    minuteString = "0" + minute;

                } else {

                    minuteString = Integer.toString(minute);
                }

                // Display the time chosen by the user
                studyGroupTimeEdtTxt.setText(getResources().getString(R.string.time_chosen, hourOfDay, minuteString, amOrPm));
                hour = hourOfDay;
                StudyGroupCreationActivity.this.minute = minute;

            }
        }, hour, minute, false);

        // Display the timePickerDialog to the user
        timePickerDialog.show();
    }

    @Override
    public void passAttendeeCount(int attendeeCount) {

        // Store the attendee count passed
        this.attendeeCount = attendeeCount;
    }

    private void saveStudyGroup(){

        // Get the data the user entered to create a study group
        String studyGroupNameString = studyGroupNameEdtTxt.getText().toString().trim();
        String studyGroupSubjectString = studyGroupSubjectEdtTxt.getText().toString().trim();
        String addressString = studyGroupAddressEdtTxt.getText().toString().trim();
        String cityString = studyGroupCityEdtTxt.getText().toString().trim();
        String stateString = studyGroupStateEdtTxt.getText().toString().trim();
        String studyGroupDateString = studyGroupDateEdtTxt.getText().toString().trim();
        String studyGroupTimeString = studyGroupTimeEdtTxt.getText().toString().trim();

        // Variables to create the studyGroupObject
        String completeAddressString = "";
        double addressLatitude = 0.0;
        double addressLongitude = 0.0;

        // ColorStateList to change the edit text color if the user need to update the fields of information required to create an account
        ColorStateList editTextColorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_light));

        // If the user didn't enter a study group name
        if(studyGroupNameString.equals("")){

            studyGroupNameEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupNameEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupNameEdtTxt.setText(null);
            studyGroupNameEdtTxt.setHint("Study Group name required.");

        // If the study group name is less than 3 characters in length
        } else if (studyGroupNameString.length() <= 3){

            studyGroupNameEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupNameEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupNameEdtTxt.setText(null);
            studyGroupNameEdtTxt.setHint("Name must be at least 3 characters.");
        }

        // If the user didn't enter a study group subject
        if(studyGroupSubjectString.equals("")){

            studyGroupSubjectEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupSubjectEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupSubjectEdtTxt.setText(null);
            studyGroupSubjectEdtTxt.setHint("Subject Required.");

        // If the study group subject is less than 3 characters in length
        } else if (studyGroupSubjectString.length() <= 3){

            studyGroupSubjectEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupSubjectEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupSubjectEdtTxt.setText(null);
            studyGroupSubjectEdtTxt.setHint("Subject must be at least 3 characters.");
        }

        // If the user didn't enter a study group address
        if(addressString.equals("")){

            studyGroupAddressEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupAddressEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupAddressEdtTxt.setText(null);
            studyGroupAddressEdtTxt.setHint("Address Required.");

        // If the user didn't enter a city
        } else if (cityString.equals("")){

            studyGroupCityEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupCityEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupCityEdtTxt.setText(null);
            studyGroupCityEdtTxt.setHint("City Required.");

        // If user didn't enter a state
        } else if(stateString.equals("")){

            studyGroupStateEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupStateEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupStateEdtTxt.setText(null);
            studyGroupStateEdtTxt.setHint("State Required.");

        // If the user entered data to find study group location
        } else {

            // Create a complete address to get the location of the study group
            completeAddressString = addressString +", "+ cityString + ", " + stateString;

            // Create Address list to create an address from the address the user entered
            List<Address> addressList;

            // Geocoder object to get the lat and long of the study group location
            Geocoder geocoder = new Geocoder(this);

            try{
                // Convert the address the user entered to an Address Object
                addressList = geocoder.getFromLocationName(completeAddressString, 1);

                // If the list isn't null
                if(addressList != null){

                    // Get the address the user entered
                    Address addressLocation = addressList.get(0);

                    // Get the latitude and longitude of the address the user entered
                    addressLatitude = addressLocation.getLatitude();
                    addressLongitude = addressLocation.getLongitude();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If the address Lat and Long wasn't found
        if(addressLatitude == 0.0 && addressLongitude == 0.0){

            // Display an alert dialog to ask the user to enter a valid address
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Address Not Found");
            alertDialogBuilder.setMessage("To create a study group, enter a valid address. Double check the address entered to make sure it's correct.");
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

        // If the user didn't choose a study group date
        if(studyGroupDateString.equals("")){

            studyGroupDateEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupDateEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupDateEdtTxt.setText(null);
            studyGroupDateEdtTxt.setHint("Date Required.");
        }

        // If he user didn't choose a study group time
        if(studyGroupTimeString.equals("")){

            studyGroupTimeEdtTxt.setBackgroundTintList(editTextColorStateList);
            studyGroupTimeEdtTxt.setHintTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));

            studyGroupTimeEdtTxt.setText(null);
            studyGroupTimeEdtTxt.setHint("Time Required.");
        }

        // If the user didn't choose an attendee count
        if(attendeeCount == 0){

            // Display an alert dialog to ask the user to enter a first name
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Please Enter an Attendee Count");
            alertDialogBuilder.setMessage("To create a study group, please select the max amount of attendees to attend to the group.");
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

        // Set a reference to our Firebase database
        DatabaseReference databaseReference = mFirebaseDatabase.getReference();

        // If the current user isn't null
        if(user != null){

            // If the user entered all the data to create a study group object
            if(!studyGroupNameString.equals("") && !studyGroupSubjectString.equals("") && !completeAddressString.equals("") && addressLatitude != 0.0 && addressLongitude != 0.0 && !studyGroupDateString.equals("") && !studyGroupTimeString.equals("") && attendeeCount != 0){

                // Get a unique key to prevent study group overwrites
                String uniqueKey = UUID.randomUUID().toString();

                // Create a studyGroup object from the data the user entered
                StudyGroup studyGroup = new StudyGroup(userProfileData.getImageUrl(), studyGroupNameString, studyGroupSubjectString, completeAddressString, addressString, cityString,
                        stateString, addressLatitude, addressLongitude, studyGroupDateString, studyGroupTimeString, attendeeCount, true, userProfileData.getUsername(), uniqueKey, hour, minute, dayOfMonth, month, year);

                // Create an attendees object to set the creator as an attendee
                Attendees attendees = new Attendees(userProfileData.getUsername(), userProfileData.getImageUrl());

                // If the unique key isn't null
                if(uniqueKey != null){

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, CALENDAR_REQUEST_CODE);
                    }

                    long calID = 1;
                    long startMillis, endMillis;
                    Calendar beginTime = Calendar.getInstance();
                    beginTime.set(year, month, dayOfMonth, hourOfDayForCalendar, minute);
                    startMillis = beginTime.getTimeInMillis();
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(year, month, dayOfMonth, 23, 59);
                    endMillis = endTime.getTimeInMillis();

                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Events.DTSTART, startMillis);
                    values.put(CalendarContract.Events.DTEND, endMillis);
                    values.put(CalendarContract.Events.TITLE, studyGroupNameString + " Study Group");
                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                    if (uri != null) {
                        if(uri.getLastPathSegment() != null){
                            long eventID = Long.parseLong(uri.getLastPathSegment());
                        }
                    }

                    // Store the study group and attendees to the database
                    databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(uniqueKey).setValue(studyGroup);
                    databaseReference.child("studyGroups").child("studyGroupsToDisplay").child(uniqueKey).child("attendees").child(uniqueKey).setValue(attendees);
                    databaseReference.child("users").child(user.getUid()).child("userProfile").child("studyGroups").child("currentStudyGroup").setValue(uniqueKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Intent to send the user to the study group
                            Intent studyGroupIntent = new Intent(StudyGroupCreationActivity.this, StudyGroupActivity.class);

                            // Start the StudyGroupActivity with the intent created above
                            startActivity(studyGroupIntent);
                        }
                    });
                }
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
