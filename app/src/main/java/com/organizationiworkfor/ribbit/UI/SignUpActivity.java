package com.organizationiworkfor.ribbit.UI;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.RibbitApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    @Bind(R.id.emailField) EditText mEmailField;
    @Bind(R.id.usernameField) EditText mUsernameField;
    @Bind(R.id.passwordField) EditText mPasswordField;
    @Bind(R.id.signupButton) Button mSignUpButton;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.cancelButton) Button mCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }


    @OnClick(R.id.signupButton)
    public void signUpClick() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        String email = mEmailField.getText().toString();

        //gets rid of spaces
        username = username.trim();
        password = password.trim();
        email = email.trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialogFragment dialog = new AlertDialogFragment();
            dialog.setAlertMessage(getString(R.string.signup_blank_error));
            dialog.show(getFragmentManager(), "Error Dialog");
        } else {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            ParseUser newUser = new ParseUser();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUsername(username);
            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (e == null) {
                        //success if no exception
                        RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AlertDialogFragment dialog = new AlertDialogFragment();
                        dialog.setAlertMessage(e.getMessage());
                        dialog.show(getFragmentManager(), "Error Dialog");
                    }
                }
            });

        }
    }

    @OnClick(R.id.cancelButton)
    public void cancelClick() {
        finish();
    }


}
