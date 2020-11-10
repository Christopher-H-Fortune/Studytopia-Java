package com.fullsail.christopherfortune.studytopia.Fragments.ForumSubjectRoomFragment;

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

public class SubjectRoomFragment extends ListFragment {

    public static final String TAG = "SubjectRoomFragment.TAG";

    public SubjectRoomInterface subjectRoomListener;

    public static SubjectRoomFragment newInstance(){
        return new SubjectRoomFragment();
    }

    public interface SubjectRoomInterface {
        void editSentMessage(int messageChosen, View view);
        void passForumRoomViews(ListView forumListView, EditText messageEditText, ImageButton sendButton);
        void sendForumMessage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof SubjectRoomInterface){
            subjectRoomListener = (SubjectRoomInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forum_subject_room, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView forumListView = getView().findViewById(android.R.id.list);

            forumListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    subjectRoomListener.editSentMessage(position, view);
                    return true;
                }
            });

            EditText messageEditText = getView().findViewById(R.id.forum_subject_message_edt_txt);
            ImageButton sendMessageButton  = getView().findViewById(R.id.send_message_button);
            sendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subjectRoomListener.sendForumMessage();
                }
            });

            subjectRoomListener.passForumRoomViews(forumListView, messageEditText, sendMessageButton);
        }
    }
}
