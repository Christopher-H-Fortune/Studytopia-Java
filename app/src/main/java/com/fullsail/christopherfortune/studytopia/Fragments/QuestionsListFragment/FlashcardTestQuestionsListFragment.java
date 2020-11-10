package com.fullsail.christopherfortune.studytopia.Fragments.QuestionsListFragment;

import androidx.fragment.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class FlashcardTestQuestionsListFragment extends ListFragment {

    public static final String TAG = "FlashcardTestQuestionsListFragment.TAG";

    public FlashcardTestQuestionsListInterface flashcardTestQuestionsListListener;

    public static FlashcardTestQuestionsListFragment newInstance(){
        return new FlashcardTestQuestionsListFragment();
    }

    public interface FlashcardTestQuestionsListInterface {
       void passQuestionListView(ListView questionListView);
       void questionSelection(int questionSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestQuestionsListInterface){
            flashcardTestQuestionsListListener = (FlashcardTestQuestionsListInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_flashcard_test_questions_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if(getView() != null){

            ListView questionListView = getView().findViewById(android.R.id.list);
            questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    flashcardTestQuestionsListListener.questionSelection(position);
                }
            });
            flashcardTestQuestionsListListener.passQuestionListView(questionListView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_question_menu, menu);
    }

}
