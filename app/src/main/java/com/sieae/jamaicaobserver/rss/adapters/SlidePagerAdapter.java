package com.sieae.jamaicaobserver.rss.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sieae.jamaicaobserver.DBHelper;
import com.sieae.jamaicaobserver.GlobalVariables;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.sieae.jamaicaobserver.rss.ui.RssDetailActivity;
import com.sieae.jamaicaobserver.rss.ui.RssDetailFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

/**
 * Created by Mohammad Arshi Khan on 08-04-2016.
 */
public class SlidePagerAdapter extends PagerAdapter {
    Activity activity;
    String[]  imageArray;
    int item_position;
    RSSFeed myRssFeed;
    private boolean isRead = false;

    public SlidePagerAdapter(Activity act, String[] imgArra, int position, RSSFeed myRssFeed, boolean isRead) {
        imageArray = imgArra;
        activity = act;
        this.item_position = position;
        this.myRssFeed = myRssFeed;
        this.isRead = isRead;

    }

    public SlidePagerAdapter(Activity act, String imgArra, int position, RSSFeed myRssFeed, boolean isRead) {
        imageArray = new String[1];
        imageArray[0] = imgArra;
        activity = act;
        this.item_position = position;
        this.myRssFeed = myRssFeed;
        this.isRead = isRead;

    }

    public int getCount() {
        return imageArray.length;
    }

    public Object instantiateItem(View collection, int position) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final ImageView rootView = (ImageView) inflater.inflate(R.layout.myimageview, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(5, 5, 5, 5);

//				rootView.setClickable(false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRssFeed.getItem(item_position).setIsRead(true);

                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);

                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                rootView.setColorFilter(filter);

//                List<RSSItem> items = myRssFeed.getList();
//                myRssFeed.setList(items);
//                notifyDataSetChanged();

                activity.startActivity(RssDetailActivity.getLaunchIntent(activity, myRssFeed, item_position));
            }
        });
        rootView.setFocusable(false);
        rootView.setFocusableInTouchMode(false);
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        rootView.setMinimumWidth(size.x);
        rootView.setLayoutParams(params);
        try {
            String thumburl = imageArray[position];
            if (thumburl != "" && thumburl != null){
                Picasso.with(activity).load(thumburl).into(rootView);
                if(isRead){
                    ColorMatrix matrix = new ColorMatrix();
                    matrix.setSaturation(0);

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    rootView.setColorFilter(filter);
                    isRead = false;
                }
            } else {
                //					holder.listThumb.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ViewPager) collection).addView(rootView, 0);
        return rootView;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}