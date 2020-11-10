package com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

public class FlashcardTestFragment extends Fragment {

    public static final String TAG = "FlashcardTestFragment.TAG";

    public FlashcardTestInterface flashcardTestListener;

    public static FlashcardTestFragment newInstance(){
        return new FlashcardTestFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestInterface){
            flashcardTestListener = (FlashcardTestInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View flashcardTestCreationFragmentView = inflater.inflate(R.layout.fragment_flashcard_test, container, false);

        final TextView questionTextView = flashcardTestCreationFragmentView.findViewById(R.id.test_question_txt_vw);
        final TextView questionNumberTextView = flashcardTestCreationFragmentView.findViewById(R.id.test_question_number_txt_vw);

        final EditText questionAnswerEditText = flashcardTestCreationFragmentView.findViewById(R.id.test_answer_edt_txt);

        final Button checkAnswerButton = flashcardTestCreationFragmentView.findViewById(R.id.check_answer_button);
        final Button skipAnswerButton = flashcardTestCreationFragmentView.findViewById(R.id.skip_question_button);

        flashcardTestListener.passFlashcardTestView(questionTextView, questionNumberTextView, questionAnswerEditText, checkAnswerButton, skipAnswerButton);

        return flashcardTestCreationFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            Chronometer countUpChronometer = getView().findViewById(R.id.count_up_chronometer);

            flashcardTestListener.startChronometer(countUpChronometer);
        }
    }

    public interface FlashcardTestInterface {
        void passFlashcardTestView(TextView questionTextView, TextView questionNumberTextView, EditText questionAnswerEditText, Button checkAnswerButton, Button skipButton);
        void startChronometer(Chronometer countUpChronometer);
    }
}
