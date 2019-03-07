package com.example.chat.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.activities.FriendListActivity;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenImageFragment extends Fragment {
    private Uri mImageUri;
    @BindView(R.id.fragment_full_screen_images_photoview) PhotoView mPhotoView;
    @BindView(R.id.fragment_full_screen_framelayout) FrameLayout mFrameLayout;

    public FullScreenImageFragment() {
        // Required empty public constructor
    }

    public static FullScreenImageFragment newInstance(Uri imageUri) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.mImageUri = imageUri;
        return fragment;
    }

    @Override
    public void onDestroy() {
        ((FriendListActivity) getActivity()).changeActionBarVisibility();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);
        ButterKnife.bind(this, view);
        ((FriendListActivity) getActivity()).changeActionBarVisibility();
        Glide.with(getContext())
                .load(mImageUri)
                .into(mPhotoView);
        hideNavigationBar(view);
        return view;
    }

    public static void hideNavigationBar(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
