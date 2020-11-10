package com.fullsail.christopherfortune.studytopia.AlarmReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.fullsail.christopherfortune.studytopia.DataModels.ForumMessage.ForumMessage;
import com.fullsail.christopherfortune.studytopia.DataModels.TopicOfTheDay.TopicOfTheDay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<TopicOfTheDay> archivedTopicOfTheDayArrayList = new ArrayList<>();
    private ArrayList<TopicOfTheDay> topicOfTheDayArrayList = new ArrayList<>();
    private ArrayList<ForumMessage> forumMessageArrayList = new ArrayList<>();
    private int archivedCount;
    private String archivedCountString;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference archivedTopicsReference = mFirebaseDatabase.getReference();

        archivedTopicsReference.child("TopicOfTheDay").child("archivedTopics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the topicOfTheDay data
                    TopicOfTheDay topicOfTheDay = data.getValue(TopicOfTheDay.class);

                    // Make sure topicOfTheDay isn't null
                    if (topicOfTheDay != null) {

                        String topic = topicOfTheDay.getTopic();
                        boolean isDisplayedYet = topicOfTheDay.isDisplayedYet();
                        int topicNumber = topicOfTheDay.getTopicNumber();

                        archivedTopicOfTheDayArrayList.add(new TopicOfTheDay(topic, isDisplayedYet, topicNumber));
                    }
                }

                getArchivedListCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getArchivedListCount(){

        archivedCount = archivedTopicOfTheDayArrayList.size();

        DatabaseReference topicOfTheDayReference = mFirebaseDatabase.getReference();

        topicOfTheDayReference.child("TopicOfTheDay").child("newTopics").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop through the data snapshot
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    // Get the topicOfTheDay data
                    TopicOfTheDay topicOfTheDay = data.getValue(TopicOfTheDay.class);

                    // Make sure topicOfTheDay isn't null
                    if (topicOfTheDay != null) {

                        String topic = topicOfTheDay.getTopic();
                        boolean isDisplayedYet = topicOfTheDay.isDisplayedYet();
                        int topicNumber = topicOfTheDay.getTopicNumber();

                        topicOfTheDayArrayList.add(new TopicOfTheDay(topic, isDisplayedYet, topicNumber));
                    }
                }

                transferTopicOfTheDay();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void transferTopicOfTheDay(){

        Calendar calendar = Calendar.getInstance();

        int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK);

        for(TopicOfTheDay topicOfTheDayToDisplay: topicOfTheDayArrayList){

            if(topicOfTheDayToDisplay.getTopicNumber() == dayOfTheWeek - 1){

                archivedCountString = Integer.toString(archivedCount + 1);

                topicOfTheDayToDisplay.setTopicNumber(archivedCount + 1);

                final DatabaseReference transferReference = mFirebaseDatabase.getReference();

                transferReference.child("TopicOfTheDay").child("archivedTopics").child(archivedCountString).setValue(topicOfTheDayToDisplay).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        transferReference.child("topicOfTheDayForum").child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                // Clear the array list to prevent duplicates being displayed in the list view
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

                                transferTOTDMessages();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }
    }

    private void transferTOTDMessages(){

        final DatabaseReference transferReference = mFirebaseDatabase.getReference();

        if(forumMessageArrayList.size() > 0){

            for(ForumMessage totdForumMessage: forumMessageArrayList){

                String messageChildString = totdForumMessage.getCreatorId() + " " + totdForumMessage.getDateTimeSent();

                transferReference.child("TopicOfTheDay").child("archivedTopics").child(archivedCountString).child("messages").child(messageChildString).setValue(totdForumMessage);
            }

            transferReference.child("topicOfTheDayForum").child("messages").removeValue();

        }
    }
}
