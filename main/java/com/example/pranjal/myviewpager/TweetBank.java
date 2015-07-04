package com.example.pranjal.myviewpager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pranjal on 04/07/15.
 */
public class TweetBank {
    static Long firsttweetid     = null;
    static Long lasttweetid      = null;
    static List<Tweet> tweetlist = null;

    public static MySQLiteHelper sqlitehelper = null;
    public static SQLiteDatabase WriteAbleDB  = null;
    public static SQLiteDatabase ReadAbleDB   = null;


    public static void init(Context ct){
        sqlitehelper = new MySQLiteHelper(ct);
        WriteAbleDB  = sqlitehelper.getWritableDatabase();
        ReadAbleDB   = sqlitehelper.getReadableDatabase();
        tweetlist = new ArrayList<Tweet>();
    }

}
