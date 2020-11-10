package com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity;

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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardCategoryListActivity.PublicFlashcardsCategoryListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.TakePublicFlashcardTestActivity.TakePublicFlashcardTestActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.PublicFlashcardTests.PublicFlashcardTests;
import com.fullsail.christopherfortune.studytopia.Adapters.PublicFlashcardTestsAdapter.PublicFlashcardTestAdapter;
import com.fullsail.christopherfortune.studytopia.Fragments.PublicFlashcardsFragment.PublicFlashcardsFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
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

public class PublicFlashcardsActivity extends AppCompatActivity implements PublicFlashcardsFragment.PublicFlashcardsInterface {

    private DrawerLayout drawerLayout;
    private UserData userProfileData = new UserData();
    private ListView publicFlashcardListView;
    private String subjectChosenString;
    private ArrayList<PublicFlashcardTests> publicFlashcardTestsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_flashcards);

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("subjectChosen")){

            subjectChosenString = startingIntent.getStringExtra("subjectChosen");
        }

        // Get an instance of the firebase authentication and database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        // Get the current user signed in
        final FirebaseUser user = mAuth.getCurrentUser();

        // Make sure the user ins't null
        if(user != null){

            // Get the users ID
            final String uId = user.getUid();

            // Set the database reference using the mFirebaseDatabase
            DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference("/users/" + uId);
            mDatabaseReference.keepSynced(true);

            // Set the database to have a ValueEventListener to display the games data to the user
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Loop through the data snapshot
                    for(DataSnapshot data : dataSnapshot.getChildren()){

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
        }

        // Create a database reference to obtain the data from the firebase database
        DatabaseReference publicFlashcardsDatabaseRef = mFirebaseDatabase.getReference();

        // Get the public flashcard tests from the subject chosen
        publicFlashcardsDatabaseRef.child("publicTests").child("subjects").child(subjectChosenString).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear the Array List to prevent duplicates
                publicFlashcardTestsArrayList.clear();

                // If there is data
                if(dataSnapshot.getValue() != null){

                    // Loop through each object obtained
                    for(DataSnapshot data : dataSnapshot.getChildren()) {

                        // Get the publicFlashcards data
                        PublicFlashcardTests publicFlashcards = data.getValue(PublicFlashcardTests.class);

                        // Make sure publicFlashcards isn't null
                        if (publicFlashcards != null) {

                            // Get the data to create a publicFlashcardTest object
                            String testId = publicFlashcards.getFlashcardTestId();
                            String testName = publicFlashcards.getFlashcardTestName();
                            int flashcardCount = publicFlashcards.getFlashcardCount();
                            boolean isPublic = publicFlashcards.isFlashcardPublic();
                            String creationDate = publicFlashcards.getFlashcardCreationDate();
                            String testCreator = publicFlashcards.getFlashcardTestCreator();

                            // Add the PublicFlashcardTests to the publicFlashcardTestsArrayList
                            publicFlashcardTestsArrayList.add(new PublicFlashcardTests(testId, testName, flashcardCount, isPublic, creationDate, testCreator));
                        }
                    }
                }

                // Call the update list method to display the new tests
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        publicFlashcardsDatabaseRef.keepSynced(true);

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
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        if(networkInfo != null){

                            if(networkInfo.isConnected()){
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent mapIntent = new Intent(PublicFlashcardsActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PublicFlashcardsActivity.this);
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
                        Intent studyGroupIntent = new Intent(PublicFlashcardsActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(PublicFlashcardsActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(PublicFlashcardsActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(PublicFlashcardsActivity.this, PublicFlashcardsCategoryListActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsIntent = new Intent(PublicFlashcardsActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(PublicFlashcardsActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Public Flashcards</font>", Html.FROM_HTML_MODE_LEGACY));

            StringBuilder uppercaseStringBuilder = new StringBuilder(subjectChosenString.length());

            String[] subjectChosenWordArray = subjectChosenString.split(" ");

            for (String s : subjectChosenWordArray) {

                uppercaseStringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
                System.out.println(uppercaseStringBuilder);
            }

            actionbar.setSubtitle(Html.fromHtml("<small><font color='#ffffff'>" + uppercaseStringBuilder + "</small></font>", Html.FROM_HTML_MODE_LEGACY));
        }

        // Display the PublicFlashcardsFragment to the user
        getSupportFragmentManager().beginTransaction().replace(R.id.public_flashcards_frame, PublicFlashcardsFragment.newInstance(), PublicFlashcardsFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(PublicFlashcardsActivity.this, UserProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    });
                    firstLastNameTxtVw.setText(userProfileData.getFirstName() + " " + userProfileData.getLastName());
                    emailTxtVw.setText(userProfileData.getEmail());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void passListView(ListView publicListView) {

        // Pass the listView to display the data obtained from the database
        publicFlashcardListView = publicListView;
    }

    @Override
    public void testSelected(final int testSelected) {

        // Ask the user if they want to take the test
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Take Test?");
        alertDialogBuilder.setMessage("To take the test, tap take test. To view test list tap cancel.");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(
                "Take Test",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Create a flashcardTest object from the test chosen in the list view
                        PublicFlashcardTests publicFlashcardTestChosen = publicFlashcardTestsArrayList.get(testSelected);

                        // Intent to send the user to the TakeFlashcardTestActivity so they can take the test selected
                        Intent takePublicTestIntent = new Intent(PublicFlashcardsActivity.this, TakePublicFlashcardTestActivity.class);

                        // Pass the flashcard test chosen object and subject chosen string to the TakeFlashcardTestActivity
                        takePublicTestIntent.putExtra("testChosen", publicFlashcardTestChosen);
                        takePublicTestIntent.putExtra("subjectChosen", subjectChosenString);

                        // Start the TakeFlashcardTestActivity with the intent created above
                        startActivity(takePublicTestIntent);
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
    }

    private void updateList(){

        if(publicFlashcardTestsArrayList.size() == 0){

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View emptyView = layoutInflater.inflate(R.layout.empty_view_stub, null);
            publicFlashcardListView.setEmptyView(emptyView);
        } else {
            // Create a PublicFlashcardTestAdapter to set to the listView
            PublicFlashcardTestAdapter publicFlashcardTestAdapter = new PublicFlashcardTestAdapter(this, R.layout.public_flashcard_row, publicFlashcardTestsArrayList);

            // Set the adapter created above to the publicFlashcardListView
            publicFlashcardListView.setAdapter(publicFlashcardTestAdapter);
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
