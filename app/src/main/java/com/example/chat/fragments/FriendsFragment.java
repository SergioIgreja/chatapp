package com.example.chat.fragments;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chat.R;
import com.example.chat.activities.FriendListActivity;
import com.example.chat.adapter.FriendListAdapter;
import com.example.chat.models.Friend;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsFragment extends Fragment implements LifecycleOwner {

    private ArrayList<Friend> mFriends;
    @BindView(R.id.friend_list_recycler_view) RecyclerView mRecyclerView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(ArrayList<Friend> friends) {
        FriendsFragment fragment = new FriendsFragment();
        fragment.mFriends = friends;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getLifecycle().addObserver((LifecycleObserver) getActivity());
    }

    @Override
    public void onStart() {
        ((FriendListActivity)getActivity()).setActionBarTitle("Friends");
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new FriendListAdapter(getContext(),mFriends));
        return view;
    }

    @Override
    public Lifecycle getLifecycle() {
        return super.getLifecycle();
    }
}
