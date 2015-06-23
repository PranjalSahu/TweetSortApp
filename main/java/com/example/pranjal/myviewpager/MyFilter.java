package com.example.pranjal.myviewpager;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by pranjal on 21/06/15.
 */
public class MyFilter {
    public static boolean checkit(Tweet t){

        return t.user.verified;
    }
}
