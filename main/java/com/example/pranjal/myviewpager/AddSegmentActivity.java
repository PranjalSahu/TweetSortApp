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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

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
    ArrayAdapter<String> adapter;
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

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.user_item_twitter, R.id.product_name, HelperFunctions.users);

        //adapter.getf
        lv.setAdapter(adapter);

    }

    public class UserAdapter extends BaseAdapter {
        private Context localContext;
        private final LayoutInflater mInflater;

        UserAdapter(Context ct){
            this.localContext = ct;
            mInflater = LayoutInflater.from(localContext);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int getCount() {
            return HelperFunctions.users.size();
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
            View v;
            SquareImageView picture;
            TextView name;

            if (convertView == null) {
                //LayoutInflater inflater = LayoutInflater.from(storedActivity);
                //imageView  = new SquareImageView(storedActivity);

                v = mInflater.inflate(R.layout.new_grid_item, parent, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.picturetext,    v.findViewById(R.id.picturetext));

                //imageView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 400));
                //imageView = (NetworkImageView) storedView.inflate(parentActivity, R.id.networkimageview, null);
                // .inflate(, false);
            } else
                v = convertView;

            picture = (SquareImageView) v.getTag(R.id.picture);
            name    = (TextView) v.getTag(R.id.picturetext);

            //picture.setImageUrl(imageUrls.get(position), mImageLoader);
            //picture.setImageUrl(imageTweets.get(position).entities.media.get(0).mediaUrl, mImageLoader);
            //name.setText("@" + imageTweets.get(position).user.screenName);

            return v;
        }
    }
}
