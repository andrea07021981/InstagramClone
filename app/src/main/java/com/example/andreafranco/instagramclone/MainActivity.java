package com.example.andreafranco.instagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private static final int REQUEST_IMAGES = 1;

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

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoto();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.share:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    getPhoto();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGES && resultCode == RESULT_OK) {
            Uri uriImages = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImages);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();

                //Start operation for sending image to server
                ParseFile parseFile = new ParseFile("image.png", byteArray);
                ParseObject parseObjectImage = new ParseObject("Image");
                parseObjectImage.put("image", parseFile);
                parseObjectImage.put("username", ParseUser.getCurrentUser().getUsername());
                parseObjectImage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error saving image:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
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
                        if (e == null) {
                            showUserList();
                        }
                    }
                });
            } else {
                //Try the login
                ParseUser.logInInBackground(mUsernameEditText.getText().toString(), mPasswordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        showMessage(e, "Login ok!");
                        if (e == null) {
                            showUserList();
                        }
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
        } else if (view.getId() == R.id.logo_imageview || view.getId() == R.id.background_layout) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClick(view);
        }
        return false;
    }

    private void showUserList() {
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }
}
