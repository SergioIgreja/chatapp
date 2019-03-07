package com.example.chat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.SyncStateContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.chat.Config;
import com.example.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    public static final String EXTRA_USERNAME = "example.com.chat.USERNAME";
    public static final String EXTRA_PASSWORD = "example.com.chat.PASSWORD";


    @BindView(R.id.username_edit_text) EditText mUsernameEditText;
    @BindView(R.id.password_edit_text) EditText mPasswordEditText;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.main_relative_layout) RelativeLayout mMainRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLogin(v);
            }
        });

        mMainRelativeLayout.setOnTouchListener(this);
    }

    public void verifyLogin(View v){
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            Config config = Config.getInstance();
            if(config.accounts.containsKey(username)) {
                if(config.accounts.get(username).equals(password)) {
                    if(hasInternetConnection()) {
                        displayFriendList(v);
                    }
                }else{
                    displayWrongPasswordAnimation();
                }
            }else {
                displayWrongCredentialsDialog();
            }
        }else {
            displayFriendList(v);
        }
    }

    public Boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }

        showAlertDialog(getString(R.string.no_connectivity_dialog_title), getString(R.string.no_connectivity_dialog_message));
        return false;
    }

    public void showAlertDialog(String title, String message) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void displayWrongPasswordAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mPasswordEditText.setBackgroundResource(R.drawable.edit_text_error);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPasswordEditText.setBackgroundResource(R.drawable.edit_text_normal);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mPasswordEditText.startAnimation(shake);
    }

    public void displayWrongCredentialsDialog() {
        showAlertDialog(getString(R.string.wrong_credentials_dialog_title), getString(R.string.wrong_credentials_dialog_message));
    }


    public void displayFriendList(View view){
        Intent intent = new Intent(this, FriendListActivity.class);

        String username = mUsernameEditText.getText().toString();
        intent.putExtra(EXTRA_USERNAME, username);

        String password = mPasswordEditText.getText().toString();
        intent.putExtra(EXTRA_PASSWORD, password);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

}
