package com.fullsail.christopherfortune.studytopia.Adapters.ArchivedTopicOfDayAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.TopicOfTheDay.TopicOfTheDay;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class ArchivedTopicOfDayAdapter extends ArrayAdapter<TopicOfTheDay> {

    private final Context context;
    private final int resource;
    private final ArrayList<TopicOfTheDay> topicOfTheDayArrayList;

    public ArchivedTopicOfDayAdapter(Context context, int resource, ArrayList<TopicOfTheDay> topicOfTheDayArrayList) {
        super(context, resource, topicOfTheDayArrayList);
        this.context = context;
        this.resource = resource;
        this.topicOfTheDayArrayList = topicOfTheDayArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View archivedTopicOfDayRowView = layoutInflater.inflate(R.layout.archived_topic_of_day_row, null);

        TextView archivedTopicOfDayTxtVw = archivedTopicOfDayRowView.findViewById(R.id.archived_topic_of_day_txt_vw);
        archivedTopicOfDayTxtVw.setText(topicOfTheDayArrayList.get(position).getTopic());

        return archivedTopicOfDayRowView;
    }
}
