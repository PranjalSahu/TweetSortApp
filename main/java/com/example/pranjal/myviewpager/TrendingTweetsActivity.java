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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;


public class TrendingTweetsActivity extends BaseActivity {

    LinearLayout linlaHeaderProgress;
    ObservableListView listView;
    MyAdapter      tweetadapter;
    List<Tweet> tweetlist;
    private View mHeaderView;
    private View mToolbarView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
            case 0:
                setResult(0);
                finish();
        }
    }

    void setmydata(ListView listView, View headerView){
        listView.addHeaderView(headerView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trending_tweets);

        listView            = (ObservableListView) findViewById(R.id.mylist);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setBackgroundColor(-1);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().hide();

        //getSupportActionBar().setLogo(R.drawable.circle_twitter);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbarView = findViewById(R.id.toolbar);

        //mToolbarView.
        tweetadapter    = new MyAdapter(this);
        listView        = (ObservableListView) findViewById(R.id.mylist);

        //setmydata(listView, this.getLayoutInflater().inflate(R.layout.padding, listView, false));
        listView.setAdapter(tweetadapter);
        mToolbarView = findViewById(R.id.toolbar);

        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));

        tweetlist = new ArrayList<Tweet>();
        LoadTrendingTweets();
    }

    public void displayTweets(){
        tweetadapter.setTweets(tweetlist);
        tweetadapter.notifyDataSetChanged();
        linlaHeaderProgress.setVisibility(View.GONE);
    }

    public void LoadTrendingTweets() {
        HelperFunctions.searchService.tweets("#LiesMenTell", null, null, null, "mixed", 80, null, null, null, true,
                new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        List<Tweet> ls = result.data.tweets;
                        if (ls.size() > 0) {
                            tweetlist.addAll(ls);
                        }
                        displayTweets();
                    }

                    @Override
                    public void failure(TwitterException e) {

                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.mymenutrend, menu);
        System.out.println("pranjal menu has visible items " + menu.hasVisibleItems());
        return true;
    }
}
