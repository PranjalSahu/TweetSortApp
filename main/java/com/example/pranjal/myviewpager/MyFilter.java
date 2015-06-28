package com.example.pranjal.myviewpager;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

/**
 * Created by pranjal on 21/06/15.
 */
public class MyFilter {
    public static boolean checkit(Tweet t){
        return t.user.verified;
    }

    public static List<Tweet> getFilteredList(List<Tweet> tList){
        for(int i=0;i< tList.size();++i){
            if(!checkit(tList.get(i)))
                tList.remove(i);
        }
        return tList;
    }
}
