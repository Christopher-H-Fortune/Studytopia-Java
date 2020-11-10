package com.fullsail.christopherfortune.studytopia.Adapters.PastFlashcardTestsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.FlashcardTest.FlashcardTest;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class PastFlashcardTestsAdapter extends ArrayAdapter<FlashcardTest> {

    private final Context context;
    private final int resource;
    private final ArrayList<FlashcardTest> flashcardsArrayList;

    public PastFlashcardTestsAdapter(Context context, int resource, ArrayList<FlashcardTest> flashcardsArrayList){
        super(context, resource, flashcardsArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardsArrayList = flashcardsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View pastTestRowView = layoutInflater.inflate(R.layout.past_tests_taken_row, null);

        TextView pastTestNameTxtVw = pastTestRowView.findViewById(R.id.past_test_taken_row_name_txt_vw);
        TextView pastTestCardCountTxtVw = pastTestRowView.findViewById(R.id.test_taken_card_count_row_txt_vw);

        pastTestNameTxtVw.setText(flashcardsArrayList.get(position).getFlashcardTestName());
        pastTestCardCountTxtVw.setText(flashcardsArrayList.get(position).getFlashcardCount() + " Cards");

        return pastTestRowView;
    }
}
