package com.example.andreafranco.instagramclone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText mUsernameEditText, mPasswordEditText;
    TextView mLoginTextView;
    ImageView mLogoImageView;
    ConstraintLayout mBackground;
    boolean mSignUpModeActive = true;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsernameEditText = findViewById(R.id.username_edittext);
        mPasswordEditText = findViewById(R.id.password_edittext);
        mPasswordEditText.setOnKeyListener(this);
        mSignUpButton = findViewById(R.id.signup_button);
        mLoginTextView = findViewById(R.id.login_textview);
        mLoginTextView.setOnClickListener(this);
        mLogoImageView = findViewById(R.id.logo_imageview);
        mLogoImageView.setOnClickListener(this);
        mBackground = findViewById(R.id.background_layout);
        mBackground.setOnClickListener(this);
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void signUpClick(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (TextUtils.isEmpty(mUsernameEditText.getText()) ||
                TextUtils.isEmpty(mPasswordEditText.getText())) {
            Toast.makeText(this, "Username and password are required", Toast.LENGTH_LONG).show();
        } else {
            if (mSignUpModeActive) {
                ParseUser user = new ParseUser();
                user.setUsername(mUsernameEditText.getText().toString());
                user.setPassword(mPasswordEditText.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        showMessage(e, "Sign Up completed");
                    }
                });
            } else {
                //Try the login
                ParseUser.logInInBackground(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        showMessage(e, "Login ok!");
                    }
                });
            }
        }
    }

    private void showMessage(ParseException e, String message) {
        if (e == null) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_textview) {
            if (mSignUpModeActive) {
                mSignUpModeActive = false;
                mSignUpButton.setText("Login");
                mLoginTextView.setText("or, Sign Up");
            } else {
                mSignUpModeActive = true;
                mSignUpButton.setText("Sign Up");
                mLoginTextView.setText("or, Login");
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClick(view);
        }
        return false;
    }
}
