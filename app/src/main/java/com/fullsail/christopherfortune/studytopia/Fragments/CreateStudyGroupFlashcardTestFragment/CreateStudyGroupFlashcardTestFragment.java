package com.fullsail.christopherfortune.studytopia.Fragments.CreateStudyGroupFlashcardTestFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

public class CreateStudyGroupFlashcardTestFragment extends Fragment {

    public static final String TAG = "CreateStudyGroupFlashcardTestFragment.TAG";

    public CreateStudyGroupFlashcardTestInterface createStudyGroupFlashcardTestListener;

    public static CreateStudyGroupFlashcardTestFragment newInstance(){
        return new CreateStudyGroupFlashcardTestFragment();
    }

    public interface CreateStudyGroupFlashcardTestInterface {
        void continueToQuestionList(EditText flashcardTestNameEdtTxt);
        void passQuestionCount(int questionCount);
        void passSeekBarAndLabel(SeekBar flashcardCountSeekBar, TextView flashcardCountMaxLabel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof CreateStudyGroupFlashcardTestInterface){
            createStudyGroupFlashcardTestListener = (CreateStudyGroupFlashcardTestInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View createStudyGroupFlashcardTestView = inflater.inflate(R.layout.fragment_study_group_flashcard_test_creation, container, false);

        final EditText flashcardNameEdtTxt = createStudyGroupFlashcardTestView.findViewById(R.id.study_group_test_name_edt_txt);
        final TextView flashcardCountLbl = createStudyGroupFlashcardTestView.findViewById(R.id.flashcard_count_lbl);
        final SeekBar flashcardCountSeekBar = createStudyGroupFlashcardTestView.findViewById(R.id.flashcard_count_seek_bar);
        flashcardCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 flashcardCountLbl.setText(getString(R.string.flashcardCount, progress));
                 createStudyGroupFlashcardTestListener.passQuestionCount(progress);
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {

             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {

             }
         });

        Button continueCreatingTestButton = createStudyGroupFlashcardTestView.findViewById(R.id.contine_flashcard_test_creation_btn);
        continueCreatingTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStudyGroupFlashcardTestListener.continueToQuestionList(flashcardNameEdtTxt);
            }
        });

        return createStudyGroupFlashcardTestView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            final SeekBar flashcardCountSeekBar = getView().findViewById(R.id.flashcard_count_seek_bar);
            final TextView flashcardCountMaxLabel = getView().findViewById(R.id.max_search_range_label);
            createStudyGroupFlashcardTestListener.passSeekBarAndLabel(flashcardCountSeekBar, flashcardCountMaxLabel);
        }
    }
}
