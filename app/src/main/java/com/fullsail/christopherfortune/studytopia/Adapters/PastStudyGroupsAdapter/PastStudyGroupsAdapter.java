package com.fullsail.christopherfortune.studytopia.Adapters.PastStudyGroupsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.StudyGroup.StudyGroup;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class PastStudyGroupsAdapter extends ArrayAdapter<StudyGroup> {

    private final Context context;
    private final int resource;
    private final ArrayList<StudyGroup> studyGroupsArrayList;

    public PastStudyGroupsAdapter(Context context, int resource, ArrayList<StudyGroup> studyGroupsArrayList){
        super(context, resource, studyGroupsArrayList);
        this.context = context;
        this.resource = resource;
        this.studyGroupsArrayList = studyGroupsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View pastStudyGroupRowView = layoutInflater.inflate(R.layout.past_study_groups_row, null);

        TextView pastStudyGroupNameTxtVw = pastStudyGroupRowView.findViewById(R.id.past_study_group_name_txt_vw);
        TextView pastStudyGroupAttendeeCountTxtVw = pastStudyGroupRowView.findViewById(R.id.past_study_group_count_txt_vw);
        TextView pastStudyGroupCreatorTxtVw = pastStudyGroupRowView.findViewById(R.id.past_study_group_creator);

        pastStudyGroupNameTxtVw.setText(studyGroupsArrayList.get(position).getStudyGroupName());
        pastStudyGroupAttendeeCountTxtVw.setText(studyGroupsArrayList.get(position).getStudyGroupAttendeeCount() + " People");
        pastStudyGroupCreatorTxtVw.setText("Created By:\n" + studyGroupsArrayList.get(position).getCreatorUsername());

        return pastStudyGroupRowView;
    }
}
