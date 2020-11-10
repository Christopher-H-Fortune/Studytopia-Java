package com.fullsail.christopherfortune.studytopia.Fragments.EditProfleFragment;

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
import android.widget.EditText;

import com.fullsail.christopherfortune.studytopia.R;
import com.loopj.android.image.SmartImageView;

public class EditProfileFragment extends Fragment {

    public static final String TAG = "EditProfileFragment.TAG";

    public EditProfileInterface editProfileListener;

    public static EditProfileFragment newInstance(){
        return new EditProfileFragment();
    }

    public interface EditProfileInterface{
        void passViews(EditText firstNameEditText,
                       EditText lastNameEditText,
                       EditText usernameEditText,
                       EditText emailEditText,
                       EditText passwordEditText,
                       SmartImageView editProfileImageSmartImg);
        void editProfileImage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof EditProfileInterface){
            editProfileListener = (EditProfileInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View editProfileFragmentView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        SmartImageView editProfileImageSmartImg = editProfileFragmentView.findViewById(R.id.new_profile_image_smart_img);
        editProfileImageSmartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileListener.editProfileImage();
            }
        });
        final EditText firstNameEditText = editProfileFragmentView.findViewById(R.id.first_name_profle_edit_edt_txt);
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    firstNameEditText.setText(null);
                }
            }
        });
        final EditText lastNameEditText = editProfileFragmentView.findViewById(R.id.last_name_profle_edit_edt_txt);
        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    lastNameEditText.setText(null);
                }
            }
        });
        final EditText usernameEditText = editProfileFragmentView.findViewById(R.id.username_profle_edit_edt_txt);
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    usernameEditText.setText(null);
                }
            }
        });
        final EditText emailEditText = editProfileFragmentView.findViewById(R.id.email_profle_edit_edt_txt);
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    emailEditText.setText(null);
                }
            }
        });
        final EditText passwordEditText = editProfileFragmentView.findViewById(R.id.password_profle_edit_edt_txt);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    passwordEditText.setText(null);
                }
            }
        });

        editProfileListener.passViews(firstNameEditText, lastNameEditText, usernameEditText, emailEditText, passwordEditText, editProfileImageSmartImg);

        return editProfileFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_profile_menu, menu);
    }
}
