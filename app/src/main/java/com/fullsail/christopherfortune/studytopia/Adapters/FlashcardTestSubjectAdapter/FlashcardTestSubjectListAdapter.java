package com.fullsail.christopherfortune.studytopia.Adapters.FlashcardTestSubjectAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.Subjects.Subjects;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class FlashcardTestSubjectListAdapter extends ArrayAdapter<Subjects> {

    private final Context context;
    private final int resource;
    private final ArrayList<Subjects> subjectsArrayList;

    public FlashcardTestSubjectListAdapter(Context context, int resource, ArrayList<Subjects> subjectsArrayList) {
        super(context, resource, subjectsArrayList);
        this.context = context;
        this.resource = resource;
        this.subjectsArrayList = subjectsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View subjectsRowView = layoutInflater.inflate(R.layout.subject_row, null);

        TextView usernameTxtVw = subjectsRowView.findViewById(R.id.subject_row_txt_view);
        usernameTxtVw.setText(subjectsArrayList.get(position).getSubjectString());

        ImageView subjectIconImageView = subjectsRowView.findViewById(R.id.subject_icon_image_view);
        subjectIconImageView.setImageResource(subjectsArrayList.get(position).getSubjectIcon());

        return subjectsRowView;
    }
}
