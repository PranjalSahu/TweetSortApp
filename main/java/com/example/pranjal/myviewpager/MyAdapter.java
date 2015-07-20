package com.example.pranjal.myviewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    LayoutInflater inflater         = null;

    public MyAdapter(Context context, StatusesService ss, FavoriteService fs){
        super(context);
        statusesService = ss;
        favoriteService = fs;
        inflater = LayoutInflater.from(context);

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

        System.out.println("tweet updated checking " + this.getTweetAtPosition(pos).id + " " + this.getTweetAtPosition(pos).favorited);
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




    public View getView(final int position, View convertView, ViewGroup parent) {
        Object rowView    = convertView;
        final Tweet tweet = this.getItem(position);

        System.out.println("getview "+tweet.idStr+" "+tweet.favorited);

        if(convertView == null) {
            rowView          = this.getTweetView(this.context, tweet);
            View buttonsRow  = inflater.inflate(R.layout.buttons_row, parent, false);

            final ImageButton iv2 = (ImageButton)buttonsRow.findViewById(R.id.retweetimagebutton);
            final ImageButton iv3 = (ImageButton)buttonsRow.findViewById(R.id.favoriteimagebutton);
            final TextView     t1 = (TextView)buttonsRow.findViewById(R.id.retweetcounttext);
            final TextView     t2 = (TextView)buttonsRow.findViewById(R.id.favoritecounttext);

            iv2.setTag(tweet);
            iv3.setTag(tweet);

            t1.setText(Integer.toString(tweet.retweetCount));
            t2.setText(Integer.toString(tweet.favoriteCount));


            iv2.setBackgroundColor(0);
            iv3.setBackgroundColor(0);

            iv2.setTag(R.string.tweetposition, position);
            if(tweet.retweeted)
                iv2.setImageResource(R.drawable.retweet_on);

            iv3.setTag(R.string.tweetposition, position);
            if(tweet.favorited)
                iv3.setImageResource(R.drawable.favorite_on);

            iv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet) v.getTag();
                    //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if (!tempTweet.retweeted) {
                        statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                //updateTweet(result.data);
                                iv2.setTag(tempTweet.id);
                                //Toast.makeText(context, "Retweet done "+result.data.retweeted, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(context, "Retweet Not done", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        statusesService.destroy(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                updateTweet(result.data);
                                iv2.setTag(result.data);
                                iv2.setImageResource(R.drawable.retweet);
                                //Toast.makeText(context, "UnRetweet done", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(context, "UnRetweet Not done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });


            iv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tempTweet = (Tweet)v.getTag();
                    //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if(!tempTweet.favorited) {
                        //iv3.setImageResource(R.drawable.favorite_on);
                        favoriteService.create(tempTweet.id, false, new Callback<Tweet>() {
                            @Override
                            public void success(Result<Tweet> result) {
                                updateTweet(result.data);
                                iv3.setTag(result.data);
                                iv3.setImageResource(R.drawable.favorite_on);
                                //Toast.makeText(context, "Favorite Done "+result.data.favorited, Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(context, "Favorite Not Done", Toast.LENGTH_LONG).show();
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
                                //Toast.makeText(context, "UnFavorite Done", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void failure(TwitterException e) {
                                //Toast.makeText(context, "UnFavorite Not Done", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });

            LinearLayout lv1 = new LinearLayout(this.context, null);
            lv1.setOrientation(LinearLayout.VERTICAL);

            lv1.addView((View) rowView, 0);
            lv1.addView((View) buttonsRow, 1);

            rowView = (View)lv1;

           //disable subviews to avoid links are clickable
            if(rowView instanceof ViewGroup){
                System.out.println("Disabling views while creating view");
                HelperFunctions.disableViewAndSubViews((ViewGroup) rowView);
            }

            //enable root view and attach custom listener
            ((BaseTweetView)(((LinearLayout) rowView)
                    .getChildAt(0)))
                    .setEnabled(true);

            ((BaseTweetView)(((LinearLayout) rowView)
                    .getChildAt(0)))
                    .setTag(tweet);


            /*for(int i=0;i<5;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setEnabled(true);
            }*/

            View btnRow              = ((LinearLayout)(((LinearLayout) rowView).getChildAt(1)));
            final ImageButton child1 = (ImageButton)btnRow.findViewById(R.id.retweetimagebutton);
            final ImageButton child2 = (ImageButton)btnRow.findViewById(R.id.favoriteimagebutton);

            child2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Tweet tempTweet = (Tweet) v.getTag();
                    //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    if (tempTweet.favorited) {
                        //Toast.makeText(context, "Favorite Already Done ", Toast.LENGTH_LONG).show();
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
                            //Toast.makeText(context, "Favorite Not Done " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            child1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet) v.getTag();
                    //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            updateTweet(tempTweet.id);
                            child1.setTag(result.data);
                            child1.setImageResource(R.drawable.retweet_on);
                            //Toast.makeText(context, "Retweet done1 " + result.data.retweeted, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            //Toast.makeText(context, "Retweet Not done1", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

            child1.setEnabled(true);
            child2.setEnabled(true);
            t1.setEnabled(true);
            t2.setEnabled(true);

        } else {

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setTweet(tweet);


            /*for(int i=0;i<5;++i) {
                View temp = (View)((LinearLayout) (((LinearLayout) convertView).getChildAt(1)))
                        .getChildAt(i);
                    temp.setTag(tweet);
            }*/

            //disable subviews to avoid links are clickable
            if(convertView instanceof ViewGroup){
                HelperFunctions.disableViewAndSubViews((ViewGroup) convertView);
            }

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setEnabled(true);

            ((BaseTweetView)(((LinearLayout) convertView)
                    .getChildAt(0)))
                    .setTag(tweet);


            /*for(int i=0;i<5;++i) {
                ((LinearLayout) (((LinearLayout) rowView)
                        .getChildAt(1)))
                        .getChildAt(i)
                        .setEnabled(true);
            }*/

            View btnRow              = ((LinearLayout)(((LinearLayout) rowView).getChildAt(1)));
            final ImageButton child1 = (ImageButton)btnRow.findViewById(R.id.retweetimagebutton);
            final ImageButton child2 = (ImageButton)btnRow.findViewById(R.id.favoriteimagebutton);
            final TextView t1        = (TextView)btnRow.findViewById(R.id.retweetcounttext);
            final TextView t2        = (TextView)btnRow.findViewById(R.id.favoritecounttext);

            child1.setEnabled(true);
            child2.setEnabled(true);
            t1.setEnabled(true);
            t2.setEnabled(true);

            t1.setTag(tweet);
            t2.setTag(tweet);

            t1.setText(Integer.toString(((Tweet) t1.getTag()).retweetCount));
            t2.setText(Integer.toString(((Tweet) t2.getTag()).favoriteCount));


            if(tweet.retweeted)
                child1.setImageResource(R.drawable.retweet_on);
            else
                child1.setImageResource(R.drawable.retweet);

            if(tweet.favorited) {
                child2.setImageResource(R.drawable.favorite_on);
                //System.out.println("0pranjalupdate favoriteon " + tweet.id);
            }
            else {
                //System.out.println("0pranjalupdate favorite " + tweet.id);
                child2.setImageResource(R.drawable.favorite);
            }


           child2.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Tweet tempTweet = (Tweet) v.getTag();
                   //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                   if (!tempTweet.favorited) {
                       //child2.setImageResource(R.drawable.favorite_on);
                       favoriteService.create(tempTweet.id, false, new Callback<Tweet>() {
                           @Override
                           public void success(Result<Tweet> result) {
                               updateTweet(result.data);
                               child2.setTag(result.data);
                               child2.setImageResource(R.drawable.favorite_on);
                               System.out.println("5pranjalupdate favoriteon " + result.data.id);
                               //Toast.makeText(context, "Favorite Done " + result.data.favorited, Toast.LENGTH_LONG).show();
                           }

                           @Override
                           public void failure(TwitterException e) {
                               //Toast.makeText(context, "Favorite Not Done", Toast.LENGTH_LONG).show();
                           }
                       });
                   } else {

                   }
               }
           });

            child1.setOnClickListener(new View.OnClickListener() {//myOnClickListener(tweet) {
                @Override
                public void onClick(View v) {
                    final Tweet tempTweet = (Tweet) v.getTag();
                    //Toast.makeText(context, tempTweet.idStr, Toast.LENGTH_LONG).show();

                    statusesService.retweet(tempTweet.id, false, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            child1.setImageResource(R.drawable.retweet_on);
                            System.out.println("pranjalupdate retweet " + result.data.id);
                            child1.setTag(result.data);
                            child2.setTag(result.data);
                            updateTweet(tempTweet.id);
                            //Toast.makeText(context, "Retweet done "+result.data.retweeted, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void failure(TwitterException e) {
                            //Toast.makeText(context, "Retweet Not done", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });
        }

        return (View)rowView;
    }
}
