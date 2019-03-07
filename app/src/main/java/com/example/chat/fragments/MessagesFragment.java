package com.example.chat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.Utils;
import com.example.chat.activities.FriendListActivity;
import com.example.chat.adapter.MessageListAdapter;
import com.example.chat.models.Message;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class MessagesFragment extends Fragment {
    static final int SELECT_PICTURE_REQUEST_CODE = 1;

    private ArrayList<Message> mMessages;
    private MessageListAdapter mMessageListAdapter;
    private LinearLayoutManager llm;
    private String mFriendName;
    private Uri outputFileUri;

    @BindView(R.id.message_list_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.send_message_button) ImageButton mSendMessageButton;
    @BindView(R.id.open_widget_options_button) ImageButton mOpenWidgetOptionsButton;
    @BindView(R.id.message_edit_text) EditText mMessageEditText;

    public static MessagesFragment newInstance(ArrayList<Message> messages, String friendName) {
        MessagesFragment fragment = new MessagesFragment();
        fragment.mMessages = messages;
        fragment.mFriendName = friendName;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages,container,false);
        ButterKnife.bind(this, view);
        llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);



        mRecyclerView.setLayoutManager(llm);
        mMessageListAdapter = new MessageListAdapter(getContext(),mMessages);
        mMessageListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mMessageListAdapter);

        FriendListActivity activity = (FriendListActivity) getActivity();
        activity.setActionBarTitle(mFriendName);

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });
        mOpenWidgetOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchGetPictureIntent();
            }
        });

        return view;
    }

    private void sendMessage(View v) {
        String text = mMessageEditText.getText().toString();
        if(!text.isEmpty()){
            Message newMessage = new Message(1,false, text);
            mMessageListAdapter.addMessage(newMessage);
            mMessageEditText.setText("");
            mRecyclerView.smoothScrollToPosition(mMessages.size());
        }
    }

    private void sendImage(Uri image) throws Exception {
        Message newMessage = new Message(1, false, image, null);
        mMessageListAdapter.addMessage(newMessage);
        mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
    }

    private void dispatchGetPictureIntent() {

        // Determine Uri of camera image to save.
        final File root = getContext().getExternalCacheDir();
        File sdImageMainDirectory;
        try {
            sdImageMainDirectory = File.createTempFile("photo", ".jpg", root);
        }catch (IOException e) {
            System.out.println(e.getMessage());
            sdImageMainDirectory = new File(root,"eeqe.jpeg");
        }
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        //Filesystem
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(pickIntent, "Select source");

        //Add the camera options
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }
                try {
                    sendImage(selectedImageUri);
                }catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }
    }

}
