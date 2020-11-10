package com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupFlashcardEditFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fullsail.christopherfortune.studytopia.R;

public class StudyGroupFlashcardEditFragment extends Fragment {

    public static final String TAG = "StudyGroupFlashcardEditFragment.TAG";

    public StudyGroupFlashcardEditInterface studyGroupFlashcardEditListener;

    public static StudyGroupFlashcardEditFragment newInstance(){
        return new StudyGroupFlashcardEditFragment();
    }

    public interface StudyGroupFlashcardEditInterface {
        void nextQuestion();
        void previousQuestion();
        void viewQuestionList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof StudyGroupFlashcardEditInterface){
            studyGroupFlashcardEditListener = (StudyGroupFlashcardEditInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View studyGroupFlashcardEditFragmentView = inflater.inflate(R.layout.fragment_study_group_flashcard_edit, container, false);

        Button previousFlashcardButton = studyGroupFlashcardEditFragmentView.findViewById(R.id.previous_question_button);
        previousFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyGroupFlashcardEditListener.previousQuestion();
            }
        });

        Button nextFlashcardButton = studyGroupFlashcardEditFragmentView.findViewById(R.id.next_question_btn);
        nextFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyGroupFlashcardEditListener.nextQuestion();
            }
        });

        Button viewQuestionsListButton = studyGroupFlashcardEditFragmentView.findViewById(R.id.view_question_list_button);
        viewQuestionsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyGroupFlashcardEditListener.viewQuestionList();
            }
        });

        return studyGroupFlashcardEditFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_menu, menu);
    }
}
