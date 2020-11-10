package com.fullsail.christopherfortune.studytopia.Fragments.UserProfileFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fullsail.christopherfortune.studytopia.R;

public class UserProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment.TAG";

    public UserProfileInterface userProfileListener;

    public static UserProfileFragment newInstance(){
        return new UserProfileFragment();
    }

    public interface UserProfileInterface{
        void viewPastStudyGroups();
        void viewTestsTaken();
        void viewsDisplayed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof UserProfileInterface){
            userProfileListener = (UserProfileInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userProfileFragmentView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageButton pastStudyGroupsButton = userProfileFragmentView.findViewById(R.id.past_study_groups_img_btn);
        pastStudyGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileListener.viewPastStudyGroups();
            }
        });

        ImageButton testsTakenButton = userProfileFragmentView.findViewById(R.id.past_flashcard_tests_img_btn);
        testsTakenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileListener.viewTestsTaken();
            }
        });

        return userProfileFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        userProfileListener.viewsDisplayed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }
}
