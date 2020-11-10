package com.fullsail.christopherfortune.studytopia.Fragments.TopicOfTheDayFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class TopicOfTheDayFragment extends Fragment {

    public static final String TAG = "TopicOfTheDayFragment.TAG";

    public TopicOfTheDayInterface topicOfTheDayListener;

    public static TopicOfTheDayFragment newInstance(){
        return new TopicOfTheDayFragment();
    }

    public interface TopicOfTheDayInterface {
        void passPastTopicsListView(ListView pastTopicsListView);
        void pastTopicSelected(int topicSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof TopicOfTheDayInterface){
            topicOfTheDayListener = (TopicOfTheDayInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_of_the_day, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){
            ListView pastTopicsListView = getView().findViewById(android.R.id.list);
            topicOfTheDayListener.passPastTopicsListView(pastTopicsListView);

            pastTopicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    topicOfTheDayListener.pastTopicSelected(position);
                }
            });
        }
    }
}
