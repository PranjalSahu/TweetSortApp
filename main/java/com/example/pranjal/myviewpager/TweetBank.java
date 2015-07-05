package com.example.pranjal.myviewpager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by pranjal on 04/07/15.
 */
public class TweetBank {
    static Long firsttweetid     = null;
    static Long lasttweetid      = null;
    static List<Tweet> tweetlist = null;

    public static MySQLiteHelper sqlitehelper = null;
    public static SQLiteDatabase WriteAbleDB  = null;
    static TreeMap<Long, Tweet> tweetmap      = null;

    public static List<String> getAllImageUrls(){
        List<String> tempList = new ArrayList<String>();
        for(Tweet t: tweetlist){
            if(MyFilter.checkit(t) && t.entities != null &&  t.entities.media != null &&  t.entities.media.size() > 0)
                tempList.add(t.entities.media.get(0).mediaUrl);
        }
        return tempList;
    }


    public static List<Tweet> getAllTweets(){
        List<Tweet> tempList = new ArrayList<Tweet>(tweetlist);
        return tempList;
    }

    public static List<Tweet> getOlderThan(Long id){
        List<Tweet> tempList  = new ArrayList<Tweet>();
        for (Tweet t: tweetlist) {         // TODO make it binary search
            //t.entities.media.
            if(t.id < id)
                tempList.add(t);
        }
        return tempList;
    }

    public static List<Tweet> getNewThan(Long id){
        List<Tweet> tempList  = new ArrayList<Tweet>();
        for (Tweet t: tweetlist) {         // TODO make it binary search
            //t.entities.media.
            if(t.id > id)
                tempList.add(t);
        }
        return tempList;
    }

    public static void insertTweet(Tweet t){
        if(firsttweetid == null)
            firsttweetid = t.id;
        if(lasttweetid == null)
            lasttweetid = t.id;

        if(tweetmap.get(t.id) == null) {
            tweetmap.put(t.id, t);
            tweetlist.add(t);

            if(t.id > firsttweetid)
                firsttweetid = t.id;
            if(t.id < lasttweetid)
                lasttweetid = t.id;
        }
    }

    public static void init(Context ct){
        //sqlitehelper = new MySQLiteHelper(ct);
        //WriteAbleDB  = sqlitehelper.getWritableDatabase();
        //tweetlist = new ArrayList<Tweet>();
        tweetmap  = new TreeMap<Long, Tweet>();
        tweetlist = new ArrayList<Tweet>();
        //sqlitehelper.clearDb(WriteAbleDB);
    }
}
