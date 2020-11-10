package com.fullsail.christopherfortune.studytopia.Fragments.TopicOfTheDayForumFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.fullsail.christopherfortune.studytopia.R;

public class TopicOfTheDayForumFragment extends ListFragment {

    public static final String TAG = "TopicOfTheDayForumFragment.TAG";

    public TopicOfTheDayForumInterface topicOfTheDayForumListener;

    public static TopicOfTheDayForumFragment newInstance(){
        return new TopicOfTheDayForumFragment();
    }

    public interface TopicOfTheDayForumInterface {
        void passViews(ListView forumsListView, EditText messageEditText, ImageButton sendMessageButton, ImageButton saveMessageButton);
        void selectMessage(int messageChosen, View view);
        void sendMessage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof TopicOfTheDayForumInterface){
            topicOfTheDayForumListener = (TopicOfTheDayForumInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_of_day_forum, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView forumListView = getView().findViewById(android.R.id.list);
            forumListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    topicOfTheDayForumListener.selectMessage(position, view);

                    return true;
                }
            });

            EditText messageEditText = getView().findViewById(R.id.forum_subject_message_edt_txt);

            ImageButton sendMessageButton = getView().findViewById(R.id.send_message_button);
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topicOfTheDayForumListener.sendMessage();
                }
            });

            ImageButton saveImageButton = getView().findViewById(R.id.save_message_edit_button);

            topicOfTheDayForumListener.passViews(forumListView, messageEditText, sendMessageButton, saveImageButton);
        }
    }
}
