package com.cmi.bache24.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmi.bache24.R;
import com.cmi.bache24.data.model.BacheTweet;
import com.cmi.bache24.ui.adapter.TweetsAdapter;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.services.common.BackgroundPriorityRunnable;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * A placeholder fragment containing a simple view.
 */
public class NewsFragment extends Fragment {

    private RecyclerView mTweetsList;
    private TweetsAdapter mTweetsAdapter;
    private TextView mDisplayName;
    private TextView mUsername;
    private List<BacheTweet> bacheTweetList;
    private CircleImageView imgPrifole;
    private LinearLayout mLayoutTweetsContainer;
    private LinearLayout mLayoutTweetsLoader;
    private TextView mTextViewLoaderMessage;
    private ProgressBar mProgressLoader;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        mTweetsList = (RecyclerView) root.findViewById(R.id.tweet_list);
        mTweetsAdapter = new TweetsAdapter(getActivity());
        mDisplayName = (TextView) root.findViewById(R.id.textview_displayname);
        mUsername = (TextView) root.findViewById(R.id.textview_username);
        imgPrifole = (CircleImageView) root.findViewById(R.id.image_profile_picture);
        mLayoutTweetsContainer = (LinearLayout) root.findViewById(R.id.layout_tweets_container);
        mLayoutTweetsLoader = (LinearLayout) root.findViewById(R.id.layout_tweets_loader);
        mTextViewLoaderMessage = (TextView) root.findViewById(R.id.textView34);
        mProgressLoader = (ProgressBar) root.findViewById(R.id.progressBar2);

        mLayoutTweetsContainer.setVisibility(View.GONE);
        mLayoutTweetsLoader.setVisibility(View.VISIBLE);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        new Handler().postDelayed(new BackgroundPriorityRunnable() {
            @Override
            protected void onRun() {
                getTweetsForUser("GobCDMX");
            }
        }, 1000);

        return root;
    }

    private void getTweetsForUser(String user) {
        bacheTweetList = new ArrayList<>();

        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("JHViUFei9dItmgB330tq18YhY")
                .setOAuthConsumerSecret(
                        "EL9ws019iCJkRp1DSgc9WwCGNKrN9PRENedbU2TCbt1Su2tQHT")
                .setOAuthAccessToken(
                        "46514008-DwsUPtIDKZUYaDLlPo535idx4jzkB1B1r4kDccEsu")
                .setOAuthAccessTokenSecret(
                        "1zmBxnIn6PkbmtO58TIKivkU6MCeQexP0anXOQS1GAKoh");

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter4j.Twitter twitter = tf.getInstance();
        try {
            List<Status> statuses;
            Paging paging = new Paging(1, 50);
            statuses = twitter.getUserTimeline(user, paging);

            for (int i = 0; i < statuses.size(); i++) {
                BacheTweet tweet = new BacheTweet();

                tweet.setUserPicture(statuses.get(i).getUser().getProfileImageURL());
                tweet.setUsername(statuses.get(i).getUser().getName());
                tweet.setDisplayName(statuses.get(i).getUser().getScreenName());
                tweet.setDescription(statuses.get(i).getText());
                tweet.setDate("");

                bacheTweetList.add(tweet);
            }

            setupTweetList();

        } catch (twitter4j.TwitterException te) {
            te.printStackTrace();

            mLayoutTweetsContainer.setVisibility(View.GONE);
            mLayoutTweetsLoader.setVisibility(View.VISIBLE);
            mProgressLoader.setVisibility(View.GONE);
            mTextViewLoaderMessage.setText("No se pudo cargar la sección de noticias");
        }
    }

    private void test() {
        TwitterCore.getInstance().logInGuest( new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient =  TwitterCore.getInstance().getApiClient(session);
                twitterApiClient.getStatusesService().userTimeline(null, "GobiernoDF", 10, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> listResult) {

                    }

                    @Override
                    public void failure(TwitterException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Could not retrieve tweets", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void failure(TwitterException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Could not get guest Twitter session", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private void setupTweetList() {

        if (bacheTweetList.size() > 0 ) {
            mDisplayName.setText(bacheTweetList.get(0).getUsername());
            mUsername.setText("@" + bacheTweetList.get(0).getDisplayName());

            Picasso.with(getActivity())
                    .load(bacheTweetList.get(0).getUserPicture())
                    .placeholder(R.drawable.user_registro)
                    .error(R.drawable.user_registro).into(imgPrifole);

            mLayoutTweetsContainer.setVisibility(View.VISIBLE);
            mLayoutTweetsLoader.setVisibility(View.GONE);

        } else {
            mLayoutTweetsContainer.setVisibility(View.GONE);
            mLayoutTweetsLoader.setVisibility(View.VISIBLE);
            mProgressLoader.setVisibility(View.GONE);
            mTextViewLoaderMessage.setText("No se encontrarón noticias para mostrar");
        }

        mTweetsAdapter.addAllSections(bacheTweetList);

        mTweetsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTweetsList.setAdapter(mTweetsAdapter);
    }
}
