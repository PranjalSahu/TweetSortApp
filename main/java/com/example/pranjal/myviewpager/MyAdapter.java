package com.example.pranjal.myviewpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.FavoriteService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.BaseTweetView;
import com.twitter.sdk.android.tweetui.TweetViewAdapter;

import java.util.List;

/**
 * Created by pranjal on 29/04/15.
 */

public class MyAdapter extends TweetViewAdapter {

    StatusesService statusesService = null;
    FavoriteService favoriteService = null;


    public MyAdapter(Context context, StatusesService ss, FavoriteService fs){
        super(context);
        statusesService = ss;
        favoriteService = fs;
    }

    private void updateTweet(Tweet updateTweet){
        int pos = 0;
        //R.anim.
        List<Tweet> tl =  this.getTweets();

        for(Tweet t:tl){
            if(t.id == updateTweet.id)
                break;
            ++pos;
        }

        tl.set(pos, updateTweet);
        this.setTweets(tl);
        System.out.println("tweet updated " + updateTweet.id + " old: " + this.getTweetAtPosition(pos).favorited + " new: " + updateTweet.favorited);

        this.notifyDataSetChanged();

        System.out.println("tweet updated checking "+this.getTweetAtPosition(pos).id+" "+this.getTweetAtPosition(pos).favorited);
        return;
    }

    private void updateTweet(Tweet temp, int a){
        int pos = 0;
        List<Tweet> tl =  this.getTweets();

        for(Tweet t:tl){
            if(t.id == temp.id)
                break;
            ++pos;
        }

        tl.set(pos, temp);

        //System.out.println("pranjalsahuretweeted: "+temp.id);
        this.setTweets(tl);
        this.notifyDataSetChanged();
        return;
    }

    private void updateTweet(long id){
        statusesService.lookup(Long.toString(id), false, false, false, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                updateTweet(result.data.get(0), 0);
            }

            @Override
            public void failure(TwitterException e) {

            }
        });
        return;
    }

    private void getFollowers(){

        //String encodedSearch = URLEncoder.encode(searchTerm, "UTF-8");

    }

    private Tweet getTweetAtPosition(int position){
        return this.getItem(position);
    }

    //helper method to disable subviews
    private void disableViewAndSubViews(ViewGroup layout) {

        layout.setEnabled(false);
        layout.setClickable(false);
        layout.setLongClickable(false);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            if (child instanceof ViewGroup) {
                disableViewAndSubViews((ViewGroup) child);
            } else {

                if(child instanceof TextView) {
                    TextView tmp = ((TextView) child);
                    return;
                }
                if(child instanceof ImageView){
                    ImageView tmp = ((ImageView) child);
                    //System.out.println("pranjaldisable : checking ImageView " + child.getId());
                    //return;
                }
                child.setEnabled(false);
                child.setClickable(false);
                child.setLongClickable(false);
            }
        }
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        Object rowView    = convertView;
        final Tweet tweet = this.getItem(position);

        System.out.println("getview "+tweet.idStr+" "+tweet.favorited);

        if(convertView == null) {
            rowView          = this.getTweetView(this.context, tweet);

            final ImageButton iv1      = new ImageButton(this.context);
            final ImageButton iv2  = new ImageButton(this.context);
            final ImageButton iv3  = new ImageButton(this.context);

            iv1.setBackgroundColor(0);
            iv2.setBackgroundColor(0);
            iv3.setBackgroundColor(0);

            iv1.setTag(tweet);
            iv1.setTag(R.string.tweetposition, position);

            iv2.setTag(tweet);
            iv2.setTag(R.string.tweetposition, position);
            if (!tweet.retweeted) {
                iv2.setImageResource(R.drawable.retweet);
            }
            else {
                iv2.setImageResource(R.drawable.retweet_on);
                //System.out.println("pranjalupdate retweeton "+tweet.id);
            }

            iv3.setTag(tweet);
            iv3.setTag(R.string.tweetposition, position);
            if (!tweet.favorited) {
                iv3.setImageResource(R.drawable.favorite);
                //System.out.println("1pranjalupdate favorite " + tweet.id);
            }
            else {
                iv3.setImageResource(R.drawable.favorite_on);
                //System.out.println("1pranjalupdate favoriteon " + tweet.id);
            }

            iv1.setImageResource(R.drawable.abc_item_background_holo_dark);

            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tempTweet = (Tweet) v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();
                }
            });

            iv2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet)v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if(!tempTweet.retweeted) {

                        statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                //updateTweet(result.data);
                                iv2.setTag(tempTweet.id);

                                Toast.makeText(context, "Retweet done "+result.data.retweeted, Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void failure(TwitterException e) {
                                Toast.makeText(context, "Retweet Not done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        statusesService.destroy(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                updateTweet(result.data);
                                iv2.setTag(result.data);
                                iv2.setImageResource(R.drawable.retweet);
                                Toast.makeText(context, "UnRetweet done", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(TwitterException e) {
                                Toast.makeText(context, "UnRetweet Not done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });


            iv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tempTweet = (Tweet)v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if(!tempTweet.favorited) {
                        favoriteService.create(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                updateTweet(result.data);
                                iv3.setTag(result.data);
                                iv3.setImageResource(R.drawable.favorite_on);

                                Toast.makeText(context, "Favorite Done "+result.data.favorited, Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void failure(TwitterException e) {
                                Toast.makeText(context, "Favorite Not Done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        favoriteService.destroy(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                updateTweet(result.data);
                                iv3.setTag(result.data);
                                iv3.setImageResource(R.drawable.favorite);
                                Toast.makeText(context, "UnFavorite Done", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void failure(TwitterException e) {
                                Toast.makeText(context, "UnFavorite Not Done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });


            LinearLayout lv1 = new LinearLayout(this.context, null);
            lv1.setOrientation(LinearLayout.VERTICAL);

            //lv1.removeAllViews();

            lv1.addView((View) rowView, 0);

            LinearLayout lv2 = new LinearLayout(this.context, null);
            lv2.setOrientation(LinearLayout.HORIZONTAL);
            lv2.setBackgroundColor(-1);

            lv2.addView((View) iv1, 0);
            lv2.addView((View) iv2, 1);
            lv2.addView((View) iv3, 2);

            lv1.addView((View) lv2, 1);

            rowView = (View)lv1;

            //disable subviews to avoid links are clickable
            if(rowView instanceof ViewGroup){
                System.out.println("Disabling views while creating view");
                disableViewAndSubViews((ViewGroup) rowView);
            }

            //enable root view and attach custom listener
            ((BaseTweetView)(((LinearLayout) rowView)
                    .getChildAt(0)))
                    .setEnabled(true);

            ((BaseTweetView)(((LinearLayout) rowView)
                    .getChildAt(0)))
                    .setTag(tweet);

            /*((BaseTweetView)(((LinearLayout) rowView)
                    .getChildAt(0)))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Tweet tempTweet = (Tweet) v.getTag();
                            Intent intent = new Intent(v.getContext(), ShowTweet.class);
                            intent.putExtra("tweetid", tempTweet.id);
                            context.startActivity(intent);
                        }
                    });
            */
            for(int i=0;i<3;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setEnabled(true);
            }

            final ImageButton child1 = (ImageButton)((LinearLayout)(((LinearLayout) rowView)
                    .getChildAt(1)))
                    .getChildAt(1);

            final ImageButton child2 = (ImageButton)((LinearLayout)(((LinearLayout) rowView)
                    .getChildAt(1)))
                    .getChildAt(2);

            child2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tempTweet = (Tweet) v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if (tempTweet.favorited) {
                        Toast.makeText(context, "Favorite Already Done ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    favoriteService.create(tempTweet.id, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            updateTweet(result.data);
                            child2.setTag(result.data);
                            child2.setImageResource(R.drawable.favorite_on);
                        }

                        @Override
                        public void failure(TwitterException e) {
                            Toast.makeText(context, "Favorite Not Done " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            child1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet) v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            updateTweet(tempTweet.id);
                            child1.setTag(result.data);
                            child1.setImageResource(R.drawable.retweet_on);
                            Toast.makeText(context, "Retweet done1 " + result.data.retweeted, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            Toast.makeText(context, "Retweet Not done1", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });


            /*for(int i=0;i<3;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tweet tempTweet = (Tweet) v.getTag();
                                String tweetId = "click tweetId:" + tempTweet.idStr;
                                Toast.makeText(context, tweetId, Toast.LENGTH_SHORT).show();
                            }
                        });
            }*/



        } else {

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setTweet(tweet);

            for(int i=0;i<3;++i) {
                View temp = (View)((LinearLayout) (((LinearLayout) convertView).getChildAt(1)))
                        .getChildAt(i);
                    temp.setTag(tweet);
            }

            //disable subviews to avoid links are clickable
            if(convertView instanceof ViewGroup){
                disableViewAndSubViews((ViewGroup) convertView);
            }

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setEnabled(true);

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setTag(tweet);

            /*((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Tweet tempTweet = (Tweet) v.getTag();
                            Intent intent = new Intent(v.getContext(), ShowTweet.class);
                            intent.putExtra("tweetid", tempTweet.id);
                            context.startActivity(intent);
                        }
                    });
               */
            for(int i=0;i<3;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setEnabled(true);
            }

            final ImageButton child1 = (ImageButton)((LinearLayout)(((LinearLayout) rowView)
                    .getChildAt(1)))
                    .getChildAt(1);

            final ImageButton child2 = (ImageButton)((LinearLayout)(((LinearLayout) rowView)
                    .getChildAt(1)))
                    .getChildAt(2);


            //Tweet temp1 = (Tweet)child1.getTag();
            //Tweet temp2 = (Tweet)child2.getTag();

            if(tweet.retweeted)
                child1.setImageResource(R.drawable.retweet_on);
            else
                child1.setImageResource(R.drawable.retweet);

            if(tweet.favorited) {
                child2.setImageResource(R.drawable.favorite_on);
                System.out.println("0pranjalupdate favoriteon " + tweet.id);
            }
            else {
                System.out.println("0pranjalupdate favorite "+tweet.id);
                child2.setImageResource(R.drawable.favorite);
            }

            /*for(int i=0;i<3;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Tweet tempTweet = (Tweet) v.getTag();
                                String tweetId = "click tweetId1:" + tempTweet.idStr;
                                Toast.makeText(context, tweetId, Toast.LENGTH_SHORT).show();
                            }
                        });
            }*/


           child2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Tweet tempTweet = (Tweet) v.getTag();
                   Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                   if(!tempTweet.favorited) {
                       favoriteService.create(tempTweet.id, false, new Callback<Tweet>() {
                           @Override
                           public void success(Result<Tweet> result) {
                               updateTweet(result.data);
                               child2.setTag(result.data);
                               child2.setImageResource(R.drawable.favorite_on);
                               System.out.println("5pranjalupdate favoriteon " + result.data.id);
                               Toast.makeText(context, "Favorite Done " + result.data.favorited, Toast.LENGTH_LONG).show();
                           }

                           @Override
                           public void failure(TwitterException e) {
                               Toast.makeText(context, "Favorite Not Done", Toast.LENGTH_LONG).show();
                           }
                       });
                   }
                   else{

                   }
               }
           });

            child1.setOnClickListener(new View.OnClickListener() {//myOnClickListener(tweet) {
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet) v.getTag();
                    Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            child1.setImageResource(R.drawable.retweet_on);
                            System.out.println("pranjalupdate retweet " + result.data.id);
                            child1.setTag(result.data);
                            child2.setTag(result.data);
                            updateTweet(tempTweet.id);
                            Toast.makeText(context, "Retweet done "+result.data.retweeted, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            Toast.makeText(context, "Retweet Not done", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }

        return (View)rowView;
    }
}



/*
            TextView tv = new TextView(this.context);
            tv.setHeight(50);
            tv.setWidth(50);
            tv.setBackgroundColor(-1);
            tv.setTextColor(Color.BLUE);
            */