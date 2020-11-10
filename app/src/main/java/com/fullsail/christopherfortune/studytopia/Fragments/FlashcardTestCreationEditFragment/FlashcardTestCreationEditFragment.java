package com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationEditFragment;

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
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

public class FlashcardTestCreationEditFragment extends Fragment {

    public static final String TAG = "FlashcardTestCreationEditFragment.TAG";

    public FlashcardTestCreationEditInterface flashcardTestCreationEditListener;

    public static FlashcardTestCreationEditFragment newInstance(){
        return new FlashcardTestCreationEditFragment();
    }

    public interface FlashcardTestCreationEditInterface {
        void viewQuestionsList();
        void nextQuestion();
        void previousQuestion();
        void passTextView(TextView questionNumberTextView, TextView testNameTextView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestCreationEditInterface){
            flashcardTestCreationEditListener = (FlashcardTestCreationEditInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View flashcardTestCreationEditFragmentView = inflater.inflate(R.layout.fragment_test_creation_edit, container, false);

        Button questionsListButton = flashcardTestCreationEditFragmentView.findViewById(R.id.view_question_list_button);
        questionsListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardTestCreationEditListener.viewQuestionsList();
            }
        });

        Button nextQuestionButton = flashcardTestCreationEditFragmentView.findViewById(R.id.next_question_edit_button);
        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardTestCreationEditListener.nextQuestion();
            }
        });

        Button previousQuestionButton = flashcardTestCreationEditFragmentView.findViewById(R.id.previous_question_edit_button);
        previousQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardTestCreationEditListener.previousQuestion();
            }
        });

        return flashcardTestCreationEditFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if(getView() != null){

            TextView questionNumberTxtVw = getView().findViewById(R.id.new_question_number_txt_vw);
            TextView testNameTextVw = getView().findViewById(R.id.flashcard_test_name_txt_vw);

            flashcardTestCreationEditListener.passTextView(questionNumberTxtVw, testNameTextVw);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_menu, menu);
    }
}
