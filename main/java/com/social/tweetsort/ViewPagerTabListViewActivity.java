/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.social.tweetsort;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * SlidingTabLayout and SlidingTabStrip are from google/iosched:
 * https://github.com/google/iosched
 */
public class ViewPagerTabListViewActivity extends BaseActivity implements ObservableScrollViewCallbacks {


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
        //System.exit(0);
        finish();
    }

    int currentState = 0;

    private View mHeaderView;
    private View mToolbarView;
    private int  mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    static MyApplication appState;
    com.social.tweetsort.SlidingTabLayout slidingTabLayout;

    public static Context baseContext = null;

    String username                  = null;

    private RadioGroup rg1;

    View footer;

    protected ImageLoader imageLoader;
    DisplayImageOptions options;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.originaltimeline){
            Fragment fgT = getCurrentFragment();
            if(fgT instanceof MyFragment) {
                HelperFunctions.animate = false;
                MyFragment fg           = (MyFragment) fgT;
                ObservableListView olv  = fg.listView;
                fg.tweetadapter.setTweets(fg.tweetlist);
                fg.tweetadapter.notifyDataSetChanged();
                olv.smoothScrollToPosition(0);
                HelperFunctions.animate = true;
                Toast.makeText(this, "Sorted By Time", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if(id == R.id.sortitemsbyfavorites){
                Fragment fgT = getCurrentFragment();
                if(fgT instanceof MyFragment) {
                    HelperFunctions.animate = false;
                    MyFragment fg           = (MyFragment) fgT;
                    ObservableListView olv  = fg.listView;
                    List<Tweet> tList       = fg.tweetlist;
                    MyAdapter mya           = fg.tweetadapter;
                    fg.tempTweetList        = new ArrayList<Tweet>(fg.tweetlist);
                    HelperFunctions.sortTweets(2, fg.tempTweetList, mya, olv);
                    fg.tweetadapter.setTweets(fg.tempTweetList);
                    fg.tweetadapter.notifyDataSetChanged();
                    olv.smoothScrollToPosition(0);
                    Toast.makeText(this, "Sorted By Favorite Count", Toast.LENGTH_LONG).show();
                    HelperFunctions.animate = true;
                }
            return true;
        }
        else if(id == R.id.sortitemsbytweet){
            Fragment fgT = getCurrentFragment();
            if(fgT instanceof MyFragment) {
                HelperFunctions.animate = false;
                MyFragment fg           = (MyFragment) fgT;
                ObservableListView olv  = fg.listView;
                List<Tweet> tList       = fg.tweetlist;
                MyAdapter mya           = fg.tweetadapter;
                fg.tempTweetList        = new ArrayList<Tweet>(fg.tweetlist);
                HelperFunctions.sortTweets(1, fg.tempTweetList, mya, olv);
                fg.tweetadapter.setTweets(fg.tempTweetList);
                fg.tweetadapter.notifyDataSetChanged();
                olv.smoothScrollToPosition(0);
                Toast.makeText(this, "Sorted By Retweet Count", Toast.LENGTH_LONG).show();
                HelperFunctions.animate = true;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class LoadFriends extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                System.out.println("PRANJALUSERNAMEIS " + HelperFunctions.twitterStream.getScreenName());
            } catch (TwitterException e) {
                System.out.println("PRANJALUSERNAMEIS EXCEPTION");
                e.printStackTrace();
            }

            long nextCursor = -1;
            IDs friendIds   = null;
            do{
                ResponseList<User> followers = null;
                try {
                    friendIds = HelperFunctions.twitter.getFriendsIDs(nextCursor);
                    long arr[] = friendIds.getIDs();
                    int cur    = 0;

                    while(cur+100<arr.length) {
                        followers = HelperFunctions.twitter.lookupUsers(Arrays.copyOfRange(arr, cur, cur+100));
                        HelperFunctions.friends.addAll(followers);
                        for(User follower : followers) {
                            HelperFunctions.users.add(follower.getName());
                            System.out.println("FRIEND " + follower.getId() + " " + follower.getScreenName() + " " + follower.getName());
                        }
                        cur = cur+100;
                    }
                    followers = HelperFunctions.twitter.lookupUsers(Arrays.copyOfRange(arr, cur, arr.length));
                    for(User follower : followers) {
                        HelperFunctions.friends.addAll(followers);
                        System.out.println("FRIEND " + follower.getId() + " " + follower.getScreenName() + " " + follower.getName());
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }while((nextCursor = friendIds.getNextCursor()) != 0);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private static final UserStreamListener listener = new UserStreamListener() {
        @Override
        public void onStatus(Status status) {
            //TweetBank.insertTweet(t);
            System.out.println("onStatus @" + status.getUser().getScreenName() + " - " + status.getText());
            String statusJson = TwitterObjectFactory.getRawJSON(status);
            System.out.println("rawjson "+statusJson);
            Tweet updatedTweet  = HelperFunctions.gson.fromJson(statusJson, Tweet.class);
            System.out.println("rawjson updateTweet is "+updatedTweet.text);
            TweetBank.insertTweet(updatedTweet);
        }

        @Override
        public void onFriendList(long[] friendIds) {

        }

        @Override
        public void onFavorite(User source, User target, Status favoritedStatus) {
            System.out.println("PRANJALUSERNAMEIS favorite some tweet");

        }

        @Override
        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {

        }

        @Override
        public void onFollow(User source, User followedUser) {
            System.out.println("PRANJALUSERNAMEIS followed someone");

        }

        @Override
        public void onUnfollow(User source, User unfollowedUser) {

        }

        @Override
        public void onDirectMessage(DirectMessage directMessage) {

        }

        @Override
        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {

        }

        @Override
        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {

        }

        @Override
        public void onUserListCreation(User listOwner, UserList list) {

        }

        @Override
        public void onUserListUpdate(User listOwner, UserList list) {

        }

        @Override
        public void onUserListDeletion(User listOwner, UserList list) {

        }

        @Override
        public void onUserProfileUpdate(User updatedUser) {

        }

        @Override
        public void onBlock(User source, User blockedUser) {

        }

        @Override
        public void onUnblock(User source, User unblockedUser) {

        }

        @Override
        public void onException(Exception ex) {
            System.out.println("PRANJALUSERNAMEIS exception");
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onDeletionNotice(long directMessageId, long userId) {
            System.out.println("Got a direct message deletion notice id:" + directMessageId);
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            System.out.println("Got a track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("Got stall warning:" + warning);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpagertab);

        HelperFunctions.TITLES.add(0, "TimeLine");
        HelperFunctions.TITLES.add(1, "Verified");
        HelperFunctions.TITLES.add(2, "Trending");

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        TweetBank.init(this.getApplicationContext());
    //    TweetBank.sqlitehelper.clearDb(TweetBank.WriteAbleDB);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        baseContext = this.getApplication().getBaseContext();

        HelperFunctions.authConfig = new TwitterAuthConfig(Keys.TWITTER_KEY, Keys.TWITTER_SECRET);
        Fabric.with(this, new Twitter(HelperFunctions.authConfig));
        Fabric.with(this, new TweetUi());
        Fabric.with(this, new TweetComposer());

        HelperFunctions.currentSession = Twitter.getSessionManager().getActiveSession();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ConfigurationBuilder config = new ConfigurationBuilder();
        config.setJSONStoreEnabled(true);
        config.setOAuthConsumerKey(Keys.TWITTER_KEY);
        config.setOAuthConsumerSecret(Keys.TWITTER_SECRET);
        config.setOAuthAccessToken(HelperFunctions.currentSession.getAuthToken().token);
        config.setOAuthAccessTokenSecret(HelperFunctions.currentSession.getAuthToken().secret);

        Configuration cf        = config.build();
        HelperFunctions.twitter = new TwitterFactory(cf).getInstance();

        System.out.println("PRANJALUSERNAMEIS YOYO");

        HelperFunctions.twitterStream = new TwitterStreamFactory(cf).getInstance();
        HelperFunctions.twitterStream.addListener(listener);
        // user() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        HelperFunctions.twitterStream.user();
        //twitterStream.
        /*try {
            System.out.println("PRANJALUSERNAMEIS " + twitterStream.getScreenName());
        } catch (TwitterException e) {
            System.out.println("PRANJALUSERNAMEIS EXCEPTION");
            e.printStackTrace();
        }*/

        //new LoadFriends().execute("0", "1");

        username = HelperFunctions.currentSession.getUserName();

        HelperFunctions.twitterApiClient = new MyTwitterApiClient(HelperFunctions.currentSession); //TwitterCore.getInstance().getApiClient(currentSession);
        HelperFunctions.accountService   = HelperFunctions.twitterApiClient.getAccountService();
        HelperFunctions.statusesService  = HelperFunctions.twitterApiClient.getStatusesService();
        HelperFunctions.favoriteService  = HelperFunctions.twitterApiClient.getFavoriteService();
        HelperFunctions.searchService    = HelperFunctions.twitterApiClient.getSearchService();

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.underlinecolor));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        // When the page is selected, other fragments' scrollY should be adjusted
        // according to the toolbar status(shown/hidden)
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                propagateToolbarState(toolbarIsShown());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        propagateToolbarState(toolbarIsShown());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //System.out.println("ViewPagerTabListViewActivity onScrollChanged dragging "+dragging+" firstScroll "+firstScroll+" scrollY "+scrollY);

        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
        //System.out.println("ViewPagerTabListViewActivity onDownMotionEvent");

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //System.out.println("ViewPagerTabListViewActivity onUpOrCancelMotionEvent ScrollState "+scrollState);

        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }

        int toolbarHeight = mToolbarView.getHeight();
        final ObservableListView listView = (ObservableListView) view.findViewById(R.id.mylist);
        if (listView == null) {
            return;
        }

        TextView tv;

        int scrollY = listView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                propagateToolbarState(toolbarIsShown());
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private Fragment getCurrentFragment() {
        Fragment fg = mPagerAdapter.getItemAt(mPager.getCurrentItem());

        if(fg instanceof MyImageFragment) {
            MyImageFragment mif = (MyImageFragment) fg;
            return mif;
        }
        else if(fg instanceof TrendingFragment){
            TrendingFragment mif = (TrendingFragment) fg;
            return mif;
        }
        else{
            MyFragment mf = (MyFragment) fg;
            return mf;
        }
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();

        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            Fragment f = mPagerAdapter.getItemAt(i);
            if (f == null ){//|| f instanceof MyImageFragment) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            ObservableListView listView;

            //if(f instanceof  MyFragment)
                listView = (ObservableListView) view.findViewById(R.id.mylist);
            //else
             //   listView = (ObservableListView) view.findViewById(R.id.newsobservableview);

            if (isShown) {
                // Scroll up
                if (0 < listView.getCurrentScrollY()) {
                    listView.setSelection(0);
                }
            } else {
                // Scroll down (to hide padding)
                if (listView.getCurrentScrollY() < toolbarHeight) {
                    listView.setSelection(1);
                }
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    //private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private int mScrollY;

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        protected Fragment createItem(int position) {
            Bundle b     = new Bundle();
            b.putInt("position", position);

            if (position == 0)
                b.putBoolean("filter", false);
            else
                b.putBoolean("filter", true);

            if (0 < mScrollY) {
                b.putInt(ViewPagerTabListViewFragment.ARG_INITIAL_POSITION, 1);
            }

            Fragment f = HelperFunctions.getFragment(position, b);

            if(position == 2)
                f = new TrendingFragment();
            return f;
        }

        @Override
        public int getCount() {
            return HelperFunctions.TITLES.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return HelperFunctions.TITLES.get(position);
        }
    }

}
