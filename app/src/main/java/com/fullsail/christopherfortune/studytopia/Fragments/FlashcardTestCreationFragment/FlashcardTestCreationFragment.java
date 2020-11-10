package com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestCreationFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

public class FlashcardTestCreationFragment extends Fragment {

    public static final String TAG = "FlashcardTestCreationFragment.TAG";

    public FlashcardTestCreationInterface flashcardTestCreationListener;

    public static FlashcardTestCreationFragment newInstance(){
        return new FlashcardTestCreationFragment();
    }

    public interface FlashcardTestCreationInterface {
        void passFlashcardCount(int flashcardCount);
        void continueTestCreation(EditText flashcardTestNameEdtTxt,
                                  RadioButton publicTestRadioButton);
        void passFlashcardCountSeekbar(SeekBar flashcardCountSeekBar, TextView flashcardMaxCountLabel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestCreationInterface){
            flashcardTestCreationListener = (FlashcardTestCreationInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View flashcardTestCreationFragmentView = inflater.inflate(R.layout.fragment_flashcard_test_creation, container, false);

        final EditText flashcardNameEdtTxt = flashcardTestCreationFragmentView.findViewById(R.id.study_group_test_name_edt_txt);
        final TextView flashcardCountLbl = flashcardTestCreationFragmentView.findViewById(R.id.flashcard_count_lbl);
        final SeekBar flashcardCountSeekBar = flashcardTestCreationFragmentView.findViewById(R.id.flashcard_count_seek_bar);
        flashcardCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                flashcardCountLbl.setText(getString(R.string.flashcardCount, progress));
                flashcardTestCreationListener.passFlashcardCount(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final RadioButton publicTestRadioButton = flashcardTestCreationFragmentView.findViewById(R.id.public_radio_btn);


        Button continueButton = flashcardTestCreationFragmentView.findViewById(R.id.contine_flashcard_test_creation_btn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardTestCreationListener.continueTestCreation(flashcardNameEdtTxt, publicTestRadioButton);
            }
        });

        return flashcardTestCreationFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null){

            final SeekBar flashcardCountSeekBar = getView().findViewById(R.id.flashcard_count_seek_bar);

            final TextView maxFlashcardCountLabel = getView().findViewById(R.id.max_search_range_label);

            flashcardTestCreationListener.passFlashcardCountSeekbar(flashcardCountSeekBar, maxFlashcardCountLabel);
        }
    }
}
