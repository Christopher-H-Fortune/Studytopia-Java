package com.fullsail.christopherfortune.studytopia.Adapters.PublicFlashcardTestsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.PublicFlashcardTests.PublicFlashcardTests;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class PublicFlashcardTestAdapter extends ArrayAdapter<PublicFlashcardTests> {

    private final Context context;
    private final int resource;
    private final ArrayList<PublicFlashcardTests> flashcardsArrayList;

    public PublicFlashcardTestAdapter(Context context, int resource, ArrayList<PublicFlashcardTests> flashcardsArrayList){
        super(context, resource, flashcardsArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardsArrayList = flashcardsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View questionRowView = layoutInflater.inflate(R.layout.public_flashcard_row, null);

        TextView testNameTxtVw = questionRowView.findViewById(R.id.test_name_public_txt_vw);
        TextView flashcardCountTxtVw = questionRowView.findViewById(R.id.flashcard_count_public_txt_vw);
        TextView flashcardCreatorTxtVw = questionRowView.findViewById(R.id.creator_public_txt_vw);

        testNameTxtVw.setText(flashcardsArrayList.get(position).getFlashcardTestName());

        String flashcardCountString = (flashcardsArrayList.get(position).getFlashcardCount()) + " Cards";
        flashcardCountTxtVw.setText(flashcardCountString);

        String creatorString = "Creator:\n" + flashcardsArrayList.get(position).getFlashcardTestCreator();
        flashcardCreatorTxtVw.setText(creatorString);

        return questionRowView;
    }
}
