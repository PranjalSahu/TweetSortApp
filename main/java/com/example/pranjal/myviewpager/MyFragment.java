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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubNativeAdRenderer;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.AccountService;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MyFragment extends BaseFragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY    = "i8lsarVzM1RLdQli7JvGibJya";
    private static final String TWITTER_SECRET = "ivA141Pewjx3VYfKOUBMIRJZZnNhPQNW9gVdM1nlXrnsNmir29";

    MyApplication appState;

    TwitterSession ts = null;
    LinearLayout        mLayout;

    List<Tweet> tweetlist;
    List<Tweet> tempTweetList;      // for storing in sorted order

    twitter4j.Twitter twitter1;
    TwitterFactory      twitterFactory;

    LinearLayout linlaHeaderProgress;
    MyAdapter         tweetadapter;
    MoPubAdAdapter mAdAdapter;


    Long              firsttweetid   = null;
    Long              lasttweetid    = null;
    boolean           loading        = false;
    SharedPreferences prefs          = null;
    ProgressBar headerProgress       = null;
    LinearLayout myLayout;

    MySQLiteHelper sqlitehelper      = null;
    private SQLiteDatabase WriteAbleDB;
    private SQLiteDatabase ReadAbleDB;

    String custkey      = "FacGCa1kekg6t68N9n1r46GAI";
    String custsecret   = "aQSljFzqIKuVu4H4sr9OQhvtEVW4sn1qRMHtJezZMiMKeOFlWo";
    String accesstoken  = "163158983-PcgEMJBfxFQBSK2JHcnKYfZhGTyPio6jt23z3FBh";
    String accesssecret = "BIf9DohxN21Y3jF1m3LP3JAgR2gA673Ywwe20QjVFyCnZ";

    StatusesService statusesService;
    AccountService accountService;
    FavoriteService favoriteService;
    ObservableListView listView;
    Context baseContext;

    SwipeRefreshLayout mSwipeLayout;
    Activity storedActivity;

    private RequestParameters mRequestParameters;
    private static final String MY_AD_UNIT_ID = "d05480af91a04d7c841c5f9bb7621032";

    boolean filterTweets;
    View footer;

    AbsListView.OnScrollListener listenerObject = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.mynativead)
                .mainImageId(R.id.tw__full_ad_image)
                .iconImageId(R.id.tw__tweet_author_avatar_pran)
                .titleId(R.id.tw__tweet_author_full_name_pran)
                .textId(R.id.tw__tweet_text_pran)
                        //.addExtra("sponsoredText", R.id.sponsored_text)
                        //.addExtra("sponsoredImage", R.id.sponsored_image)
                .build();


        MoPubNativeAdPositioning.MoPubServerPositioning adPositioning =
                MoPubNativeAdPositioning.serverPositioning();
        MoPubNativeAdRenderer adRenderer = new MoPubNativeAdRenderer(viewBinder);

        footer           = (View)activity.getLayoutInflater().inflate(R.layout.listview_footer_row, null);

        tweetadapter    = new MyAdapter(activity, this.statusesService, this.favoriteService);
        mAdAdapter      = new MoPubAdAdapter(activity, tweetadapter, adPositioning);
        mAdAdapter.registerAdRenderer(adRenderer);
        //mySetOnScrollListener(activity);

        storedActivity = activity;
        LoadTweets();
    }

    public void setAppState( Context baseContext, StatusesService statusesService,
            AccountService accountService,
            FavoriteService favoriteService) {
        this.statusesService  = statusesService;
        this.accountService   = accountService;
        this.favoriteService  = favoriteService;
        this.baseContext      = baseContext;

        if(baseContext == null)
            System.out.println("PRANJALITISNULLBASEa");

    }

    void setmydata(ListView listView, View headerView){
        listView.addHeaderView(headerView);
    }

    protected void setDummyDataWithHeader(ListView listView, View headerView) {
        listView.addHeaderView(headerView);
        setDummyData(listView); // testing git
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tweet_list, container, false);

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeLayout.setProgressViewOffset(false, 150, 200);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecent();
            }
        });


        Activity parentActivity = getActivity();
        Fabric.with(getActivity(), new TweetUi());

        Bundle bd = getArguments();
        if(bd != null && bd.getBoolean("filter"))
            filterTweets = true;
        else
            filterTweets = false;

        linlaHeaderProgress = (LinearLayout) view.findViewById(R.id.linlaHeaderProgress);
        listView            = (ObservableListView) view.findViewById(R.id.mylist);

        //listView.setBackgroundColor(getResources().getColor(R.color.mycolors));


        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

        mRequestParameters = new RequestParameters.Builder()
                //.location(location)
                .keywords("food")
                .desiredAssets(desiredAssets)
                .build();


        setmydata(listView, inflater.inflate(R.layout.padding, listView, false));

        listView.setAdapter(tweetadapter);

        linlaHeaderProgress.setBackgroundColor(-1);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(listView, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        listView.setSelection(initialPosition);
                    }
                });
            }

            // TouchInterceptionViewGroup should be a parent view other than ViewPager.
            // This is a workaround for the issue #117:
            // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
            listView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.root));

            listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        ConfigurationBuilder config =
                new ConfigurationBuilder()
                        .setOAuthConsumerKey(custkey)
                        .setOAuthConsumerSecret(custsecret)
                        .setOAuthAccessToken(accesstoken)
                        .setOAuthAccessTokenSecret(accesssecret);

        twitter1         = new TwitterFactory(config.build()).getInstance();
        tweetlist        = new ArrayList<Tweet>();
        return view;
    }

    public void mySetOnScrollListener(final Activity activity){

        if(listenerObject == null) {
            listenerObject = new AbsListView.OnScrollListener() {
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    int visibleThreshold = 5;
                    System.out.println("firstVisibleItem "+firstVisibleItem+" visibleItemCount "+visibleItemCount+" totalItemCount "+totalItemCount+" (totalItemCount - visibleItemCount) "+(totalItemCount - visibleItemCount)+" (firstVisibleItem + visibleThreshold) "+(firstVisibleItem + visibleThreshold));
                    if (loading == false && totalItemCount > 5 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        Toast.makeText(activity, "END REACHED", Toast.LENGTH_SHORT);
                        //mytweets();
                        System.out.println("pranjal tweet footer scroll");
                        loading = true;
                        //lv.addFooterView(footer);
                        //System.out.println("Footer View Added");
                    }
                }
            };
        }

        listView.setOnScrollListener(listenerObject);
    }

    void loadRecent(){
        System.out.println("LOAD recent called");

        statusesService.homeTimeline(10, firsttweetid, null, false, true, false, true,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> ls = result.data;
                        firsttweetid = ls.get(0).getId();
                        tweetlist.addAll(0, ls);
                        tweetadapter.setTweets(tweetlist);
                        listView.setAdapter(tweetadapter);
                        tweetadapter.notifyDataSetChanged();
                        mSwipeLayout.setRefreshing(false);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                    }
                }
        );
    }

    void mytweets() {
        //linlaHeaderProgress.setVisibility(View.VISIBLE);
        statusesService.homeTimeline(10, null, lasttweetid, false, true, false, true,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> ls = result.data;

                        for (int i = 1; i < ls.size(); ++i) {
                            Tweet t = ls.get(i);
                            tweetlist.add(t);
                            lasttweetid = t.getId();
                        }

                        tweetadapter.setTweets(tweetlist);
                        linlaHeaderProgress.setVisibility(View.GONE);

                        listView.setAdapter(tweetadapter);
                        tweetadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                        linlaHeaderProgress.setVisibility(View.GONE);
                    }
                }
        );
    }

    public void LoadTweets() {

        statusesService.homeTimeline(150, null, lasttweetid, false, true, false, true,
                new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        List<Tweet> ls = result.data;

                        for (int i = 1; i < ls.size(); ++i) {
                            Tweet t = ls.get(i);
                            if(firsttweetid != null)
                                firsttweetid = t.getId();

                            if((filterTweets && MyFilter.checkit(t)) || !filterTweets)
                                tweetlist.add(t);

                            lasttweetid = t.getId();
                        }
                        tweetadapter.setTweets(tweetlist);
                        tempTweetList = new ArrayList<Tweet>(tweetlist);

                        tweetadapter.notifyDataSetChanged();
                        mAdAdapter.loadAds(MY_AD_UNIT_ID, mRequestParameters);

                        /*mAdAdapter.setAdLoadedListener(new MoPubNativeAdLoadedListener() {
                            @Override
                            public void onAdLoaded(int i) {

                            }

                            @Override
                            public void onAdRemoved(int i) {

                            }
                        });*/

                        mySetOnScrollListener(storedActivity);
                        listView.addFooterView(footer);

                        listView.setAdapter(mAdAdapter);
                        linlaHeaderProgress.setVisibility(View.GONE);
                        System.out.println("TWEETS LOADED " + lasttweetid);
                        loading = false;
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        exception.printStackTrace();
                        System.out.println("EXCEPTION FAILED TWITTER");
                    }
                }
        );
    }
}
