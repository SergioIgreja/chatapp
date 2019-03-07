package com.example.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.models.Message;
import com.github.chrisbanes.photoview.PhotoView;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Message> mMessages;
    private Context mContext;

    private static final String TAG = "FragmentRecycleVAdapter";

    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageListAdapter(Context context, ArrayList<Message> messages) {
        mContext = context;
        mMessages = messages;
    }

    public void addMessage(Message message) {
        this.mMessages.add(message);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                View view = LayoutInflater.from(mContext).inflate(R.layout.message_sent_layout, parent, false);
                MessageListAdapter.MessageSentViewHolder holder = new MessageListAdapter.MessageSentViewHolder(view);
                return holder;
            case 0:
                View view2 = LayoutInflater.from(mContext).inflate(R.layout.message_received_layout, parent, false);
                MessageReceivedViewHolder holder2= new MessageReceivedViewHolder(view2);
                return holder2;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder fragmentViewHolder, final int position) {
           if(fragmentViewHolder instanceof MessageReceivedViewHolder) {
               ((MessageReceivedViewHolder) fragmentViewHolder).friendMessage.setText(mMessages.get(position).getText());
           }else if (fragmentViewHolder instanceof MessageSentViewHolder){
               final MessageSentViewHolder messageSentViewHolder = (MessageSentViewHolder) fragmentViewHolder;
               final Message message = mMessages.get(position);
               if (message.getImage() == null){
                   messageSentViewHolder.myMessage.setText(message.getText());
                   messageSentViewHolder.myMessageImage.setVisibility(View.GONE);
                   messageSentViewHolder.myMessage.setVisibility(View.VISIBLE);
               }else {
                   Glide.with(mContext)
                           .load(message.getImage().getImageUri())
                           .into(messageSentViewHolder.myMessageImage);
                   messageSentViewHolder.myMessageImage.setVisibility(View.VISIBLE);
                   messageSentViewHolder.myMessage.setVisibility(View.GONE);
                   messageSentViewHolder.myMessageImage.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
                       @Override
                       public boolean onSingleTapConfirmed(MotionEvent e) {
                           return false;
                       }

                       @Override
                       public boolean onDoubleTap(MotionEvent e) {
                           EventBus.getDefault().post(message.getImage().getImageUri());
                           return true;
                       }

                       @Override
                       public boolean onDoubleTapEvent(MotionEvent e) {
                           return false;
                       }
                   });
               }
           }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        return message.getAuthor() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public static class MessageReceivedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.friend_message) TextView friendMessage;
        @BindView(R.id.message_received_parent_layout) LinearLayout messageReceivedParentLayout;

        public MessageReceivedViewHolder(View v) {
            super(v);
            ButterKnife.bind(this,v);
        }
    }

    public static class MessageSentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.my_message) TextView myMessage;
        @BindView(R.id.message_sent_parent_layout) LinearLayout messageSentParentLayout;
        @BindView(R.id.my_message_image) PhotoView myMessageImage;

        public MessageSentViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}
