package com.fullsail.christopherfortune.studytopia.Adapters.TopicOfTheDayForumAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.ForumMessage.ForumMessage;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class TopicOfTheDayForumAdapter extends ArrayAdapter<ForumMessage> {

    private final Context context;
    private final int resource;
    private final ArrayList<ForumMessage> forumMessageArrayList;

    public TopicOfTheDayForumAdapter(Context context, int resource, ArrayList<ForumMessage> forumMessageArrayList){
        super(context, resource, forumMessageArrayList);
        this.context = context;
        this.resource = resource;
        this.forumMessageArrayList = forumMessageArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View messageRowView = layoutInflater.inflate(R.layout.forum_message_row, null);

        if(position % 2 == 1){
            messageRowView.setBackgroundColor(ContextCompat.getColor(context, R.color.forum_row));
        } else {
            messageRowView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }

        TextView messageTextView = messageRowView.findViewById(R.id.forum_message_text_view);
        TextView dateSentTextView = messageRowView.findViewById(R.id.time_message_sent_text);
        TextView usernameOfSenderTextView = messageRowView.findViewById(R.id.forum_message_username_text_view);

        messageTextView.setText(forumMessageArrayList.get(position).getMessageEntered());
        dateSentTextView.setText(forumMessageArrayList.get(position).getTimeSent());
        usernameOfSenderTextView.setText(forumMessageArrayList.get(position).getCreatorUsername());

        return  messageRowView;
    }
}
