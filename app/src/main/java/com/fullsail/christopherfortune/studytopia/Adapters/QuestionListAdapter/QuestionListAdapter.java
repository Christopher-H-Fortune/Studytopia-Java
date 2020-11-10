package com.fullsail.christopherfortune.studytopia.Adapters.QuestionListAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fullsail.christopherfortune.studytopia.DataModels.Flashcards.Flashcards;
import com.fullsail.christopherfortune.studytopia.R;

import java.util.ArrayList;

public class QuestionListAdapter extends ArrayAdapter<Flashcards> {

    private final Context context;
    private final int resource;
    private final ArrayList<Flashcards> flashcardsArrayList;

    public QuestionListAdapter(Context context, int resource, ArrayList<Flashcards> flashcardsArrayList){
        super(context, resource, flashcardsArrayList);
        this.context = context;
        this.resource = resource;
        this.flashcardsArrayList = flashcardsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View questionRowView = layoutInflater.inflate(R.layout.question_row, null);

        TextView testNameTxtVw = questionRowView.findViewById(R.id.question_row_txt_vw);
        TextView flashcardCountTxtVw = questionRowView.findViewById(R.id.question_number_question_row_txt_vw);

        testNameTxtVw.setText(flashcardsArrayList.get(position).getQuestion());

        String flashcardCountString = Integer.toString(flashcardsArrayList.get(position).getQuestionNumber());
        flashcardCountTxtVw.setText(flashcardCountString);

        return questionRowView;
    }
}
