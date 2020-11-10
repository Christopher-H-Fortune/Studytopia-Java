package com.fullsail.christopherfortune.studytopia.Adapters.FlashcardResultsAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class FlashcardResultsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int resource;
    private final ArrayList<String> flashcardsArrayList;

    public FlashcardResultsAdapter(Context context, int resource, ArrayList<String> flashcardsArrayList){
        super(context, resource, flashcardsArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardsArrayList = flashcardsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View flashcardNumberRowView = layoutInflater.inflate(R.layout.flashcard_results_row, null);

        TextView flashcardNumberTxtVw = flashcardNumberRowView.findViewById(R.id.flashcard_number_results_text_view);

        flashcardNumberTxtVw.setText(flashcardsArrayList.get(position));

        return flashcardNumberRowView;
    }
}
