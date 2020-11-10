package com.fullsail.christopherfortune.studytopia.Fragments.FlashcardTestResultsFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fullsail.christopherfortune.studytopia.R;
import com.github.mikephil.charting.charts.PieChart;

public class FlashcardTestResultsFragment extends Fragment {

    public static final String TAG = "FlashcardTestResultsFragment.TAG";

    private FlashcardTestResultsInterface flashcardTestResultsListener;

    public static FlashcardTestResultsFragment newInstance(){
        return new FlashcardTestResultsFragment();
    }

    public interface FlashcardTestResultsInterface{
        void passViews(PieChart correctWrongPieChart, PieChart answeredSkippedPieChart);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestResultsInterface){
            flashcardTestResultsListener = (FlashcardTestResultsInterface)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flashcard_test_results, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            PieChart correctWrongGraphView = getView().findViewById(R.id.correct_wrong_graph);
            PieChart answeredSkippedGraphView = getView().findViewById(R.id.answered_skipped_graph);
            flashcardTestResultsListener.passViews(correctWrongGraphView, answeredSkippedGraphView);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done_menu, menu);
    }
}
