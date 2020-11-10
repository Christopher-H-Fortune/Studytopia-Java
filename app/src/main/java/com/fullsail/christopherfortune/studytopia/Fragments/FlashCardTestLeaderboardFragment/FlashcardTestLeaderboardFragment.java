package com.fullsail.christopherfortune.studytopia.Fragments.FlashCardTestLeaderboardFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class FlashcardTestLeaderboardFragment extends Fragment {

    public static final String TAG = "FlashcardTestLeaderboardFragment.TAG";

    public static FlashcardTestLeaderboardInterface flashcardTestLeaderboardListener;

    public static FlashcardTestLeaderboardFragment newInstance(){
        return new FlashcardTestLeaderboardFragment();
    }

    public interface FlashcardTestLeaderboardInterface{
        void passLeaderboardListView(ListView leaderboardListView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FlashcardTestLeaderboardInterface){
            flashcardTestLeaderboardListener = (FlashcardTestLeaderboardInterface)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flashcard_test_leaderboard, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        if(getView() != null){

            ListView leaderboardListView = getView().findViewById(android.R.id.list);
            flashcardTestLeaderboardListener.passLeaderboardListView(leaderboardListView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done_menu, menu);
    }
}
