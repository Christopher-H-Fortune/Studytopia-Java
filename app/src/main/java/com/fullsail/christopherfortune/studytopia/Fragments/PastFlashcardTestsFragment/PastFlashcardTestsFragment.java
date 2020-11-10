package com.fullsail.christopherfortune.studytopia.Fragments.PastFlashcardTestsFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class PastFlashcardTestsFragment extends ListFragment {

    public static final String TAG = "PastFlashcardTestsFragment.TAG";

    public PastFlashcardTestsInterface pastFlashcardTestsListener;

    public static PastFlashcardTestsFragment newInstance(){
        return new PastFlashcardTestsFragment();
    }

    public interface PastFlashcardTestsInterface{
        void passPastTestListView(ListView testListView);
        void selectPastTest(int testChosen);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PastFlashcardTestsInterface){
            pastFlashcardTestsListener = (PastFlashcardTestsInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_past_flashcard_tests, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView pastTestListView = getView().findViewById(android.R.id.list);
            pastTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pastFlashcardTestsListener.selectPastTest(position);
                }
            });
            pastFlashcardTestsListener.passPastTestListView(pastTestListView);
        }
    }
}
