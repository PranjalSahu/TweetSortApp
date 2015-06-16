package com.example.pranjal.myviewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity {

    TwitterSession    currentSession = null;
    TwitterAuthConfig authConfig     = null;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setRefreshActionButtonState(boolean refreshing) {
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewpagertab);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        String TWITTER_KEY    = "i8lsarVzM1RLdQli7JvGibJya";
         String TWITTER_SECRET = "ivA141Pewjx3VYfKOUBMIRJZZnNhPQNW9gVdM1nlXrnsNmir29";

        authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        MyApplication appState = ((MyApplication) getApplicationContext());
        currentSession         = Twitter.getSessionManager().getActiveSession();
        appState.currentSession = currentSession;

        if (currentSession == null) {
            System.out.println("NULL POINTER EXCEPTION");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appState.startActivity(intent);
        }
        else {
            System.out.println("Pranjal testing");
            Intent intent = new Intent(MainActivity.this, ViewPagerTabListViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appState.startActivity(intent);
        }
    }
}
