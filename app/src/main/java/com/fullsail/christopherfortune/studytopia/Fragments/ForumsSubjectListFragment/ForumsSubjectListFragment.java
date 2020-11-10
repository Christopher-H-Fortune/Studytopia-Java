package com.fullsail.christopherfortune.studytopia.Fragments.ForumsSubjectListFragment;

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

public class ForumsSubjectListFragment extends Fragment {

    public static final String TAG = "ForumsSubjectListFragment.TAG";

    public ForumsSubjectListInterface forumsSubjectListListener;

    public static ForumsSubjectListFragment newInstance(){
        return new ForumsSubjectListFragment();
    }

    public interface ForumsSubjectListInterface {
        void viewForumRoom(int subjectChosen);
        void passSubjectView(ListView subjectListView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof ForumsSubjectListInterface){
            forumsSubjectListListener = (ForumsSubjectListInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forums_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView subjectListView = getView().findViewById(android.R.id.list);
            subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    forumsSubjectListListener.viewForumRoom(position);
                }
            });

            forumsSubjectListListener.passSubjectView(subjectListView);
        }
    }
}
