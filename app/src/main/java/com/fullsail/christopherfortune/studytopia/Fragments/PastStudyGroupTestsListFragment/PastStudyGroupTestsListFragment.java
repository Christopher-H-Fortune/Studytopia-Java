package com.fullsail.christopherfortune.studytopia.Fragments.PastStudyGroupTestsListFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.fullsail.christopherfortune.studytopia.R;

public class PastStudyGroupTestsListFragment extends ListFragment {

    public static final String TAG = "PastStudyGroupTestsListFragment.TAG";

    public PastStudyGroupTestListInterface pastStudyGroupTestListListener;

    public static PastStudyGroupTestsListFragment newInstance(){
        return new PastStudyGroupTestsListFragment();
    }

    public interface PastStudyGroupTestListInterface{
        void passTestListView(ListView testListView);
        void pastStudyGroupTestSelected(int testSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PastStudyGroupTestListInterface){
            pastStudyGroupTestListListener = (PastStudyGroupTestListInterface)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_study_group_test_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView pastStudyGroupTestListView = getView().findViewById(android.R.id.list);
            pastStudyGroupTestListListener.passTestListView(pastStudyGroupTestListView);
            pastStudyGroupTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pastStudyGroupTestListListener.pastStudyGroupTestSelected(position);
                }
            });
        }
    }
}
