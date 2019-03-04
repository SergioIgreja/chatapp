package com.example.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chat.R;
import com.example.chat.models.Friend;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private ArrayList<Friend> mFriends;
    private Context mContext;

    private static final String TAG = "FriendListAdapter";

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendListAdapter(Context context, ArrayList<Friend> friends) {
        mContext = context;
        mFriends = friends;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_cell_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder called");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mFriends.get(position).getPicture(),holder.friendImage);

        holder.friendName.setText(mFriends.get(position).getName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(mFriends.get(position).getName());
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView friendImage;
        TextView friendName;
        RelativeLayout parentLayout;

        public ViewHolder(View v) {
            super(v);
            friendImage = (CircleImageView) v.findViewById(R.id.friend_image);
            friendName = (TextView) v.findViewById(R.id.friend_name);
            parentLayout = (RelativeLayout) v.findViewById(R.id.parent_layout);
        }
    }
}
