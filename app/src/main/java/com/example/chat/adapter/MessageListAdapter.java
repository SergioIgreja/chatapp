package com.example.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.models.Message;
import com.nostra13.universalimageloader.core.ImageLoader;

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
           }else {
               MessageSentViewHolder messageSentViewHolder = (MessageSentViewHolder) fragmentViewHolder;
               Message message = mMessages.get(position);
               if (message.getImage() == null){
                   messageSentViewHolder.myMessage.setText(mMessages.get(position).getText());
               }else {
                   messageSentViewHolder.myMessageImage.setImageBitmap(message.getImage());
                   messageSentViewHolder.myMessageImage.setVisibility(View.VISIBLE);
                   messageSentViewHolder.myMessage.setVisibility(View.GONE);
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

        TextView friendMessage;
        LinearLayout messageReceivedParentLayout;

        public MessageReceivedViewHolder(View v) {
            super(v);
            friendMessage = (TextView) v.findViewById(R.id.friend_message);
            messageReceivedParentLayout = (LinearLayout) v.findViewById(R.id.message_received_parent_layout);
        }
    }

    public static class MessageSentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.my_message) TextView myMessage;
        @BindView(R.id.message_sent_parent_layout) LinearLayout messageSentParentLayout;
        @BindView(R.id.my_message_image) ImageView myMessageImage;

        public MessageSentViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
