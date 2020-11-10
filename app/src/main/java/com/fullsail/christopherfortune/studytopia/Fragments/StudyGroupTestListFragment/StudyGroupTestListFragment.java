package com.fullsail.christopherfortune.studytopia.Fragments.StudyGroupTestListFragment;

import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.fullsail.christopherfortune.studytopia.R;

public class StudyGroupTestListFragment extends ListFragment {

    public static final String TAG = "StudyGroupTestListFragment.TAG";

    public StudyGroupInterface studyGroupTestsListener;

    public static StudyGroupTestListFragment newInstance(){
        return new StudyGroupTestListFragment();
    }

    public interface StudyGroupInterface {
        void leaveStudyGroup();
        void createFlashcards();
        void takeFlashcardTest(int testChosen);
        void editFlashcardTest(int testChosen);
        void passTestListView(ListView testListView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof StudyGroupInterface){
            studyGroupTestsListener = (StudyGroupInterface) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View studyGroupFragmentView = inflater.inflate(R.layout.fragment_study_group_test_list, container, false);

        Button createFlashcardsBtn = studyGroupFragmentView.findViewById(R.id.study_group_create_flashcards_btn);
        createFlashcardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyGroupTestsListener.createFlashcards();
            }
        });

        return studyGroupFragmentView;
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

                    final int testChosen = position;

                    // Ask the user to choose a flashcard count
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Take Test?");
                    alertDialogBuilder.setMessage("To take the test, tap take test. To view test list tap cancel.");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    studyGroupTestsListener.takeFlashcardTest(testChosen);
                                }
                            });
                    alertDialogBuilder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
            testListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    final int testChosen = position;

                    // Ask the user to choose a flashcard count
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setTitle("Edit Test?");
                    alertDialogBuilder.setMessage("To edit the test, tap edit test. To view test list tap cancel.");
                    alertDialogBuilder.setCancelable(true);
                    alertDialogBuilder.setPositiveButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    studyGroupTestsListener.editFlashcardTest(testChosen);
                                }
                            });
                    alertDialogBuilder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return true;
                }
            });
            studyGroupTestsListener.passTestListView(testListView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.leave_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.save){
            studyGroupTestsListener.leaveStudyGroup();
        }

        return true;
    }

}
