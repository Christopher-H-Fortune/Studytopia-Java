package com.fullsail.christopherfortune.studytopia.Fragments.LoginFragments.ForgotPasswordFragment;

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

public class ForgotPasswordFragment extends Fragment {

    // String variable to reference the loginFragment when displaying
    public static final String TAG = "ForgotPasswordFragment.TAG";

    // LoginFragmentInterface variable to call the interface methods
    private ForgotPasswordInterface forgotPasswordListener;

    public interface ForgotPasswordInterface {
        void sendResetLink(EditText emailEditText);
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // If the context is an instance of the loginFragmentInterface
        if (context instanceof ForgotPasswordInterface) {

            // Set the loginFragmentInterfaceListener to the context as a LoginFragmentInterface
            forgotPasswordListener = (ForgotPasswordInterface) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get the fragment_login to inflate to the view
        View forgotPasswordFragmentView = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        // Edit Text to obtain the users email
        final EditText emailEditText = forgotPasswordFragmentView.findViewById(R.id.email_password_reset_edit_txt);

        // Get the sendEmail button to allow the user to reset their password
        Button sendEmail = forgotPasswordFragmentView.findViewById(R.id.send_email_button);

        // Set and onClickListener to the loginButton
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the login interface method passing the email and password editText
                forgotPasswordListener.sendResetLink(emailEditText);
            }
        });

        // Return the loginFragmentView
        return forgotPasswordFragmentView;
    }
}
