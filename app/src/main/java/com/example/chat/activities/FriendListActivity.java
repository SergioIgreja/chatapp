package com.example.chat.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.chat.R;
import com.example.chat.fragments.FriendsFragment;
import com.example.chat.fragments.MessagesFragment;
import com.example.chat.models.Friend;
import com.example.chat.models.Message;
import com.example.chat.network.GetDataService;
import com.example.chat.network.RetrofitClientInstance;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListActivity extends AppCompatActivity {
    private static final String TAG = "FriendListActivity";

    //vars
    private ArrayList<Friend> mFriends;
    private ArrayList<Message> mMessages;
    private Handler mHandler;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        Log.d(TAG, "onCreate: started");

        mHandler = new Handler();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        GetDataService getDataService = retrofit.create(GetDataService.class);

        Call<List<Friend>> call = getDataService.getAllFriends();
        call.enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Failure");
                }

                mFriends = (ArrayList) response.body();

                initFriendListRecyclerView();
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                Log.d(TAG, "onResponse: Failure");
            }
        });

    }

    private void initFriendListRecyclerView() {
        Log.d(TAG, "initRecyclerView: init");

        FriendsFragment fragment = FriendsFragment.newInstance(mFriends);
        mFragmentTransaction.replace(R.id.fragment_content, fragment, "FRIENDS-FRAGMENT");
        mFragmentTransaction.commit();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartMessagingEvent(String friendName) {
        initMessageListRecyclerView(friendName);
    }

    private void initMessageListRecyclerView(final String friendName) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        GetDataService getDataService = retrofit.create(GetDataService.class);

        Call<List<Message>> call = getDataService.getAllMessages();
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, final Response<List<Message>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Failure");
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMessages = (ArrayList) response.body();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();

                        MessagesFragment fragment = MessagesFragment.newInstance(mMessages, friendName);

                        ft.replace(R.id.fragment_content, fragment, "CHAT-FRAGMENT").addToBackStack(null);
                        ft.commit();

                    }
                });

            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d(TAG, "onResponse: Failure");
            }
        });
    }

}
