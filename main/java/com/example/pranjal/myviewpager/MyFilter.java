package com.example.pranjal.myviewpager;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pranjal on 21/06/15.
 */
public class MyFilter {
    public static boolean checkit(Tweet t){
        return t.user.verified;
    }

    public static List<Tweet> getFilteredList(List<Tweet> tList){
        List<Tweet> resultList = new ArrayList<Tweet>();

        for(int i=0;i< tList.size();++i){
            if(checkit(tList.get(i)))
                resultList.add(tList.get(i));
        }

        return resultList;
    }
}
