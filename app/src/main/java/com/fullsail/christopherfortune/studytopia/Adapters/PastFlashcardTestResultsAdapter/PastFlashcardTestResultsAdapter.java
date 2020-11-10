package com.fullsail.christopherfortune.studytopia.Adapters.PastFlashcardTestResultsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;
import com.fullsail.christopherfortune.studytopia.DataModels.TestResults.TestResults;

import java.util.ArrayList;

public class PastFlashcardTestResultsAdapter extends ArrayAdapter<TestResults> {

    private final Context context;
    private final int resource;
    private final ArrayList<TestResults> flashcardsArrayList;

    public PastFlashcardTestResultsAdapter(Context context, int resource, ArrayList<TestResults> flashcardsArrayList){
        super(context, resource, flashcardsArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardsArrayList = flashcardsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View pastTestRowView = layoutInflater.inflate(R.layout.past_test_selected_results_row, null);

        TextView pastTestDateTxtVw = pastTestRowView.findViewById(R.id.test_taken_date_txt_vw);
        TextView pastTestTimeTxtVw = pastTestRowView.findViewById(R.id.test_taken_time_txt_vw);

        if(flashcardsArrayList != null){
            pastTestDateTxtVw.setText(flashcardsArrayList.get(position).getTestName());
            pastTestTimeTxtVw.setText(flashcardsArrayList.get(position).getTimeTaken());
        }

        return pastTestRowView;
    }

}
