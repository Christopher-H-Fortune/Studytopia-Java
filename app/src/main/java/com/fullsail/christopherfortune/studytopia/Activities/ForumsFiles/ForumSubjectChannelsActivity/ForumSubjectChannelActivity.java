package com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumSubjectChannelsActivity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumChannelMessagesActivity.ForumChannelMessagesActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.ForumChannelsAdapter.ForumChannelsAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.ForumChannel.ForumChannel;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.R;
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
import java.util.UUID;

public class ForumSubjectChannelActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private UserData userProfileData = new UserData();
    private ArrayList<ForumChannel> forumChannelArrayList = new ArrayList<>();
    private ListView userCreatedForumChannelsListView;
    private String subjectChosenString;
    private ForumChannelsAdapter forumChannelsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_subject_channel_activity);

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("subjectChosen")){
            subjectChosenString = startingIntent.getStringExtra("subjectChosen");
        }

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

        final DatabaseReference forumChannelsReference = mFirebaseDatabase.getReference();
        forumChannelsReference.child("subjectsForum").child(subjectChosenString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                forumChannelArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the forumChannel data
                    ForumChannel forumChannel = data.getValue(ForumChannel.class);

                    // Make sure forumChannel isn't null
                    if (forumChannel != null) {

                        // Get the forumChannel data to create a forumChannel object
                        String channelName = forumChannel.getChannelName();
                        String userId = forumChannel.getCreatorId();
                        String creatorUserName = forumChannel.getCreatorUsername();
                        String channelId = forumChannel.getChannelId();
                        int totalMessages = forumChannel.getTotalMessages();

                        // Store the forumChannel to the userProfileData
                        forumChannelArrayList.add(new ForumChannel(channelName, userId, creatorUserName, channelId, totalMessages));
                    }
                }

                updateForumChannelList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                                Intent mapIntent = new Intent(ForumSubjectChannelActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(ForumSubjectChannelActivity.this);
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

                            androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                        break;
                    case R.id.nav_study_group:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent studyGroupIntent = new Intent(ForumSubjectChannelActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(ForumSubjectChannelActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(ForumSubjectChannelActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(ForumSubjectChannelActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(ForumSubjectChannelActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(ForumSubjectChannelActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Forum Channels</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        userCreatedForumChannelsListView = findViewById(android.R.id.list);
        userCreatedForumChannelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ForumChannel forumChannelSelected = forumChannelArrayList.get(position);

                Intent forumChannelMessageIntent = new Intent(ForumSubjectChannelActivity.this, ForumChannelMessagesActivity.class);
                forumChannelMessageIntent.putExtra("subjectChosen", subjectChosenString);
                forumChannelMessageIntent.putExtra("forumChannel", forumChannelSelected);
                startActivity(forumChannelMessageIntent);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater alertDialogLayoutInfaltor = LayoutInflater.from(ForumSubjectChannelActivity.this);
                View alertDialogView = alertDialogLayoutInfaltor.inflate(R.layout.forum_alert_dialog_view, null);
                final EditText channelNameEditText = alertDialogView.findViewById(R.id.forum_channel_name_edit_text);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForumSubjectChannelActivity.this);
                alertDialogBuilder.setView(alertDialogView);
                alertDialogBuilder.setTitle(R.string.create_forum_channel);
                alertDialogBuilder.setMessage("To create a forum channel, please enter a name in the field below and tap create channel. To cancel forum channel creation, tap cancel.");
                alertDialogBuilder.setPositiveButton("Create", null);
                alertDialogBuilder.setNegativeButton("Cancel", null);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button createChannelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        createChannelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(channelNameEditText.getText().toString().trim().isEmpty()){

                                    ColorStateList editTextColorStateList = ColorStateList.valueOf(ContextCompat.getColor(ForumSubjectChannelActivity.this, android.R.color.holo_red_light));
                                    channelNameEditText.setBackgroundTintList(editTextColorStateList);
                                    channelNameEditText.setHintTextColor(ContextCompat.getColor(ForumSubjectChannelActivity.this, android.R.color.holo_red_light));

                                } else {

                                    final String channelId = UUID.randomUUID().toString();

                                    final ForumChannel newForumChannel = new ForumChannel(channelNameEditText.getText().toString().trim(), user.getUid(), userProfileData.getUsername(), channelId, 0);

                                    DatabaseReference forumChannelCreationReference = mFirebaseDatabase.getReference();
                                    forumChannelCreationReference.child("subjectsForum").child(subjectChosenString).child(channelId).setValue(newForumChannel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Intent forumChannelMessageIntent = new Intent(ForumSubjectChannelActivity.this, ForumChannelMessagesActivity.class);
                                            forumChannelMessageIntent.putExtra("subjectChosen", subjectChosenString);
                                            forumChannelMessageIntent.putExtra("forumChannel", newForumChannel);
                                            startActivity(forumChannelMessageIntent);
                                        }
                                    });
                                }
                            }
                        });

                        Button cancelChannelCreationButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        cancelChannelCreationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });
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
                        Intent profileIntent = new Intent(ForumSubjectChannelActivity.this, UserProfileActivity.class);
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

    private void updateForumChannelList(){

        forumChannelsAdapter = new ForumChannelsAdapter(this, R.layout.forum_channel_row, forumChannelArrayList);
        userCreatedForumChannelsListView.setAdapter(forumChannelsAdapter);
        userCreatedForumChannelsListView.setTextFilterEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem channelSearchMenuItem = menu.findItem(R.id.search);
        SearchView channelSearchView = (SearchView) channelSearchMenuItem.getActionView();

        if (searchManager != null) {
            channelSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        channelSearchView.setSubmitButtonEnabled(true);
        channelSearchView.setOnQueryTextListener(this);


        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        forumChannelsAdapter.getFilter().filter(newText);
        return true;
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
