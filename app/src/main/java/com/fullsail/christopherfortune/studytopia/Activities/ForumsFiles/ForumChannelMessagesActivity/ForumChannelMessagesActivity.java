package com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumChannelMessagesActivity;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.Activities.FlashcardTestFiles.FlashcardTestSubjectListActivity.FlashcardTestSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.ForumsSubjectListActivity.ForumsSubjectListActivity;
import com.fullsail.christopherfortune.studytopia.Activities.ForumsFiles.TopicOfTheDayActivity.TopicOfTheDayActivity;
import com.fullsail.christopherfortune.studytopia.Activities.LoginActivity.LoginSignupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.PublicFlashcardsTestsFiles.PublicFlashcardsActivity.PublicFlashcardsActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupActivity.StudyGroupActivity;
import com.fullsail.christopherfortune.studytopia.Activities.StudyGroupFiles.StudyGroupMapActivity.StudyGroupMapActivity;
import com.fullsail.christopherfortune.studytopia.Activities.UserProfileActivity.UserProfileActivity;
import com.fullsail.christopherfortune.studytopia.Adapters.TopicOfTheDayForumAdapter.TopicOfTheDayForumAdapter;
import com.fullsail.christopherfortune.studytopia.DataModels.ForumChannel.ForumChannel;
import com.fullsail.christopherfortune.studytopia.DataModels.ForumMessage.ForumMessage;
import com.fullsail.christopherfortune.studytopia.DataModels.User.UserData;
import com.fullsail.christopherfortune.studytopia.Fragments.ForumSubjectRoomFragment.SubjectRoomFragment;
import com.fullsail.christopherfortune.studytopia.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.image.SmartImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class ForumChannelMessagesActivity extends AppCompatActivity implements SubjectRoomFragment.SubjectRoomInterface {

    private DrawerLayout drawerLayout;
    public ActionBar actionbar;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseDatabase;
    private UserData userProfileData = new UserData();
    private EditText messageEditText;
    private ImageButton sendButton;
    private ListView forumListView;
    private ArrayList<ForumMessage> forumMessageArrayList = new ArrayList<>();
    private String subjectChosenString;
    private ForumChannel forumChannelSelected;
    private ForumChannel forumChannelData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_channel_messages);

        Intent startingIntent = getIntent();

        if(startingIntent.hasExtra("subjectChosen") && startingIntent.hasExtra("forumChannel")){
            subjectChosenString = startingIntent.getStringExtra("subjectChosen");
            forumChannelSelected = (ForumChannel) startingIntent.getSerializableExtra("forumChannel");
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

        DatabaseReference forumChannelDatabaseReference = mFirebaseDatabase.getReference();

        forumChannelDatabaseReference.child("subjectsForum").child(subjectChosenString).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the ForumMessage data
                    ForumChannel forumChannel = data.getValue(ForumChannel.class);

                    // Make sure forumMessageData isn't null
                    if (forumChannel != null) {

                        // Get the forum message data to create a ForumMessage object
                        String channelId = forumChannel.getChannelId();
                        int totalMessages = forumChannel.getTotalMessages();
                        String channelName = forumChannel.getChannelName();
                        String creatorUsername = forumChannel.getCreatorUsername();
                        String creatorId = forumChannel.getCreatorId();

                        if(channelId.equals(forumChannelSelected.getChannelId())){
                            // Store the userData to the userProfileData
                            forumChannelData = new ForumChannel(channelName, creatorId, creatorUsername, channelId, totalMessages);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference forumRoomDatabaseReference = mFirebaseDatabase.getReference();

        forumRoomDatabaseReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                forumMessageArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the ForumMessage data
                    ForumMessage forumMessageData = data.getValue(ForumMessage.class);

                    // Make sure forumMessageData isn't null
                    if (forumMessageData != null) {

                        // Get the forum message data to create a ForumMessage object
                        String forumMessage = forumMessageData.getMessageEntered();
                        String timeSent = forumMessageData.getTimeSent();
                        String dateSent = forumMessageData.getDateSent();
                        String creatorUsername = forumMessageData.getCreatorUsername();
                        String creatorId = forumMessageData.getCreatorId();
                        String dateTimeSent = forumMessageData.getDateTimeSent();

                        // Store the userData to the userProfileData
                        forumMessageArrayList.add(new ForumMessage(forumMessage, timeSent, dateSent, creatorUsername, creatorId, dateTimeSent));
                    }
                }

                updateForumList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        forumRoomDatabaseReference.keepSynced(true);

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
                                Intent mapIntent = new Intent(ForumChannelMessagesActivity.this, StudyGroupMapActivity.class);
                                startActivity(mapIntent);
                            }

                        } else {

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForumChannelMessagesActivity.this);
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
                        Intent studyGroupIntent = new Intent(ForumChannelMessagesActivity.this, StudyGroupActivity.class);
                        startActivity(studyGroupIntent);
                        break;
                    case R.id.nav_topic_of_the_day:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent topicOfDayIntent = new Intent(ForumChannelMessagesActivity.this, TopicOfTheDayActivity.class);
                        startActivity(topicOfDayIntent);
                        break;
                    case R.id.nav_forums:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent forumsSubjectListIntent = new Intent(ForumChannelMessagesActivity.this, ForumsSubjectListActivity.class);
                        startActivity(forumsSubjectListIntent);
                        break;
                    case R.id.nav_public_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent publicFlashcardsIntent = new Intent(ForumChannelMessagesActivity.this, PublicFlashcardsActivity.class);
                        startActivity(publicFlashcardsIntent);
                        break;
                    case R.id.nav_flashcards:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent flashcardsListIntent = new Intent(ForumChannelMessagesActivity.this, FlashcardTestSubjectListActivity.class);
                        startActivity(flashcardsListIntent);
                        break;
                    case R.id.nav_logout:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent logoutIntent = new Intent(ForumChannelMessagesActivity.this, LoginSignupActivity.class);
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
            actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>" + forumChannelSelected.getChannelName() + "</font>", Html.FROM_HTML_MODE_LEGACY));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.forum_channel_messages_frame, SubjectRoomFragment.newInstance(), SubjectRoomFragment.TAG).commit();
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
                            Intent profileIntent = new Intent(ForumChannelMessagesActivity.this, UserProfileActivity.class);
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
    public void editSentMessage(int messageChosen, View view) {

        final int messageChosenInt = messageChosen;

        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit_forum_message:

                        sendButton.setImageResource(R.drawable.ic_save_black_24dp);
                        messageEditText.setText(forumMessageArrayList.get(messageChosenInt).getMessageEntered());
                        sendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(messageEditText.getText().toString().trim().isEmpty()){

                                    // Ask the user to enter a message to save the edit to the message
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForumChannelMessagesActivity.this);
                                    alertDialogBuilder.setTitle("Please Enter a Message");
                                    alertDialogBuilder.setMessage("To edit your forum message, please enter a message to save.");
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

                                    // If the user has a valid message to save the edits to the database
                                } else {

                                    DatabaseReference editMessageReference = mFirebaseDatabase.getReference();

                                    editMessageReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("messages").child(user.getUid() + " " + forumMessageArrayList.get(messageChosenInt).getDateSent() + " " + forumMessageArrayList.get(messageChosenInt).getTimeSent()).child("messageEntered").setValue(messageEditText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            messageEditText.setText(null);
                                            sendButton.setImageResource(R.drawable.ic_arrow_forward_black_24dp);
                                            Toast.makeText(ForumChannelMessagesActivity.this, "Edit Saved", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case R.id.delete_forum_message:

                        // Ask the user to enter a message to save the edit to the message
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ForumChannelMessagesActivity.this);
                        alertDialogBuilder.setTitle("Are You Sure?");
                        alertDialogBuilder.setMessage("If you want to delete your message, press delete. If you want to cancel press cancel.");
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setPositiveButton(
                                "Delete",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        DatabaseReference deleteMessageReference = mFirebaseDatabase.getReference();

                                        deleteMessageReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("messages").child(user.getUid() + " " + forumMessageArrayList.get(messageChosenInt).getDateSent() + " " + forumMessageArrayList.get(messageChosenInt).getTimeSent()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ForumChannelMessagesActivity.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        int forumChannelMessageCount = forumChannelData.getTotalMessages() - 1;

                                        deleteMessageReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("totalMessages").setValue(forumChannelMessageCount);
                                    }
                                });
                        alertDialogBuilder.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        break;
                }

                return true;
            }
        });
        popupMenu.inflate(R.menu.forum_message_menu);

        if(forumMessageArrayList.get(messageChosen).getCreatorUsername().equals(userProfileData.getUsername())){
            popupMenu.show();
        }
    }

    @Override
    public void passForumRoomViews(ListView forumListView, EditText messageEditText, ImageButton sendButton) {
        this.messageEditText = messageEditText;
        this.forumListView = forumListView;
        this.sendButton = sendButton;

        DatabaseReference forumRoomDatabaseReference = mFirebaseDatabase.getReference();

        forumRoomDatabaseReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                forumMessageArrayList.clear();

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the ForumMessage data
                    ForumMessage forumMessageData = data.getValue(ForumMessage.class);

                    // Make sure forumMessageData isn't null
                    if (forumMessageData != null) {

                        // Get the forum message data to create a ForumMessage object
                        String forumMessage = forumMessageData.getMessageEntered();
                        String timeSent = forumMessageData.getTimeSent();
                        String dateSent = forumMessageData.getDateSent();
                        String creatorUsername = forumMessageData.getCreatorUsername();
                        String creatorId = forumMessageData.getCreatorId();
                        String dateTimeSent = forumMessageData.getDateTimeSent();

                        // Store the userData to the userProfileData
                        forumMessageArrayList.add(new ForumMessage(forumMessage, timeSent, dateSent, creatorUsername, creatorId, dateTimeSent));
                    }
                }

                updateForumList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        forumRoomDatabaseReference.keepSynced(true);
    }

    @Override
    public void sendForumMessage() {

        String messageEntered = messageEditText.getText().toString().trim();

        if(messageEntered.isEmpty()){

            // Ask the user to enter a message to send in the forum
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Please Enter a message to send");
            alertDialogBuilder.setMessage("To send a message in the forum, please enter a message.");
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

            // If the user entered a message to send in the forum room
        } else {

            // Calendar object to get the current time
            Calendar calendar = Calendar.getInstance();

            // Get the current time and save it as a date object to format the time
            Date currentDate = calendar.getTime();

            // Format the current time to the HH:mm:ss format
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);

            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

            // Save the currentDate object formatted with the timeFormat to the format declared above
            String timeString = timeFormat.format(currentDate);

            String dateString = dateFormat.format(currentDate);

            String dateTimeString = dateString + " " + timeString;

            // Create forumMessage object to store to the database
            ForumMessage forumMessage = new ForumMessage(messageEntered, timeString, dateString, userProfileData.getUsername(), user.getUid(), dateTimeString);

            // get a reference to the firebase database
            DatabaseReference messageReference = mFirebaseDatabase.getReference();

            int forumChannelMessageCount = forumChannelData.getTotalMessages() + 1;

            messageReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("totalMessages").setValue(forumChannelMessageCount);

            // Store the forumMessage created above to save to the database
            messageReference.child("subjectsForum").child(subjectChosenString).child(forumChannelSelected.getChannelId()).child("messages").child(user.getUid() + " " + dateTimeString).setValue(forumMessage);

            messageEditText.setText(null);
        }
    }

    private void updateForumList(){

        // Sort the array list by the date sent to make it easier for the user to follow the conversation in the forum
        Collections.sort(forumMessageArrayList, new Comparator<ForumMessage>() {
            @Override
            public int compare(ForumMessage o1, ForumMessage o2) {
                return o2.getDateTimeSent().compareTo(o1.getDateTimeSent());
            }
        });

        TopicOfTheDayForumAdapter topicOfTheDayForumAdapter = new TopicOfTheDayForumAdapter(this, R.layout.forum_message_row, forumMessageArrayList);
        forumListView.setAdapter(topicOfTheDayForumAdapter);
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
