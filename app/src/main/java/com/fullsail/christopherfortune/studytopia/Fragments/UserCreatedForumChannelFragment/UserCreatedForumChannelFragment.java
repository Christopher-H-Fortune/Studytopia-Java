package com.fullsail.christopherfortune.studytopia.Fragments.UserCreatedForumChannelFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class UserCreatedForumChannelFragment extends Fragment {
    public static final String TAG = "UserCreatedForumChannelFragment.TAG";

    public UserCreatedForumChannelInterface userCreatedForumChannelListener;

    public static UserCreatedForumChannelFragment newInstance(){
        return new UserCreatedForumChannelFragment();
    }

    public interface UserCreatedForumChannelInterface {
        void passForumChannelListView(ListView userCreatedChannelsListView);
        void channelSelected(int channelSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof UserCreatedForumChannelInterface){
            userCreatedForumChannelListener = (UserCreatedForumChannelInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_created_forum_channels, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setHasOptionsMenu(true);

        if(getView() != null){

            ListView userCreatedChannelsListView = getView().findViewById(android.R.id.list);
            userCreatedForumChannelListener.passForumChannelListView(userCreatedChannelsListView);
            userCreatedChannelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    userCreatedForumChannelListener.channelSelected(position);
                }
            });
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.channel_search_menu, menu);
//
//    }
}
