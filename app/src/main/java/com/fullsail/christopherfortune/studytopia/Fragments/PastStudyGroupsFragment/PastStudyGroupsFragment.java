package com.fullsail.christopherfortune.studytopia.Fragments.PastStudyGroupsFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class PastStudyGroupsFragment extends Fragment {

    public static final String TAG = "PastStudyGroupsFragment.TAG";

    public PastStudyGroupsInterface pastStudyGroupsListener;

    public static PastStudyGroupsFragment newInstance(){
        return new PastStudyGroupsFragment();
    }

    public interface PastStudyGroupsInterface{
        void passPastStudyGroupsListView(ListView pastStudyGroupsListView);
        void viewStudyGroupChosen(int studyGroupChosen);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PastStudyGroupsInterface){
            pastStudyGroupsListener = (PastStudyGroupsInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pastStudyGroupFragmentView = inflater.inflate(R.layout.fragment_past_study_groups, container, false);

        ListView pastStudyGroupsListView = pastStudyGroupFragmentView.findViewById(android.R.id.list);
        pastStudyGroupsListener.passPastStudyGroupsListView(pastStudyGroupsListView);
        pastStudyGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pastStudyGroupsListener.viewStudyGroupChosen(position);
           }
        });

        return pastStudyGroupFragmentView;
    }
}
