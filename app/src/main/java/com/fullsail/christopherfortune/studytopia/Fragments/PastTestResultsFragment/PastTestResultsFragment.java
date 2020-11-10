package com.fullsail.christopherfortune.studytopia.Fragments.PastTestResultsFragment;

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

public class PastTestResultsFragment extends ListFragment {

    public static final String TAG = "PastTestResultsFragment.TAG";

    public PastTestResultsInterface pastFlashcardTestsResultsListener;

    public static PastTestResultsFragment newInstance(){
        return new PastTestResultsFragment();
    }

    public interface PastTestResultsInterface{
        void passPastTestResultsListView(ListView resultsListView);
        void selectTestResults(int resultsChosen);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PastTestResultsInterface){
            pastFlashcardTestsResultsListener = (PastTestResultsInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_past_test_results, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getView() != null) {

            ListView pastTestListView = getView().findViewById(android.R.id.list);
            pastTestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pastFlashcardTestsResultsListener.selectTestResults(position);
                }
            });
            pastFlashcardTestsResultsListener.passPastTestResultsListView(pastTestListView);
        }
    }
}
