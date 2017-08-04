package com.sieae.jamaicaobserver.rss.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sieae.jamaicaobserver.DBHelper;
import com.sieae.jamaicaobserver.GlobalVariables;
import com.sieae.jamaicaobserver.Helper;
import com.sieae.jamaicaobserver.MainActivity;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.fav.FavDbAdapter;
import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSHandler;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.sieae.jamaicaobserver.rss.adapters.SlidePagerAdapter;
import com.squareup.picasso.Picasso;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.SyncListener;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
/**
 * This activity is used to display a list of rss items
 */

public class AllRssFragment extends ListFragment {

    private List<RSSFeed> myRssFeedList = new ArrayList<RSSFeed>();
//    private RSSFeed myRssFeed = null;
    private Activity mAct;
    private LinearLayout ll;
    private RelativeLayout pDialog;
    private FavDbAdapter mDbHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isNight_Mode = false;
    private MyCustomAdapter mAdapter;
    private ListView mListview;
    private SharedPreferences mSettings;
    private DBHelper db;
    private List<RSSItem> myCartoonPicList = new ArrayList<RSSItem>();
    int color_lightgray, color_white, color_black;
    int color_bg, color_text;



    public class MyCustomAdapter extends ArrayAdapter<RSSFeed> {
        String img_url1, img_url2;


        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<RSSFeed> list) {
            super(context, textViewResourceId, list);

            mDbHelper = new FavDbAdapter(mAct);
            mDbHelper.open();

            db = new DBHelper(mAct);
            try {
                db.createDatabase();
            } catch (IOException e) {
                System.out.println("unable to create database " + e.toString());
            }

            try {
                db.openDatabase();
            } catch (Exception e) {
                System.out.println("unable to open database " + e.toString());
            }
        }

        @Override
        public int getCount() {
            if(myRssFeedList.size()>=15) // 15th position for exceed cartoon feeds
                return myRssFeedList.size()-1;
            else return myRssFeedList.size();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final RSSFeed myRssFeed1 = myRssFeedList.get(position);
            List<RSSItem> rssItems = null;
            if (myRssFeed1.getList().size()==0)
            {
                return row;
            }else if (myRssFeed1.getList().size()>=7)
            {
                rssItems = myRssFeed1.getList().subList(0,6);
            }else if (myRssFeed1.getList().size()<7){
                rssItems = myRssFeed1.getList().subList(0,myRssFeed1.getList().size());
            }


//            final RSSFeed cartoonRssFeed = myRssFeedList.get(myRssFeedList.size()-1);
//            if (!myCartoonPicList.isEmpty())
//                cartoonItems = myCartoonPicList.get(position);

            final ViewHolder holder;
//            int color_bg, color_text;
//            if (isNight_Mode) {
//                color_bg = Color.parseColor("#000000");
//                color_text = Color.parseColor("#ffffff");
//            } else {
////                color_bg = Color.parseColor("#ffffff");
//                color_text = Color.parseColor("#000000");
//
//                color_bg = color_lightgray;
//            }
            if (row == null) {

                LayoutInflater inflater = mAct.getLayoutInflater();
                row = inflater.inflate(R.layout.fragment_allrss_row, null);

                holder = new ViewHolder();

                holder.listTitle = (TextView) row.findViewById(R.id.listtitle);
                holder.listPubdate = (TextView) row.findViewById(R.id.listpubdate);
                holder.listDescription = (TextView) row.findViewById(R.id.shortdescription);
                holder.listThumb = (ImageView) row.findViewById(R.id.listthumb);
                holder.favorite = (ImageView) row.findViewById(R.id.favorite);
                holder.slide_show = (ImageView) row.findViewById(R.id.slide_show);
                holder.share = (ImageView) row.findViewById(R.id.share);
                holder.time = (TextView) row.findViewById(R.id.time);
//				holder.limiter_layout = (LinearLayout)row.findViewById(R.id.limiter_layout);
                holder.cardView = (CardView) row.findViewById(R.id.card_view);
                holder.cardView1 = (CardView) row.findViewById(R.id.card_view1);
                holder.cardView2 = (CardView) row.findViewById(R.id.card_view2);
                holder.cardView3 = (CardView) row.findViewById(R.id.card_view3);
                holder.cardView4 = (CardView) row.findViewById(R.id.card_view4);
                holder.cardView5 = (CardView) row.findViewById(R.id.card_view5);
                holder.bottom_layout = (LinearLayout) row.findViewById(R.id.bottom_layout);
                holder.viewPager = (AutoScrollViewPager ) row.findViewById(R.id.pager);
                holder.frameLayout = (FrameLayout) row.findViewById(R.id.top_frame);
                holder.pager_indicator = (LinearLayout) row.findViewById(R.id.viewPagerCountDots);
                holder.timer = new Timer();
                holder.adView = (AdView) row.findViewById(R.id.rowAdView);
                holder.adView.setVisibility(View.GONE);

                //AllRSS
                holder.news_type = (TextView) row.findViewById(R.id.news_type);
                holder.allrowAdView = (AdView) row.findViewById(R.id.allrowAdView);
                holder.subheading1 = (TextView) row.findViewById(R.id.subheading1);
                holder.subheading2 = (TextView) row.findViewById(R.id.subheading2);
                holder.subheading3 = (TextView) row.findViewById(R.id.subheading3);
                holder.subheading4 = (TextView) row.findViewById(R.id.subheading4);
                holder.subheading5 = (TextView) row.findViewById(R.id.subheading5);

                holder.img_thumb1 = (ImageView) row.findViewById(R.id.img_thumb1);
                holder.img_thumb2 = (ImageView) row.findViewById(R.id.img_thumb2);
                holder.cartoonthumb = (ImageView) row.findViewById(R.id.listthumb);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            if(position%3==0 && position!=0){
//                AdRequest adRequest = new AdRequest.Builder()
//                        .build();
//                holder.adView.setVisibility(View.VISIBLE);
//                holder.adView.loadAd(adRequest);
//            }else{
//                holder.adView.setVisibility(View.GONE);
//            }

            AdRequest adRequest = new AdRequest.Builder().build();
            holder.allrowAdView.loadAd(adRequest);

            holder.viewPager.setInterval(3500);
            holder.viewPager.startAutoScroll();

            holder.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
//					for (int i = 0; i < holder.dotsCount; i++) {
//						holder.dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
//					}
//
//					holder.dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
//			holder.viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

            try {
                Field mScroller;
                Interpolator sInterpolator = new AccelerateInterpolator();
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(holder.viewPager.getContext(), sInterpolator);
                scroller.setFixedDuration(1000);
                mScroller.set(holder.viewPager, scroller);
            } catch (NoSuchFieldException e) {
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }

            row.setBackgroundColor(color_bg);
//			holder.cardView.setCardBackgroundColor(color_bg);

//            holder.bottom_layout.setBackgroundColor(color_bg);

            final String title = Html.fromHtml(rssItems.get(0).getTitle()).toString();
            final String link = rssItems.get(0).getLink();
            final String description = rssItems.get(0).getRowDescription();
            final String date = rssItems.get(0).getPubdate().trim();

            final String subheading1 = Html.fromHtml(rssItems.get(1).getTitle()).toString();
            final String subheading2 = Html.fromHtml(rssItems.get(2).getTitle()).toString();
            final String subheading3 = Html.fromHtml(rssItems.get(3).getTitle()).toString();
            final String subheading4 = Html.fromHtml(rssItems.get(4).getTitle()).toString();
            final String subheading5 = Html.fromHtml(rssItems.get(5).getTitle()).toString();

            img_url1 = rssItems.get(4).getThumburl().toString();
            img_url2 = rssItems.get(5).getThumburl().toString();

            String[] Columns = {
                    GlobalVariables.COLUMN_HEADING

            };

//			String db_title = DatabaseUtils.sqlEscapeString(title);//title.replaceAll("'", "\'");
            String new_url = link.replace("http://", "");
            String[] url_splite = new_url.split("/");
            String heading = url_splite[url_splite.length - 1];

            String news_type = url_splite[url_splite.length - 2];
            holder.news_type.setText(news_type);

            String where = "readrssheading='" + heading + "'";
            boolean isRead = false;
            Cursor cursor = db.getAlldata(GlobalVariables.DATABASE_TABLE, Columns, where, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() == 1) {
                    isRead = true;
                    cursor.close();
                } else {
                    isRead = false;
                }
            } else {
                isRead = false;
            }
            // Wednesday, March 30, 2016 00:00 AM    "EEEE, MMMM dd, yyyy hh:mm a"
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
            Date dat = null;
            try {
                dat = sdf.parse(date);
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                String newFormatTime = formatter.format(dat);
                holder.time.setText(newFormatTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int vid = v.getId();
                    switch (vid){
                        case R.id.card_view:
                            myRssFeed1.getItem(0).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 0));
                            break;
                        case R.id.card_view1:
                            myRssFeed1.getItem(1).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 1));
                            break;
                        case R.id.card_view2:
                            myRssFeed1.getItem(2).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 2));
                            break;
                        case R.id.card_view3:
                            myRssFeed1.getItem(3).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 3));
                            break;
                        case R.id.card_view4:
                            myRssFeed1.getItem(4).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 4));
                            break;
                        case R.id.card_view5:
                            myRssFeed1.getItem(5).setIsRead(true);
                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed1, 5));
                            break;
                    }


                }
            };

            holder.cardView.setOnClickListener(clickListener);
            holder.cardView1.setOnClickListener(clickListener);
            holder.cardView2.setOnClickListener(clickListener);
            holder.cardView3.setOnClickListener(clickListener);
            holder.cardView4.setOnClickListener(clickListener);
            holder.cardView5.setOnClickListener(clickListener);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.favorite) {

                        if (mDbHelper.checkEvent(title, description, date, link, "", "", "rss")) {
                            // Item is new
                            mDbHelper.addFavorite(title, description, date, link, "", "", "rss");
                            holder.favorite.setImageResource(R.drawable.ic_action_toggle_star);
                            Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_success), Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_duplicate), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    } else if (v.getId() == R.id.share) {

//                        String linkvalue = getResources().getString(R.string.item_share_begin);
//                        String seenvalue = getResources().getString(R.string.item_share_middle);
//                        String appvalue = getResources().getString(R.string.item_share_end);
//                        String applicationName = getResources().getString(R.string.app_name);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        //this is the text that will be shared
//                        sendIntent.putExtra(Intent.EXTRA_TEXT, (title + linkvalue + link + seenvalue + applicationName + appvalue));
                        sendIntent.putExtra(Intent.EXTRA_TEXT, (title + "\n"+link));
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, title); //you can replace title with a string of your choice
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_header)));
                    }
                }
            };

            holder.favorite.setOnClickListener(onClickListener);
            if (mDbHelper.checkEvent(title, description, date, link, "", "", "rss")) {
                // Item is new
                holder.favorite.setImageResource(R.drawable.ic_action_toggle_star_outline);
            } else {
                holder.favorite.setImageResource(R.drawable.ic_action_toggle_star);

            }

            holder.share.setOnClickListener(onClickListener);

            holder.listTitle.setText(Html.fromHtml(title)); //myRssFeed.getList().get(position).getTitle()

            holder.subheading1.setText(Html.fromHtml(subheading1));
            holder.subheading2.setText(Html.fromHtml(subheading2));
            holder.subheading3.setText(Html.fromHtml(subheading3));
            holder.subheading4.setText(Html.fromHtml(subheading4));
            holder.subheading5.setText(Html.fromHtml(subheading5));
//			holder.listPubdate.setText(myRssFeed.getList().get(position).getPubdate());

            SimpleDateFormat sdf1 = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
            Date dat1 = null;
            try {
                dat1 = sdf1.parse(date);
                SimpleDateFormat formatter1 = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                String newFormatDate = formatter1.format(dat1);
                holder.listPubdate.setText(newFormatDate);
                holder.listPubdate.setTextColor(color_text);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            String html = rssItems.get(0).getRowDescription();

            holder.listDescription.setText(html);
//            holder.listDescription.setTextColor(color_text);

            try {
                String thumburl = rssItems.get(0).getThumburl();
//				thumburl = thumburl.replaceAll("jpghttp", "jpg http");
                String array_thumburl[] = thumburl.split(" ");

                if (array_thumburl.length > 0) {
                    holder.mPagerAdapter = new SlidePagerAdapter(mAct, array_thumburl, 0, myRssFeed1, isRead); // zero for first news
                    isRead = false;
                    holder.viewPager.setAdapter(holder.mPagerAdapter);
                } else {
                    holder.mPagerAdapter = new SlidePagerAdapter(mAct, thumburl, 0, myRssFeed1, isRead);
                    isRead = false;
                    holder.viewPager.setAdapter(holder.mPagerAdapter);
                }

                holder.pager_indicator.removeAllViews();

//				if(array_thumburl.length>1) {
//					holder.slide_show.setVisibility(View.VISIBLE);
//				}else{
//					holder.slide_show.setVisibility(View.GONE);
//				}

                if (array_thumburl.length > 1) {
                    holder.slide_show.setVisibility(View.VISIBLE);
                    holder.dotsCount = array_thumburl.length;

                    // Timer for auto sliding
//					if(holder.timer!=null){
//                        holder.timer.cancel();
//                    }

//                    TimerTask timerTask = new TimerTask() {
//                        @Override
//                        public void run() {
//                            mAct.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (holder.count <= holder.dotsCount) {
//                                        holder.viewPager.setCurrentItem(holder.count);
//                                        holder.count++;
//                                    } else {
//                                        holder.count = 0;
//                                        holder.viewPager.setCurrentItem(holder.count);
//                                    }
//                                }
//                            });
//                        }
//                    };
//                    holder.timer.schedule(timerTask, 3500, 3500);

                } else {
                    holder.slide_show.setVisibility(View.GONE);
                }

                String array_thumburl1[] = img_url1.split(" ");
                if (array_thumburl1.length > 1) {
                    img_url1 = array_thumburl1[0];
                }

                String array_thumburl2[] = img_url2.split(" ");
                if (array_thumburl2.length > 1) {
                    img_url2 = array_thumburl2[0];
                }

                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (img_url1 != "" && img_url1 != null){
                            Picasso.with(mAct).load(img_url1).into(holder.img_thumb1);
                        }

                        if (img_url2 != "" && img_url2 != null){
                            Picasso.with(mAct).load(img_url2).into(holder.img_thumb2);
                        }

                        try {
                            if (!myCartoonPicList.isEmpty()){
                                RSSItem cartoonItems = myCartoonPicList.get(position);
                                String imgtoonurl = cartoonItems.getThumburl();
                                if (imgtoonurl != "" && imgtoonurl != null){
                                    Picasso.with(mAct).load(imgtoonurl).into(holder.cartoonthumb);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });



            } catch (Exception e) {
                e.printStackTrace();
            }

            return row;
        }

        public class FixedSpeedScroller extends Scroller {

            private int mDuration = 1000;

            private void setFixedDuration(int duration) {
                mDuration = duration;
            }

            public FixedSpeedScroller(Context context) {
                super(context);
            }

            public FixedSpeedScroller(Context context, Interpolator interpolator) {
                super(context, interpolator);
            }

            public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
                super(context, interpolator, flywheel);
            }


            @Override
            public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                // Ignore received duration, use fixed one instead
                super.startScroll(startX, startY, dx, dy, mDuration);
            }

            @Override
            public void startScroll(int startX, int startY, int dx, int dy) {
                // Ignore received duration, use fixed one instead
                super.startScroll(startX, startY, dx, dy, mDuration);
            }
        }


    } // End of Adapter


    class GetCartoonPic extends AsyncTask<Void, Void, Void> {
//        private List<RSSItem> myCartoonPicList = new ArrayList<RSSItem>();
//        private RSSItem cartoonItems = null;
//        ImageView imageView;
//        int position;
//        public GetCartoonPic(ImageView imageView, int position){
//            this.imageView=imageView;
//            this.position=position;
//        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL rssUrl = new URL("http://www.jamaicaobserver.com/app/cartoons");
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                RSSHandler myRSSHandler = new RSSHandler();
                myXMLReader.setContentHandler(myRSSHandler);

                InputSource myInputSource = new InputSource(rssUrl.openStream());
                myInputSource.setEncoding("iso-8859-1");


                mySAXParser.parse(myInputSource, myRSSHandler);

                RSSFeed myRssFeed = myRSSHandler.getFeed();
                myCartoonPicList = myRssFeed.getList();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            mAct.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        if (!myCartoonPicList.isEmpty()){
//                            cartoonItems = myCartoonPicList.get(GetCartoonPic.this.position);
//                            String imgtoonurl = cartoonItems.getThumburl();
//                            if (imgtoonurl != "" && imgtoonurl != null){
//                                Picasso.with(mAct).load(imgtoonurl).into(GetCartoonPic.this.imageView);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        }

    }

    private int getScreenWidth() {

        WindowManager wm = (WindowManager) mAct.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    static class ViewHolder {
        TextView listTitle, listPubdate, time, listDescription;
        ImageView listThumb, favorite, share, slide_show;
        int count;
        LinearLayout bottom_layout, pager_indicator;
        CardView cardView, cardView1, cardView2, cardView3, cardView4, cardView5;
        AutoScrollViewPager viewPager;
        FrameLayout frameLayout;
        SlidePagerAdapter mPagerAdapter;
        private static int dotsCount;
        //		private static ImageView[] dots;
        Timer timer;
        AdView adView, allrowAdView;

        //All
        TextView news_type, subheading1, subheading2, subheading3, subheading4, subheading5;
        ImageView img_thumb1, img_thumb2, cartoonthumb;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_rss, container, false);
        ll = (LinearLayout) mSwipeRefreshLayout.findViewById(R.id.subroot);
        setHasOptionsMenu(true);

        if ((getResources().getString(R.string.ad_visibility).equals("0"))) {
            // Look up the AdView as a resource and load a request.
            AdView adView = (AdView) ll.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        /*remove this for work swipe refresh*/
        mSwipeRefreshLayout.setEnabled(false);

        /*temp comment*/
//        mSwipeRefreshLayout = (SwipeRefreshLayout) mSwipeRefreshLayout
//                .findViewById(R.id.activity_main_swipe_refresh_layout);

//		mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
//				android.R.color.holo_green_light,
//				android.R.color.holo_orange_light,
//				android.R.color.holo_red_light);

        /*temp comment*/
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                // TODO Auto-generated method stub
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        String weburl = AllRssFragment.this.getArguments().getString(MainActivity.DATA);
//                        String weburls[] = null;
//                        if (!weburl.isEmpty()){
//                            weburls = weburl.split(",");
//                            new MyTask(weburls).execute();
//                        }
//                    }
//
//                }, 200);
//
//            }
//        });

//        color_lightgray = getResources().getColor(R.color.lightgray);
//
//        int color_bg;
//        if (isNight_Mode) {
//            color_bg = Color.parseColor("#000000");
//        } else {
////            color_bg = Color.parseColor("#ffffff");
//
//            color_bg = color_lightgray;
//        }
        ll.setBackgroundColor(color_bg);

//	    return ll;
        return mSwipeRefreshLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAct = getActivity();
        Log.v("INFO", "onAttach() called");
        mSettings = PreferenceManager.getDefaultSharedPreferences(mAct);
        isNight_Mode = mSettings.getBoolean("isNight_Mode", false);

        color_lightgray = getResources().getColor(R.color.lightgray);
        color_white = getResources().getColor(R.color.white);
        color_black = getResources().getColor(R.color.black);

        if (isNight_Mode) {
            color_bg = color_black;
            color_text = color_white;
        } else {
//                color_bg = Color.parseColor("#ffffff");
            color_text = color_black;

            color_bg = color_lightgray;
        }

//        if (isNight_Mode) {
//            color_bg = Color.parseColor("#000000");
//        } else {
////            color_bg = Color.parseColor("#ffffff");
//
//            color_bg = color_lightgray;
//        }


        new GetCartoonPic().execute();

        mListview = getListView();
        mAdapter = new MyCustomAdapter(mAct,
                R.layout.fragment_rss_row, myRssFeedList);
        mListview.setAdapter(mAdapter);

        String weburl = AllRssFragment.this.getArguments().getString(MainActivity.DATA);
        String weburls[] = null;
        if (!weburl.isEmpty()){
            weburls = weburl.split(",");
            new MyTask(weburls).execute();
        }

        if (mListview != null) {
            mListview.setBackgroundColor(color_bg);
        }
        if (ll != null) {
            ll.setBackgroundColor(color_bg);
        }
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {
        private String weburls[];

        public MyTask(String web_urls[] ){
            this.weburls = web_urls;
        }
        @Override
        protected void onPreExecute() {
            pDialog = (RelativeLayout) ll.findViewById(R.id.progressBarHolder);
//            if (mSwipeRefreshLayout.isRefreshing()) {
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
//                String weburl = AllRssFragment.this.getArguments().getString(MainActivity.DATA);
                for(String weburl: this.weburls){
                    URL rssUrl = new URL(weburl);
                    SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                    SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                    XMLReader myXMLReader = mySAXParser.getXMLReader();
                    RSSHandler myRSSHandler = new RSSHandler();
                    myXMLReader.setContentHandler(myRSSHandler);



                    InputSource myInputSource = new InputSource(rssUrl.openStream());
                    myInputSource.setEncoding("iso-8859-1");


                    mySAXParser.parse(myInputSource, myRSSHandler);


                    RSSFeed myRssFeed = myRSSHandler.getFeed();
                    myRssFeedList.add(myRssFeed);
                    mAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    if (pDialog.getVisibility() == View.VISIBLE) {
                                        pDialog.setVisibility(View.GONE);
                                        Helper.revealView(mListview, ll);
                                    }
                                }
                            }, 200);

                        }
                    });
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
//                mListview = getListView();//(ListView) ll.findViewById(R.id.rsslist);
//                int color_bg;
//                if (isNight_Mode) {
//                    color_bg = Color.parseColor("#000000");
//                } else {
////                    color_bg = Color.parseColor("#ffffff");
//
//                    color_bg = color_lightgray;
//                }
                mListview.setBackgroundColor(color_bg);

                mListview.setOnScrollListener(new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {
//					boolean enable = false;
//					if(listview != null && listview.getChildCount() > 0){
//						// check if the first item of the list is visible
//						boolean firstItemVisible = listview.getFirstVisiblePosition() == 0;
//						// check if the top of the first item is visible
//						boolean topOfFirstItemVisible = listview.getChildAt(0).getTop() == 0;
//						// enabling or disabling the refresh layout
//						enable = firstItemVisible && topOfFirstItemVisible;
//					}
//					mSwipeRefreshLayout.setEnabled(enable);

                        /*temp comment*/
//                        int topRowVerticalPosition = (mListview == null || mListview.getChildCount() == 0) ? 0 : mListview.getChildAt(0).getTop();
//                        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);


                    }
                });

//                if (!myRssFeedList.isEmpty()){
//                    mAdapter = new MyCustomAdapter(mAct,
//                            R.layout.fragment_rss_row, myRssFeedList);
//                    mListview.setAdapter(mAdapter);
//                }

//                if (myRssFeed != null) {
//                    mAdapter = new MyCustomAdapter(mAct,
//                            R.layout.fragment_rss_row, myRssFeed.getList());
//                    mListview.setAdapter(mAdapter);
//
//                    mListview.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> arg0, View v,
//                                                int position, long id) {
////						Toast.makeText(getActivity(),"Clicked "+position, Toast.LENGTH_SHORT).show();
//                            myRssFeed.getItem(position).setIsRead(true);
//
////						mPagerAdapter.notifyDataSetChanged();
////						List<RSSItem> items = myRssFeed.getList();
////						myRssFeed.setList(items);
////						mAdapter.notifyDataSetChanged();
//
//                            startActivity(RssDetailActivity.getLaunchIntent(mAct, myRssFeed, position));
//
//
//                        }
//                    });
//
//                }

//                else {
//                    Helper.noConnection(mAct, true);
//                }

                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.GONE);
                    Helper.revealView(mListview, ll);
                }

                /*temp comment*/
//                mSwipeRefreshLayout.setRefreshing(false);
            } catch (IllegalStateException e) {

            } catch (Exception e) {

            }
            super.onPostExecute(result);
        }

    }

//	// Set the check state of an actionbar item that has its actionLayout set to a layout
//	// containing a checkbox with the ID action_item_checkbox.
//	private void setActionBarCheckboxChecked(MenuItem it, boolean checked)
//	{
//		if (it == null)
//			return;
//
//		it.setChecked(checked);
//
//		// Since it is shown as an action, and not in the sub-menu we have to manually set the icon too.
//		CheckBox cb = (CheckBox)it.getActionView().findViewById(R.id.action_item_checkbox);
//		if (cb != null)
//			cb.setChecked(checked);
//	}

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.night_mode).setChecked(isNight_Mode);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rss_menu, menu);
//		// Restore the check state e.g. if the device has been rotated.
//		final MenuItem logItem = menu.findItem(R.id.night_mode);
//		setActionBarCheckboxChecked(logItem, isNight_Mode);
//
//		CheckBox cb = (CheckBox)logItem.getActionView().findViewById(R.id.action_item_checkbox);
//		if (cb != null)
//		{
//			// Set the text to match the item.
//			cb.setText(logItem.getTitle());
//			// Add the onClickListener because the CheckBox doesn't automatically trigger onOptionsItemSelected.
//			cb.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					onOptionsItemSelected(logItem);
//				}
//			});
//		}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.night_mode:
                isNight_Mode = !isNight_Mode;
                // Toggle the checkbox.
//				setActionBarCheckboxChecked(item, !item.isChecked());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putBoolean("isNight_Mode", isNight_Mode);
                editor.commit();
                mAct.invalidateOptionsMenu();

                if (isNight_Mode) {
                    color_bg = color_black;
                    color_text = color_white;
                } else {
//                color_bg = Color.parseColor("#ffffff");
                    color_text = color_black;

                    color_bg = color_lightgray;
                }

//                int color_bg;
//                if (isNight_Mode) {
//                    color_bg = Color.parseColor("#000000");
//                } else {
////                    color_bg = Color.parseColor("#ffffff");
//
//                    color_bg = color_lightgray;
//                }
                if (mListview != null) {
                    mListview.setBackgroundColor(color_bg);
                }
                if (ll != null) {
                    ll.setBackgroundColor(color_bg);
                }
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }

                return true;
            case R.id.refresh_rss:
                /*temp comment*/
//                mSwipeRefreshLayout.setRefreshing(true);
                String weburl = AllRssFragment.this.getArguments().getString(MainActivity.DATA);
                String weburls[] = null;
                if (!weburl.isEmpty()){
                    weburls = weburl.split(",");
                    new MyTask(weburls).execute();
                }
                return true;
            case R.id.info:
                //show information about the feed in general in a dialog
//                if (myRssFeed != null) {
//                    String FeedTitle = (myRssFeed.getTitle());
//                    String FeedDescription = (myRssFeed.getDescription());
//                    //String FeedPubdate = (myRssFeed.getPubdate()); most times not present
//                    String FeedLink = (myRssFeed.getLink());
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mAct);
//
//                    String titlevalue = getResources().getString(R.string.feed_title_value);
//                    String descriptionvalue = getResources().getString(R.string.feed_description_value);
//                    String linkvalue = getResources().getString(R.string.feed_link_value);
//
//                    if (FeedLink.equals("")) {
//                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
//                                "\n\n" + descriptionvalue + ": \n" + FeedDescription);
//                    } else {
//                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
//                                "\n\n" + descriptionvalue + ": \n" + FeedDescription +
//                                "\n\n" + linkvalue + ": \n" + FeedLink);
//                    }
//                    ;
//
//                    builder.setNegativeButton(getResources().getString(R.string.ok), null)
//                            .setCancelable(true);
//                    builder.create();
//                    builder.show();
//
//                } else {
//
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
