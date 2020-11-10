package com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.LoginFragment;

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

import com.fullsail.christopherfortune.studytopia.R;

public class LoginFragment extends Fragment {

    // String variable to reference the loginFragment when displaying
    public static final String TAG = "LoginFragment.TAG";

    // LoginFragmentInterface variable to call the interface methods
    private LoginFragmentInterface loginFragmentListener;

    public interface LoginFragmentInterface{
        void login(EditText emailEditText, EditText passwordEditText);
        void signUp();
        void forgotPassword();
    }

    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // If the context is an instance of the loginFragmentInterface
        if(context instanceof LoginFragmentInterface){

            // Set the loginFragmentInterfaceListener to the context as a LoginFragmentInterface
            loginFragmentListener = (LoginFragmentInterface)context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the fragment_login to inflate to the view
        View loginFragmentView = inflater.inflate(R.layout.fragment_login, container, false);

        // Edit Text to obtain the users email and password entered
        final EditText emailEditText = loginFragmentView.findViewById(R.id.email_login_edit_text);
        final EditText passwordEditText = loginFragmentView.findViewById(R.id.password_login_edit_text);

        // Get the login button to allow the user to login
        Button loginButton = loginFragmentView.findViewById(R.id.login_button);

        // Set and onClickListener to the loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the login interface method passing the email and password editText
                loginFragmentListener.login(emailEditText, passwordEditText);
            }
        });

        // Get the sign Up Button to allow the user to sign up for an account
        Button signUpButton = loginFragmentView.findViewById(R.id.sign_up_button);

        // Set an onClickListener to the sign up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the signUp interface method
                loginFragmentListener.signUp();
            }
        });

        // Get the forgot password button
        Button forgotPasswordButton = loginFragmentView.findViewById(R.id.forgot_password_button);

        // Set an onClickListener to the forgot password button
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFragmentListener.forgotPassword();
            }
        });

        // Return the loginFragmentView
        return loginFragmentView;
    }
}
