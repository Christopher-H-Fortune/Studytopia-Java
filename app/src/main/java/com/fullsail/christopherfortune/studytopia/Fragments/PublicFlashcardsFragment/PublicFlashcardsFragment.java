package com.fullsail.christopherfortune.studytopia.Fragments.PublicFlashcardsFragment;

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

public class PublicFlashcardsFragment extends ListFragment {

    public static final String TAG = "PublicFlashcardsFragment.TAG";

    public PublicFlashcardsInterface publicFlashcardsListener;

    public static PublicFlashcardsFragment newInstance(){
        return new PublicFlashcardsFragment();
    }

    public interface PublicFlashcardsInterface{
        void passListView(ListView publicListView);
        void testSelected(int testSelected);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof PublicFlashcardsInterface){
            publicFlashcardsListener = (PublicFlashcardsInterface)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_public_flashcards, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getView() != null){

            ListView publicFlashcardListView = getView().findViewById(android.R.id.list);
            publicFlashcardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    publicFlashcardsListener.testSelected(position);
                }
            });
            publicFlashcardsListener.passListView(publicFlashcardListView);
        }
    }
}
