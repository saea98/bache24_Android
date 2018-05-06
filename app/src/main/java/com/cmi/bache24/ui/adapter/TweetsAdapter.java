package com.cmi.bache24.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.BacheTweet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 12/9/15.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetViewHolder> {

    private Context mContext;
    private List<BacheTweet> mBacheTweetList;

    public TweetsAdapter(Context context) {
        this.mContext = context;
        this.mBacheTweetList = new ArrayList<>();
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View reportView = LayoutInflater.from(mContext).inflate(R.layout.tweet_row, parent, false);

        return new TweetViewHolder(reportView);
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        BacheTweet tweet = mBacheTweetList.get(position);

        holder.setUsername(tweet.getDisplayName());
        holder.setDisplayName(tweet.getUsername());
        holder.setDate(tweet.getDate());
        holder.setDescription(tweet.getDescription());
        holder.setPicture(tweet.getUserPicture());
    }

    @Override
    public int getItemCount() {
        return mBacheTweetList.size();
    }

    public void addAllSections(List<BacheTweet> bacheTweets) {
        this.mBacheTweetList.clear();
        this.mBacheTweetList.addAll(bacheTweets);
        notifyDataSetChanged();
    }

    public class TweetViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfilePicture;
        private TextView mName;
        private TextView mUsername;
        private TextView mTimeAgo;
        private TextView mDescription;

        public TweetViewHolder(View itemView) {
            super(itemView);

            mProfilePicture = (ImageView) itemView.findViewById(R.id.imageview_tweet_picture);
            mName = (TextView) itemView.findViewById(R.id.textview_tweet_user);
            mUsername = (TextView) itemView.findViewById(R.id.textview_username);
            mTimeAgo = (TextView) itemView.findViewById(R.id.textview_hour);
            mDescription = (TextView) itemView.findViewById(R.id.textview_description);
        }

        private void setPicture(String profilePicture) {
            Picasso.with(mContext)
                    .load(profilePicture)
                    .placeholder(R.drawable.user_registro)
                    .error(R.drawable.user_registro).into(mProfilePicture);
        }

        private void setDisplayName(String user) {
            mName.setText(user);
        }

        private void setUsername(String username) {
            mUsername.setText("@" + username);
        }

        private void setDate(String date) {
            mTimeAgo.setText(date);
        }

        private void setDescription(String description) {
            mDescription.setText(description);
        }
    }
}
