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

package com.example.pranjal.myviewpager;

import android.content.Context;
import android.content.Intent;
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
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
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

    MyTwitterApiClient  twitterApiClient;

    private View mHeaderView;
    private View mToolbarView;
    private int  mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    static MyApplication appState;
    SlidingTabLayout slidingTabLayout;

    public static Context baseContext = null;

    String TWITTER_KEY    = "i8lsarVzM1RLdQli7JvGibJya";
    String TWITTER_SECRET = "ivA141Pewjx3VYfKOUBMIRJZZnNhPQNW9gVdM1nlXrnsNmir29";

    String username                  = null;

    private RadioGroup rg1;

    View footer;

    protected ImageLoader imageLoader;
    DisplayImageOptions options;


    private twitter4j.Twitter twitter;

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
                HelperFunctions.animate = true;
            }
            return true;
        }
        else if(id == R.id.addsegment){
            Intent intent = new Intent(ViewPagerTabListViewActivity.this, AddSegmentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 1);
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
        /*for(int i =0;i<3;++i) {
            MyFragment fg = (MyFragment)mPagerAdapter.getItemAt(0);
            if(fg !=  null){
                fg.setAppState(baseContext, statusesService, accountService, favoriteService);
            }
        }*/

        mPager.setAdapter(null);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        slidingTabLayout.setViewPager(mPager);
        mPager.setCurrentItem(HelperFunctions.TITLES.size()-1);
        //mPagerAdapter.notifyDataSetChanged();
    }

    public class LoadFriends extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            long nextCursor = -1;
            IDs friendIds   = null;
            do{
                ResponseList<User> followers = null;
                try {
                    friendIds = twitter.getFriendsIDs(nextCursor);
                    long arr[] = friendIds.getIDs();
                    int cur    = 0;

                    while(cur+100<arr.length) {
                        followers = twitter.lookupUsers(Arrays.copyOfRange(arr, cur, cur+100));
                        HelperFunctions.friends.addAll(followers);
                        for(User follower : followers) {
                            HelperFunctions.users.add(follower.getName());
                            System.out.println("FRIEND " + follower.getId() + " " + follower.getScreenName() + " " + follower.getName());
                        }
                        cur = cur+100;
                    }
                    followers = twitter.lookupUsers(Arrays.copyOfRange(arr, cur, arr.length));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpagertab);

        HelperFunctions.TITLES.add(0, "TimeLine");
        HelperFunctions.TITLES.add(1, "Verified");
        HelperFunctions.TITLES.add(2, "Images");

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        TweetBank.init(this.getApplicationContext());
    //    TweetBank.sqlitehelper.clearDb(TweetBank.WriteAbleDB);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        baseContext = this.getApplication().getBaseContext();

        HelperFunctions.authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(HelperFunctions.authConfig));
        Fabric.with(this, new TweetUi());
        Fabric.with(this, new TweetComposer());

        HelperFunctions.currentSession = Twitter.getSessionManager().getActiveSession();

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ConfigurationBuilder config = new ConfigurationBuilder();
        config.setOAuthConsumerKey(TWITTER_KEY);
        config.setOAuthConsumerSecret(TWITTER_SECRET);
        config.setOAuthAccessToken(HelperFunctions.currentSession.getAuthToken().token);
        config.setOAuthAccessTokenSecret(HelperFunctions.currentSession.getAuthToken().secret);

        twitter = new TwitterFactory(config.build()).getInstance();

        new LoadFriends().execute("0", "1");

        username = HelperFunctions.currentSession.getUserName();

        twitterApiClient = new MyTwitterApiClient(HelperFunctions.currentSession); //TwitterCore.getInstance().getApiClient(currentSession);
        HelperFunctions.accountService   = twitterApiClient.getAccountService();
        HelperFunctions.statusesService  = twitterApiClient.getStatusesService();
        HelperFunctions.favoriteService  = twitterApiClient.getFavoriteService();

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

            if(position == 2){
                MyImageFragment f = new MyImageFragment();
                return f;
            }
            else {
                MyFragment f = new MyFragment();
                if (position == 1) {
                    Bundle b = new Bundle();
                    b.putBoolean("filter", true);
                    f.setArguments(b);
                }

                if (0 < mScrollY) {
                    Bundle args = new Bundle();
                    args.putInt(ViewPagerTabListViewFragment.ARG_INITIAL_POSITION, 1);
                    f.setArguments(args);
                }

                return f;
            }
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
