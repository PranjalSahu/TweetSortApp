package com.example.pranjal.myviewpager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

        /*final Activity activity = this;
        final Window w = activity.getWindow();

        final View content = activity.findViewById(android.R.id.content).getRootView();
        if (content.getWidth() > 0) {
            Bitmap image = BlurBuilder.blur(content);
            w.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
        } else {
            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Bitmap image = BlurBuilder.blur(content);
                    w.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), image));
                }
            });
        }
        */

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress1);
        tweetLayout         = (LinearLayout) findViewById(R.id.tweetlinearlayout);

        linlaHeaderProgress.setBackgroundColor(-1);
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                                    LinearLayout.LayoutParams.WRAP_CONTENT);

    }
}
