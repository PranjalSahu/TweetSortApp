package com.example.pranjal.myviewpager;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pranjal on 29/04/15.
 */
public class HelperFunctions {

    public static void sortTweets(int type,  List<Tweet> tweetlist, MyAdapter tweetadapter){
        if(type == 1)
            Collections.sort(tweetlist, comparatorTweetCount);
        else if(type == 2)
            Collections.sort(tweetlist, comparatorFavoriteCount);

        tweetadapter.notifyDataSetChanged();
        //lv.setSelectionAfterHeaderView();
        //lv.smoothScrollToPosition(0);

    }

    public static void sortTweets(int type,  List<Tweet> tweetlist, MyAdapter tweetadapter, ObservableListView lv){
        if(type == 1)
            Collections.sort(tweetlist, comparatorTweetCount);
        else if(type == 2)
            Collections.sort(tweetlist, comparatorFavoriteCount);

        //tweetadapter.notifyDataSetChanged();
        //lv.setSelectionAfterHeaderView();
        //lv.smoothScrollToPosition(0);

    }

    public static Comparator<Tweet> comparatorTweetCount = new Comparator<Tweet>() {
        @Override
        public int compare(Tweet lhs, Tweet rhs) {
            if(lhs.retweetCount > rhs.retweetCount)
                return -1;
            else if(lhs.retweetCount == rhs.retweetCount)
                return 0;
            else
                return 1;
        }
    };

    public static Comparator<Tweet> comparatorFavoriteCount = new Comparator<Tweet>() {
        @Override
        public int compare(Tweet lhs, Tweet rhs) {
            if(lhs.favoriteCount > rhs.favoriteCount)
                return -1;
            else if(lhs.favoriteCount == rhs.favoriteCount)
                return 0;
            else
                return 1;
        }
    };



}
