package com.fullsail.christopherfortune.studytopia.Adapters.FlashcardTestListAdapter;

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

public class FlashcardTestListAdapter extends ArrayAdapter<FlashcardTest> {

    private final Context context;
    private final int resource;
    private final ArrayList<FlashcardTest> flashcardTestArrayList;

    public FlashcardTestListAdapter(Context context, int resource, ArrayList<FlashcardTest> flashcardTestArrayList) {
        super(context, resource, flashcardTestArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardTestArrayList = flashcardTestArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View testRowView = layoutInflater.inflate(R.layout.test_row, null);

        TextView testNameTxtVw = testRowView.findViewById(R.id.test_name_test_row_txt_vw);
        TextView flashcardCountTxtVw = testRowView.findViewById(R.id.flashcard_count_test_row_txt_vw);

        testNameTxtVw.setText(flashcardTestArrayList.get(position).getFlashcardTestName());

        String flashcardCountString = Integer.toString(flashcardTestArrayList.get(position).getFlashcardCount());
        flashcardCountTxtVw.setText(flashcardCountString);

        return testRowView;
    }
}
