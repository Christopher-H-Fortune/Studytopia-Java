package com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupAttendeeListFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class StudyGroupAttendeesListFragment extends ListFragment {

    public static final String TAG = "StudyGroupAttendeesListFragment.TAG";

    public StudyGroupAttendeeListInterface studyGroupAttendeeListListener;

    public static StudyGroupAttendeesListFragment newInstance(){
        return new StudyGroupAttendeesListFragment();
    }

    public interface StudyGroupAttendeeListInterface {
        void passListView(ListView attendeeListView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof StudyGroupAttendeeListInterface){
            studyGroupAttendeeListListener = (StudyGroupAttendeeListInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study_group_attendee_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){
            ListView attendeeListView = getView().findViewById(android.R.id.list);
            studyGroupAttendeeListListener.passListView(attendeeListView);
        }
    }
}
