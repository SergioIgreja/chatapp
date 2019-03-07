package com.example.chat.activities;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.fragments.FriendsFragment;
import com.example.chat.fragments.FullScreenImageFragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendListActivity extends AppCompatActivity implements LifecycleObserver {
    private static final String TAG = "FriendListActivity";

    //vars
    private ArrayList<Friend> mFriends;
    private ArrayList<Message> mMessages;
    private Handler mHandler;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;


    @BindView(R.id.welcome_message_friendlist_activity) TextView mWelcomeMessage;
    @BindView(R.id.drawer_layout_friendlist_activity) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.my_toolbar) Toolbar mActionBar;
    @BindView(R.id.action_bar_textview) TextView mActionBarTitle;


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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        setActionBarTitle("Home");
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mWelcomeMessage.setText("Hello " + intent.getStringExtra(MainActivity.EXTRA_USERNAME) + "!");

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.menu_friend_list:
                                    menuItem.setChecked(true);
                                    displayFriendListFragment();
                                    mDrawerLayout.closeDrawers();


                        }
                        return true;
                    }
                }
        );

        setSupportActionBar(mActionBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_light_blue_35dp);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayFriendListFragment() {
        mHandler = new Handler();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mWelcomeMessage.setVisibility(View.GONE);

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

        Fragment fragment = mFragmentManager.findFragmentByTag("FRIENDS-FRAGMENT");
        if (fragment == null) {
            mFragmentTransaction.add(R.id.fragment_content, FriendsFragment.newInstance(mFriends), "FRIENDS-FRAGMENT").addToBackStack(null);
        } else {
            mFragmentManager.popBackStack();
            mFragmentTransaction.replace(R.id.fragment_content, fragment, "FRIENDS-FRAGMENT");
        }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowImageFullScreenEvent(Uri imageUri) {
        dispatchShowImageFullScreenIntent(imageUri);
    }

    public void dispatchShowImageFullScreenIntent(Uri imageUri) {
        FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(imageUri);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, fullScreenImageFragment, "IMAGE-FRAGMENT").addToBackStack("CHAT-FRAGMENT");
        fragmentTransaction.commit();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onFragmentDestroyed() {
        mWelcomeMessage.setVisibility(View.VISIBLE);
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle.setText(title);
    }

    public void changeActionBarVisibility() {
        if (mActionBar.getVisibility() == View.GONE) {
            mActionBar.setVisibility(View.VISIBLE);
        }else {
            mActionBar.setVisibility(View.GONE);
        }

    }
}
