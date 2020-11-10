package com.fullsail.christopherfortune.studytopia.Fragments.CreateStudyGroupFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

public class CreateStudyGroupFragment extends Fragment {

    public static final String TAG = "CreateStudyGroupFragment.TAG";

    public CreateStudyGroupInterface createStudyGroupListener;

    public static CreateStudyGroupFragment newInstance(){
        return new CreateStudyGroupFragment();
    }

    public interface CreateStudyGroupInterface {
        void passViews(EditText studyGroupNameEdtTxt,
                       EditText studyGroupSubjectEdtTxt,
                       EditText studyGroupAddressEdtTxt,
                       EditText studyGroupCityEdtTxt,
                       EditText studyGroupStateEdtTxt,
                       EditText studyGroupDateEdtTxt,
                       EditText studyGroupTimeEdtTxt);
        void pickDate();
        void pickTime();
        void passAttendeeCount(int attendeeCount);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof CreateStudyGroupInterface){
            createStudyGroupListener = (CreateStudyGroupInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_study_group, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if(getView() != null){

            EditText studyGroupNameEdtTxt = getView().findViewById(R.id.study_group_name_edt_txt);
            EditText studyGroupSubjectEdtTxt = getView().findViewById(R.id.study_group_subject_edt_txt);
            EditText studyGroupAddressEdtTxt = getView().findViewById(R.id.study_group_address_edt_txt);
            EditText studyGroupCityEdtTxt = getView().findViewById(R.id.study_group_city_edt_txt);
            EditText studyGroupStateEdtTxt = getView().findViewById(R.id.study_group_state_edt_txt);
            EditText studyGroupDateEdtTxt = getView().findViewById(R.id.study_group_date_edt_txt);
            EditText studyGroupTimeEdtTxt = getView().findViewById(R.id.study_group_time_edt_txt);

            studyGroupDateEdtTxt.setInputType(InputType.TYPE_NULL);
            studyGroupTimeEdtTxt.setInputType(InputType.TYPE_NULL);

            final TextView attendeesLabel = getView().findViewById(R.id.attendees_lbl);
            createStudyGroupListener.passViews(studyGroupNameEdtTxt,
                    studyGroupSubjectEdtTxt,
                    studyGroupAddressEdtTxt,
                    studyGroupCityEdtTxt,
                    studyGroupStateEdtTxt,
                    studyGroupDateEdtTxt,
                    studyGroupTimeEdtTxt);

            Button pickDateButton = getView().findViewById(R.id.pick_date_button);
            pickDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createStudyGroupListener.pickDate();
                }
            });
            Button pickTimeButton = getView().findViewById(R.id.pick_time_button);
            pickTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createStudyGroupListener.pickTime();
                }
            });
            SeekBar attendeeCountSeekBar = getView().findViewById(R.id.attendee_count_seek_bar);
            attendeeCountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    attendeesLabel.setText(getResources().getString(R.string.max_attendee_label, progress));
                    createStudyGroupListener.passAttendeeCount(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_menu, menu);
    }

}
