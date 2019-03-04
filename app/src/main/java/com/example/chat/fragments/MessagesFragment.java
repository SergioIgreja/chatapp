package com.example.chat.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.Utils;
import com.example.chat.adapter.MessageListAdapter;
import com.example.chat.models.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class MessagesFragment extends Fragment {
    static final int SELECT_PICTURE_REQUEST_CODE = 1;

    private ArrayList<Message> mMessages;
    private MessageListAdapter mMessageListAdapter;
    private String mFriendName;
    private Uri outputFileUri;

    @BindView(R.id.message_list_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.send_message_button) ImageButton mSendMessageButton;
    @BindView(R.id.open_widget_options_button) ImageButton mOpenWidgetOptionsButton;
    @BindView(R.id.message_edit_text) EditText mMessageEditText;
    @BindView(R.id.fragment_messages_friend_name) TextView mFriendNameTextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages,container,false);
        ButterKnife.bind(this, view);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(llm);
        mMessageListAdapter = new MessageListAdapter(getContext(),mMessages);
        mMessageListAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mMessageListAdapter);

        mFriendNameTextView.setText(mFriendName);

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
            mMessages.add(newMessage);
            int position = mMessages.size() - 1;
            mMessageListAdapter.notifyItemInserted(position);
            mMessageEditText.setText("");
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    private void sendImage(Uri image) throws Exception {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), image);
        Message newMessage = new Message(1, false, bitmap);
        mMessages.add(newMessage);
        int position = mMessages.size() - 1;
        mMessageListAdapter = new MessageListAdapter(getContext(), mMessages);
        mRecyclerView.setAdapter(mMessageListAdapter);
        mRecyclerView.smoothScrollToPosition(position);
    }



    private void dispatchGetPictureIntent() {
        //Determine URI of camera image to save
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Chat" + File.separator);
        root.mkdirs();
        final String fname = Utils.getUniqueFileName();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        //Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                }else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    }else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
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
                }catch (Exception e){
                    System.out.println(e.toString());
                }
            }
        }
    }
}
