package com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestSubjectListFragment;

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

public class FlashcardTestSubjectListFragment extends ListFragment {

    public static final String TAG = "FlashcardTestSubjectListFragment.TAG";

    public FlashcardTestSubjectListInterface flashcardTestSubjectListListener;

    public static FlashcardTestSubjectListFragment newInstance(){
        return new FlashcardTestSubjectListFragment();
    }

    public interface FlashcardTestSubjectListInterface {
        void passListView(ListView subjectListView);
        void subjectChosen(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestSubjectListInterface){
            flashcardTestSubjectListListener = (FlashcardTestSubjectListInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flashcard_test_subject_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if(getView() != null){

            ListView subjectListView = getView().findViewById(android.R.id.list);
            flashcardTestSubjectListListener.passListView(subjectListView);
            subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    flashcardTestSubjectListListener.subjectChosen(position);
                }
            });
        }
    }
}
