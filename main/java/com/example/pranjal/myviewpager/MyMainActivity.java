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
import android.support.v7.app.AppCompatActivity;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import io.fabric.sdk.android.Fabric;
import twitter4j.TwitterFactory;


public class MyMainActivity extends AppCompatActivity {

    MyTwitterApiClient  twitterApiClient;
    String username                  = null;

    TwitterFactory twitterFactory;
    StatusesService statusesService;
    AccountService accountService;
    FavoriteService favoriteService;
    TwitterAuthConfig   authConfig     = null;
    TwitterSession      currentSession = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String TWITTER_KEY = "i8lsarVzM1RLdQli7JvGibJya";
        String TWITTER_SECRET = "ivA141Pewjx3VYfKOUBMIRJZZnNhPQNW9gVdM1nlXrnsNmir29";

        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        MyApplication appState = ((MyApplication) getApplicationContext());
        currentSession = Twitter.getSessionManager().getActiveSession();
        appState.currentSession = currentSession;

        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Fabric.with(this, new TweetUi());
        Fabric.with(this, new TweetComposer());

        username = currentSession.getUserName();

        twitterApiClient = new MyTwitterApiClient(currentSession); //TwitterCore.getInstance().getApiClient(currentSession);
        accountService   = twitterApiClient.getAccountService();
        statusesService  = twitterApiClient.getStatusesService();
        favoriteService  = twitterApiClient.getFavoriteService();

        appState.accountService   = accountService;
        appState.favoriteService  = favoriteService;
        appState.statusesService  = statusesService;
        appState.twitterApiClient = twitterApiClient;
        appState.authConfig       = authConfig;
        appState.currentSession   = currentSession;
        appState.username         = username;

        if(appState.statusesService == null)
            System.out.println("PRANJAL IT IS NULL CHECK IT1a");
        if(appState.favoriteService == null)
            System.out.println("PRANJAL IT IS NULL CHECK IT2a");


        if (currentSession == null) {
            System.out.println("NULL POINTER EXCEPTION");
            Intent intent = new Intent(MyMainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appState.startActivity(intent);
        } else {
            System.out.println("Pranjal testing");
            Intent intent = new Intent(MyMainActivity.this, ViewPagerTabListViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appState.startActivity(intent);
        }

    }
}