package com.fullsail.christopherfortune.studytopia.Fragments.PastStudyGroupDetailsFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.fullsail.christopherfortune.studytopia.R;

public class PastStudyGroupDetailsFragment extends Fragment {

    public static final String TAG = "PastStudyGroupDetailsFragment.TAG";

    public PastStudyGroupDetailsInterface pastStudyGroupDetailsListener;

    public static PastStudyGroupDetailsFragment newInstance(){
        return new PastStudyGroupDetailsFragment();
    }

    public interface PastStudyGroupDetailsInterface{
        void viewStudyGroupFlashcards();
        void doneViewing();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PastStudyGroupDetailsInterface){
            pastStudyGroupDetailsListener = (PastStudyGroupDetailsInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pastStudyGroupDetailsFragmentView = inflater.inflate(R.layout.fragment_past_study_group_details, container, false);

        Button studyGroupFlashcardBtn = pastStudyGroupDetailsFragmentView.findViewById(R.id.past_study_group_flashcard_test_btn);
        studyGroupFlashcardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastStudyGroupDetailsListener.viewStudyGroupFlashcards();
            }
        });

        return pastStudyGroupDetailsFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.done_menu_button){
            pastStudyGroupDetailsListener.doneViewing();
        }

        return true;
    }
}
