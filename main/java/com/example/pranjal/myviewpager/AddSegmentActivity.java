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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.mopub.volley.RequestQueue;
import com.mopub.volley.toolbox.ImageLoader;
import com.mopub.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import twitter4j.User;

//import com.crashlytics.android.Crashlytics;


public class AddSegmentActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
            case 0:
                setResult(0);
                finish();
        }
    }

    private ListView lv;
    UserAdapter adapter;
    EditText inputSearch;

    ArrayList<HashMap<String, String>> productList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addsegmentlayout);

        lv = (ListView) findViewById(R.id.user_list_view);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                AddSegmentActivity.this.adapter.getFilter().filter(cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        adapter = new UserAdapter(this);
        lv.setAdapter(adapter);
    }

    public class UserAdapter extends BaseAdapter implements Filterable {
        private Context localContext;
        private final LayoutInflater mInflater;

        private RequestQueue mRequestQueue;
        private ImageLoader  mImageLoader;

        private ArrayList<User> filteredfriends = new ArrayList<User>();

        @Override
        public Filter getFilter() {
            return new Filter() {
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                    filteredfriends = (ArrayList<User>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String filterString   = constraint.toString().toLowerCase();

                    FilterResults results = new FilterResults();
                    final List<User> list = HelperFunctions.friends;

                    int count                   = list.size();
                    final ArrayList<User> nlist = new ArrayList<User>(count);

                    String filterableString;

                    for (int i = 0; i < count; i++) {
                        User temp = list.get(i);
                        if(temp.getName().toLowerCase().contains(filterString)){
                            nlist.add(temp);
                            System.out.println("Adding User " + temp.getName());
                        }
                    }

                    results.values = nlist;
                    results.count  = nlist.size();
                    return results;
                }
            };
        }

        UserAdapter(Context ct){
            this.localContext = ct;
            mInflater = LayoutInflater.from(localContext);

            mRequestQueue = Volley.newRequestQueue(ct);
            mImageLoader  = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }
                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });

            filteredfriends = HelperFunctions.friends;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getCount() {
            return filteredfriends.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredfriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            SquareImageView picture;
            TextView name;

            if (convertView == null) {
                v = mInflater.inflate(R.layout.user_item_twitter, parent, false);
                v.setTag(R.id.userpicture,  v.findViewById(R.id.userpicture));
                v.setTag(R.id.product_name, v.findViewById(R.id.product_name));
            } else
                v = convertView;

            picture = (SquareImageView) v.getTag(R.id.userpicture);
            name    = (TextView) v.getTag(R.id.product_name);

            picture.setImageUrl(filteredfriends.get(position).getBiggerProfileImageURL(), mImageLoader);
            name.setText("@" + filteredfriends.get(position).getName());

            return v;
        }
    }
}
