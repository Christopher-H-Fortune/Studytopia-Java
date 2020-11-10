package com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.SignUpFragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.fullsail.christopherfortune.studytopia.R;

public class SignUpFragment extends Fragment {
    // String variable to reference the signUpFragment when displaying to activity
    public static final String TAG = "SignUpFragment.TAG";

    // SignUpFragmentInterface variable to call the interface methods
    private SignUpFragmentInterface signUpFragmentListener;

    public interface SignUpFragmentInterface{
        void createAccount(ImageButton profilePicImageView,
                           EditText firstNameEditText,
                           EditText lastNameEditText,
                           EditText usernameEditText,
                           EditText emailEditText,
                           EditText passwordEditText,
                           EditText verifyPasswordEditText);
        void choosePhoto(ImageButton profileImageBtn);
    }

    public static SignUpFragment newInstance(){
        return new SignUpFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // If the context is an instance of the SignUpFragmentInterface
        if(context instanceof SignUpFragmentInterface){

            // Set the signUpFragmentListener to the context as a SignUpFragmentInterface
            signUpFragmentListener = (SignUpFragmentInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Display the Sign Up Fragment to the user
        View signUpFragmentView = inflater.inflate(R.layout.fragment_signup, container, false);

        final ProgressBar progressBar = signUpFragmentView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // Obtain the user input
        final ImageButton profilePicImgBtn = signUpFragmentView.findViewById(R.id.user_profile_image_image_button);
        profilePicImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpFragmentListener.choosePhoto(profilePicImgBtn);
            }
        });

        final EditText firstNameEditText = signUpFragmentView.findViewById(R.id.first_name_signup_edit_text);
        final EditText lastNameEditText = signUpFragmentView.findViewById(R.id.last_name_signup_edit_text);
        final EditText usernameEditText = signUpFragmentView.findViewById(R.id.username_signup_edit_text);
        final EditText emailEditText = signUpFragmentView.findViewById(R.id.email_signup_edit_text);
        final EditText passwordEditText = signUpFragmentView.findViewById(R.id.password_signup_edit_text);
        final EditText verifyPasswordEditText = signUpFragmentView.findViewById(R.id.verify_password_signup_edit_text);

        // Button to create the user account
        Button createAccountButton = signUpFragmentView.findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpFragmentListener.createAccount(profilePicImgBtn,
                                                    firstNameEditText,
                                                    lastNameEditText,
                                                    usernameEditText,
                                                    emailEditText,
                                                    passwordEditText,
                                                    verifyPasswordEditText);
            }
        });

        return signUpFragmentView;
    }
}
