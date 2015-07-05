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
import android.widget.GridView;
import android.widget.TextView;

import com.mopub.volley.RequestQueue;
import com.mopub.volley.toolbox.ImageLoader;
import com.mopub.volley.toolbox.Volley;

import java.util.List;

public class MyImageFragment extends BaseFragment {
    List<String> imageUrls;
    Activity storedActivity;

    private RequestQueue mRequestQueue;
    private ImageLoader  mImageLoader;

    View storedView;
    Activity parentActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        storedActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.image_list, container, false);
        storedView = view;

        parentActivity = getActivity();

        imageUrls = TweetBank.getAllImageUrls();

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

        GridView gridView = (GridView) view.findViewById(R.id.imagegridview);
        gridView.setAdapter(new ImageAdapter(getActivity()));

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
            return imageUrls.size();
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
                v.setTag(R.id.text, v.findViewById(R.id.text));

                //imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 400));
                //imageView = (NetworkImageView) storedView.inflate(parentActivity, R.id.networkimageview, null);
               // .inflate(, false);
            } else
                v = convertView;

            picture = (SquareImageView) v.getTag(R.id.picture);
            name    = (TextView) v.getTag(R.id.text);

            picture.setImageUrl(imageUrls.get(position), mImageLoader);
            name.setText("pranjal");

            return v;
        }
    }

}
