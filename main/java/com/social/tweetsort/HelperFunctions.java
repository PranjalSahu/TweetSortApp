package com.social.tweetsort;

import android.os.Bundle;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.AccountService;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import twitter4j.TwitterStream;
import twitter4j.User;

/**
 * Created by pranjal on 29/04/15.
 */
public class HelperFunctions {

    public static boolean animate = true;                  // if 0 then don't animate else animate

    public static ArrayList<String> TITLES = new ArrayList<String>();
    public static ArrayList<User> friends  = new ArrayList<User>();
    public static ArrayList<String> users  = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> filterList = new ArrayList<ArrayList<String>>();

    public static ArrayList<MyFragment> fragments  = new ArrayList<MyFragment>();
    static{
        fragments.add(0, null);
        fragments.add(1, null);
        fragments.add(2, null);
    }


    public static twitter4j.Twitter twitter;
    public static TwitterStream twitterStream;
    public static MyTwitterApiClient  twitterApiClient;
    public static StatusesService statusesService;
    public static AccountService accountService;
    public static FavoriteService favoriteService;
    public static SearchService searchService;

    SearchService ss;

    public static TwitterAuthConfig authConfig     = null;
    public static TwitterSession currentSession = null;

    public static Gson gson = new Gson();

    public static boolean checkit(Tweet t){
        return t.user.verified;
    }

    public static MyFragment getFragment(int position, Bundle b){
//            if(fragments.get(position) != null) {
//                System.out.println("pranjalsahu returning old fragment");
//                return fragments.get(position);
//            }
//            else {
                MyFragment myfg = new MyFragment();
                myfg.setArguments(b);
                fragments.add(position, myfg);
                return myfg;
            //}
    }

    public static boolean genericFilterFunction(Tweet t, int position){
        if(position == 0 || position == 2)
            return true;
        if(position == 1)                       // for verified tweets
            return t.user.verified;

        System.out.println("YOYO "+t.user.name);
        boolean flag = false;
        ArrayList<String> userList = filterList.get(position);
        for(int i=0;i<userList.size();++i){
            if(t.user.name.contains(userList.get(i))) {
                System.out.println("PRANJAL match username : "+t.user.name+" userlist : "+userList.get(i));
                return true;
            }
        }
        return flag;
    }


    public static List<Tweet> getFilteredList(List<Tweet> tList){
        List<Tweet> resultList = new ArrayList<Tweet>();
        for(int i=0;i< tList.size();++i){
            if(checkit(tList.get(i)))
                resultList.add(tList.get(i));
        }
        return resultList;
    }

    //helper method to disable subviews
    public static void disableViewAndSubViews(ViewGroup layout) {

//        layout.setEnabled(false);
//        layout.setClickable(false);
//        layout.setLongClickable(false);
//
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            View child = layout.getChildAt(i);
//
//            if (child instanceof ViewGroup) {
//                disableViewAndSubViews((ViewGroup) child);
//            } else {
//
//                if(child instanceof TextView) {
//                    TextView tmp = ((TextView) child);
//                    return;
//                }
//                if(child instanceof ImageView){
//                    ImageView tmp = ((ImageView) child);
//                    //System.out.println("pranjaldisable : checking ImageView " + child.getId());
//                    //return;
//                }
//                child.setEnabled(false);
//                child.setClickable(false);
//                child.setLongClickable(false);
//            }
//        }
    }

    public static void sortTweets(int type,  List<Tweet> tweetlist, MyAdapter tweetadapter){
        if(type == 1)
            Collections.sort(tweetlist, comparatorTweetCount);
        else if(type == 2)
            Collections.sort(tweetlist, comparatorFavoriteCount);
        else if(type == 3)
            Collections.sort(tweetlist, comparatorId);

        if(tweetadapter != null)
            tweetadapter.notifyDataSetChanged();
        //lv.setSelectionAfterHeaderView();
        //lv.smoothScrollToPosition(0);

    }

    public static void sortTweets(int type,  List<Tweet> tweetlist, MyAdapter tweetadapter, ObservableListView lv){
        if(tweetlist == null || tweetlist.size() <= 0)
            return;

        if(type == 1)
            Collections.sort(tweetlist, comparatorTweetCount);
        else if(type == 2)
            Collections.sort(tweetlist, comparatorFavoriteCount);
    }

    public static Comparator<Tweet> comparatorId = new Comparator<Tweet>() {
        @Override
        public int compare(Tweet lhs, Tweet rhs) {
            if(lhs.id > rhs.id)
                return -1;
            else if(lhs.id == rhs.id)
                return 0;
            else
                return 1;
        }
    };

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
