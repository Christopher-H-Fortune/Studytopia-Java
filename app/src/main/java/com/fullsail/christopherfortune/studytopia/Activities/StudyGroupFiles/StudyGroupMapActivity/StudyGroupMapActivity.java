package com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.CreateStudyGroupActivity.StudyGroupCreationActivity;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Fragments.GoogleMapFragment.GoogleMapFragment;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupDetailsActivity.StudyGroupDetailsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.image.SmartImageView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class StudyGroupMapActivity extends AppCompatActivity implements LocationListener, GoogleMapFragment.GoogleMapInterface {

    private DrawerLayout drawerLayout;
    private FirebaseDatabase mFirebaseDatabase;
    private GoogleMapFragment googleMapFragment;
    public ActionBar actionbar;
    private UserData userProfileData = new UserData();
    public ArrayList<StudyGroup> studyGroupsArrayList = new ArrayList<>();
    private static final int REQUEST_LOCATION_PERMISSIONS = 0x03030;
    private LocationManager locationManager;
    private boolean requestingUpdates = false;
    private int searchRange = 25;
    public Location lastKnownLocation;
    private GoogleMap studyGroupGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_group_map);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !requestingUpdates){

            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            requestingUpdates = false;

            locationManager.removeUpdates(this);

            // Display the GoogleMapFragment so the user can view the study groups within the map
            getFragmentManager().beginTransaction().replace(R.id.study_group_map_frame, GoogleMapFragment.newInstance(), GoogleMapFragment.TAG).commit();

            // Get the google Map fragment to make updates to the markers within
            googleMapFragment = new GoogleMapFragment();

            // Call the getStudyGroups method to obtain the study groups to display on the map
            getStudyGroups();

            // Get the googleMapFragment to add the map markers
            googleMapFragment.addStudyGroupMapMarkers();

        } else {
            // Request permissions if we don't have them
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
        }

        // Get the current user signed in
        final FirebaseUser user = mAuth.getCurrentUser();

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
                                Intent mapIntent = new Intent(StudyGroupMapActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudyGroupMapActivity.this);
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
                        Intent studyGroupIntent = new Intent(StudyGroupMapActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(StudyGroupMapActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(StudyGroupMapActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(StudyGroupMapActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(StudyGroupMapActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(StudyGroupMapActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Study Groups Map</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Get the seekbar to determine the search range
        SeekBar searchRangeSeekBar = findViewById(R.id.map_range_seek_bar);

        // Set the seekbar value to the default search range of 25 miles
        searchRangeSeekBar.setProgress(searchRange);

        // set OnSeekBarChangeListener to allow the user to search for Study groups withing a larger radius
        searchRangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // Set the search range to the number chosen on the seekbar
                searchRange = progress;

                // Get the search range text view to display the search range selected by the user
                TextView searchRange = findViewById(R.id.search_rng_count_text_view);

                // Display the search range from the position on the seek bar
                searchRange.setText(getResources().getString(R.string.search_range, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                getStudyGroups();
            }
        });
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
                            Intent profileIntent = new Intent(StudyGroupMapActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
            case R.id.create_study_group:

                // Intent to send the user to the studyGroupCreationActivity so they can create a study group
                Intent createStudyGroupIntent = new Intent(this, StudyGroupCreationActivity.class);

                // Start the study group creation activity
                startActivity(createStudyGroupIntent);

                break;
            case R.id.join_study_group:

                // Ask the user if they want to join the study group
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Join Study Group?");
                alertDialogBuilder.setMessage("To join the study group tap Join. To cancel, tap cancel.");
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setPositiveButton(
                        "Join",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // Join the user to group and send to study group screen
                                Intent studyGroupIntent = new Intent(StudyGroupMapActivity.this, StudyGroupActivity.class);

                                // Start the StudyGroupActivity with the intent create above so they can view the joined study group
                                startActivity(studyGroupIntent);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If the request code is to get the users location
        if(requestCode == REQUEST_LOCATION_PERMISSIONS){

            // If the user granted access to obtain their location
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !requestingUpdates){

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                requestingUpdates = false;

                locationManager.removeUpdates(this);

                // Display the GoogleMapFragment so the user can view the study groups within the map
                getFragmentManager().beginTransaction().replace(R.id.study_group_map_frame, GoogleMapFragment.newInstance(), GoogleMapFragment.TAG).commit();

                // Get the google Map fragment to make updates to the markers within
                googleMapFragment = new GoogleMapFragment();

                // Call the getStudyGroups method to obtain the study groups to display on the map
                getStudyGroups();

                // Get the googleMapFragment to add the map markers
                googleMapFragment.addStudyGroupMapMarkers();
            }

        }
    }


    @Override
    public void onLocationChanged(Location location) {

        // If we have access to the get the users location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && !requestingUpdates){

            // get the users last known location
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Set requesting updates to false
            requestingUpdates = false;

            // Remove updates from the location manager
            locationManager.removeUpdates(this);
        } else {
            // Request permissions if we don't have them
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getStudyGroups(){
        DatabaseReference studyGroupsReference = mFirebaseDatabase.getReference("/studyGroups/studyGroupsToDisplay");
        studyGroupsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear the arrayList of study groups
                studyGroupsArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // get the data as a study group object
                    StudyGroup studyGroup = data.getValue(StudyGroup.class);

                    // If the study group isn't null
                    if(studyGroup != null){

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

                        // Location object to get get the distance from the user to the study group
                        Location studyGroupLocation = new Location("study_group_location");

                        // Set the location object above with the study group lat and long
                        studyGroupLocation.setLatitude(studyGroupLat);
                        studyGroupLocation.setLongitude(studyGroupLong);

                        if(lastKnownLocation != null){

                            // Get distance from the users last know location the the study group in meters
                            double distanceInMeters = lastKnownLocation.distanceTo(studyGroupLocation);

                            // convert the meters to miles
                            double distanceInMiles = distanceInMeters * 0.00062137;

                            // Convert the distance in miles to a big decimal
                            BigDecimal bigDecimal = new BigDecimal(Double.toString(distanceInMiles));

                            // Round the miles up
                            bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);

                            // Convert the big decimal above
                            distanceInMiles = bigDecimal.doubleValue();

                            // If the study group is within the search range
                            if(distanceInMiles < searchRange){

                                // Add the study group to the array list to display on the map
                                studyGroupsArrayList.add(new StudyGroup(studyGroupCreatorImgUrl, studyGroupName, studyGroupSubject, studyGroupCompleteAddressAddress, studyGroupAddress,
                                        studyGroupCity, studyGroupState,studyGroupLat, studyGroupLong, studyGroupDate, studyGroupTime, attendeeCount,isOnMap, creatorUsername, studyGroupId, studyGroupHour, studyGroupMinute, studyGroupDay, studyGroupMonth, studyGroupYear));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        studyGroupsReference.keepSynced(true);

        // If the study groups array list is not empty
        if(studyGroupsArrayList.size() != 0){

            // Marker options to display the study groups on the map
            MarkerOptions markerOptions = new MarkerOptions();

            // Clear the map of any previous markers
            studyGroupGoogleMap.clear();

            // For each study group object in the study groups array list
            for(StudyGroup studyGroup: studyGroupsArrayList){

                // Set the marker title with the study group name
                markerOptions.title(studyGroup.getStudyGroupName());

                // Set the marker snippet with the study group address
                markerOptions.snippet(studyGroup.getStudyGroupCompleteAddress());

                // Create a latLng object from the study group location to display it on the map
                LatLng studyGroupLocation = new LatLng(studyGroup.getStudyGroupLat(), studyGroup.getStudyGroupLong());

                // Set the marker to the studyGroupLocation
                markerOptions.position(studyGroupLocation);

                // Add the study group to the map with the marker created above
                studyGroupGoogleMap.addMarker(markerOptions);
            }
        }
    }

    @Override
    public void passMap(GoogleMap googleMap) {

        // Store the google map to make marker updates to the map
        studyGroupGoogleMap = googleMap;
    }


    @Override
    public void displayDetailFragment(Marker markerChosen) {

        // Get the study group name from the marker title
        String studyGroupName = markerChosen.getTitle();

        // For each study group in the study group array list
        for(StudyGroup studyGroup: studyGroupsArrayList){

            // If the study group name is equal to the study group name chosen
            if (studyGroup.getStudyGroupName().equals(studyGroupName)) {

                // Intent to send the user to the StudyGroupDetailsActivity
                Intent studyGroupDetailsIntent = new Intent(this, StudyGroupDetailsActivity.class);

                // Pass the study group chosen object so they can view the study group details
                studyGroupDetailsIntent.putExtra("studyGroupChosen", studyGroup);

                // Start the study group details activity with the intent created above
                startActivity(studyGroupDetailsIntent);
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
