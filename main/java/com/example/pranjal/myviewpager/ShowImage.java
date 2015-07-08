package com.example.pranjal.myviewpager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class ShowImage extends Activity {
    LinearLayout linlaHeaderProgress;
    LinearLayout tweetLayout;
    //ListView tweetLayout;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.my_tweet_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress1);
        tweetLayout         = (LinearLayout) findViewById(R.id.tweetlinearlayout);

        linlaHeaderProgress.setBackgroundColor(-1);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                    LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}
