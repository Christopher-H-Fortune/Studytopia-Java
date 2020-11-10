package com.fullsail.christopherfortune.studytopia.Fragments.SelectedFlashcardSubjectTestListFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class SelectedFlashcardSubjectTestListFragment extends ListFragment {

    public static final String TAG = "SelectedFlashcardSubjectTestListFragment.TAG";

    public selectedFlashcardSubjectTestListInterface selectedSubjectFlashcardTestListListener;

    public static SelectedFlashcardSubjectTestListFragment newInstance(){
        return new SelectedFlashcardSubjectTestListFragment();
    }

    public interface selectedFlashcardSubjectTestListInterface {
        void passList(ListView testListView);
        void takeTest(int testChosen, View rowView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof selectedFlashcardSubjectTestListInterface){
            selectedSubjectFlashcardTestListListener = (selectedFlashcardSubjectTestListInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_selected_subject_test_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_menu, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        if(getView() != null){

            ListView testListView = getView().findViewById(android.R.id.list);
            testListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedSubjectFlashcardTestListListener.takeTest(position, view);
                }
            });
            selectedSubjectFlashcardTestListListener.passList(testListView);
        }
    }
}
