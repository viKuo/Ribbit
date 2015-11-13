package com.organizationiworkfor.ribbit.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.RibbitApplication;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LogInActivity extends AppCompatActivity {
    @Bind(R.id.signupText) TextView mSignUpText;
    @Bind(R.id.loginButton) Button mLogInButton;
    @Bind(R.id.usernameField) TextView mUsernameField;
    @Bind(R.id.passwordField) TextView mPasswordField;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    private String TAG = LogInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
    }

    @OnClick(R.id.signupText)
    public void signUpClick() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.loginButton)
    public void logInClick() {
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();

        //gets rid of spaces
        username = username.trim();
        password = password.trim();


        if (username.isEmpty() || password.isEmpty()) {
            AlertDialogFragment dialog = new AlertDialogFragment();
            dialog.setAlertMessage(getString(R.string.login_empty_error));
            dialog.show(getFragmentManager(), "Error Dialog");
        } else {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (e == null) {
                        //success if no exception
                        RibbitApplication.updateParseInstallation(parseUser);

                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Log.i(TAG, parseUser.getUsername());
                    } else {
                        AlertDialogFragment dialog = new AlertDialogFragment();
                        dialog.setAlertMessage(getString(R.string.login_faulty_error));
                        dialog.show(getFragmentManager(), "Error Dialog");
                    }
                }
            });

        }
    }

}
