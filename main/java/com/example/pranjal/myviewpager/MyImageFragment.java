/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pranjal.myviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mopub.volley.RequestQueue;
import com.mopub.volley.toolbox.ImageLoader;
import com.mopub.volley.toolbox.Volley;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

public class MyImageFragment extends BaseFragment {
    LinearLayout myGallery;
    LayoutInflater mInflater;


    List<Tweet> imageTweets;
    ImageAdapter imageAdapter;

    Activity storedActivity;

    boolean loading        = false;
    private RequestQueue mRequestQueue;
    private ImageLoader  mImageLoader;

    long lastTimeStamp;

    View storedView;
    Activity parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        storedActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view  = inflater.inflate(R.layout.image_list, container, false);

        View view  = inflater.inflate(R.layout.myhorizontalscrollview, container, false);


        storedView = view;

        myGallery = (LinearLayout)view.findViewById(R.id.mygallery);



        parentActivity = getActivity();

        //imageUrls = TweetBank.getAllImageUrls();
        imageTweets =  TweetBank.getAllImageUrls();

        mRequestQueue = Volley.newRequestQueue(parentActivity);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        //GridView gridView = (GridView) view.findViewById(R.id.imagegridview);
        imageAdapter      = new ImageAdapter(getActivity());
        //gridView.setAdapter(imageAdapter);


        mInflater = LayoutInflater.from(parentActivity);


        int position = 0;
        SquareImageView picture;
        TextView name;

        for(Tweet t: imageTweets){
            View v;
            v = mInflater.inflate(R.layout.new_grid_item, container, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.picturetext, v.findViewById(R.id.picturetext));

            picture = (SquareImageView) v.getTag(R.id.picture);
            name    = (TextView) v.getTag(R.id.picturetext);

            //picture.setImageUrl(imageUrls.get(position), mImageLoader);
            picture.setImageUrl(t.entities.media.get(0).mediaUrl, mImageLoader);
            name.setText("@" + t.user.screenName);
            myGallery.addView(v);
            ++position;
        }


        /*AbsListView.OnScrollListener listenerObject = null;

        if(listenerObject == null) {
            listenerObject = new AbsListView.OnScrollListener() {
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                    int visibleThreshold = 2;
                    long currentTimeStamp = System.currentTimeMillis();
                    //System.out.println("firstVisibleItem "+firstVisibleItem+" visibleItemCount "+visibleItemCount+" totalItemCount "+totalItemCount+" (totalItemCount - visibleItemCount) "+(totalItemCount - visibleItemCount)+" (firstVisibleItem + visibleThreshold) "+(firstVisibleItem + visibleThreshold));
                    if ((currentTimeStamp - lastTimeStamp)/1000 >10 && loading == false && totalItemCount > 5 && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        imageTweets =  TweetBank.getAllImageUrls();
                        imageAdapter.notifyDataSetChanged();
                        loading       = true;
                        lastTimeStamp = System.currentTimeMillis();

                        //LoadOldTweets();
                        //footer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 70));
                    }
                }
            };
        }

        gridView.setOnScrollListener(listenerObject);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {


//                        Uri uri = Uri.parse("http://javatechig.com");
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);

                        Intent it = new Intent(parentActivity, ShowImage.class);
                        it.putExtra("tweetstring", HelperFunctions.gson.toJson(view.getTag(5)) );
                        startActivity(it);

//                        Toast.makeText(parentActivity, ((TextView) (view.getTag(R.id.picturetext))).getText(), Toast.LENGTH_SHORT).show();
//                        imageAdapter.notifyDataSetChanged();
//                        Toast.makeText(parentActivity, "Hello", Toast.LENGTH_SHORT).show();
//                        ((Image)view.setSelected(!(Image)view.getSelected()));
                    }
                });
*/



        return view;
    }


    public class ImageAdapter extends BaseAdapter {
        private Context localContext;
        private final LayoutInflater mInflater;

        ImageAdapter(Context ct){
            this.localContext = ct;
            mInflater = LayoutInflater.from(localContext);
        }

        @Override
        public int getCount() {
            return imageTweets.size();
            //return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //final SquareImageView imageView;
            View v;
            SquareImageView picture;
            TextView name;

            if (convertView == null) {
                //LayoutInflater inflater = LayoutInflater.from(storedActivity);
                //imageView  = new SquareImageView(storedActivity);

                v = mInflater.inflate(R.layout.new_grid_item, parent, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.picturetext, v.findViewById(R.id.picturetext));

                //imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 400));
                //imageView = (NetworkImageView) storedView.inflate(parentActivity, R.id.networkimageview, null);
               // .inflate(, false);
            } else
                v = convertView;

            picture = (SquareImageView) v.getTag(R.id.picture);
            name    = (TextView) v.getTag(R.id.picturetext);

            //picture.setImageUrl(imageUrls.get(position), mImageLoader);
            picture.setImageUrl(imageTweets.get(position).entities.media.get(0).mediaUrl, mImageLoader);
            name.setText("@" + imageTweets.get(position).user.screenName);

            v.setTag(5, imageTweets.get(position));
            return v;
        }
    }

}
