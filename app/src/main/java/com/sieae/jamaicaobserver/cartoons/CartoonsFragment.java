package com.sieae.jamaicaobserver.cartoons;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sieae.jamaicaobserver.GlobalVariables;
import com.sieae.jamaicaobserver.Helper;
import com.sieae.jamaicaobserver.MainActivity;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.fav.FavDbAdapter;
import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSHandler;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.squareup.picasso.Picasso;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Mohammad Arshi Khan on 05-04-2016.
 */
public class CartoonsFragment extends ListFragment implements ViewPager.OnPageChangeListener {

    private RSSFeed myRssFeed = null;
    private Activity mAct;
    private LinearLayout ll;
    private RelativeLayout pDialog;
    private FavDbAdapter mDbHelper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isNight_Mode = false;
    private MyCustomAdapter mAdapter;
    private ListView mListview;
    private SharedPreferences mSettings;
    private int dotsCount;
    private ImageView[] dots;
    int color_lightgray, color_white, color_black;
    int color_bg, color_text;

    public class MyCustomAdapter extends ArrayAdapter<RSSItem> {
        SlidePagerAdapter mPagerAdapter;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               List<RSSItem> list) {
            super(context, textViewResourceId, list);

            mDbHelper = new FavDbAdapter(mAct);
            mDbHelper.open();

        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;

            final ViewHolder holder;
//            int color_bg, color_text;
//            if (isNight_Mode) {
//                color_bg = Color.parseColor("#000000");
//                color_text = Color.parseColor("#ffffff");
//            } else {
//                color_bg = Color.parseColor("#ffffff");
//                color_text = Color.parseColor("#000000");
//            }
            if (row == null) {

                LayoutInflater inflater = mAct.getLayoutInflater();
                row = inflater.inflate(R.layout.fragment_rss_row, null);

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
                holder.bottom_layout = (LinearLayout) row.findViewById(R.id.bottom_layout);
                holder.viewPager = (ViewPager) row.findViewById(R.id.pager);
                holder.frameLayout = (FrameLayout) row.findViewById(R.id.top_frame);
                holder.pager_indicator = (LinearLayout) row.findViewById(R.id.viewPagerCountDots);
                holder.adView = (AdView) row.findViewById(R.id.rowAdView);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if(position%3==0 && position!=0){
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                holder.adView.setVisibility(View.VISIBLE);
                holder.adView.loadAd(adRequest);
            }else{
                holder.adView.setVisibility(View.GONE);
            }

            holder.viewPager.setOnPageChangeListener(CartoonsFragment.this);

            row.setBackgroundColor(color_bg);
//			holder.cardView.setCardBackgroundColor(color_bg);

//            holder.bottom_layout.setBackgroundColor(color_bg);

            final String title = Html.fromHtml(myRssFeed.getList().get(position).getTitle()).toString();
            final String link = myRssFeed.getList().get(position).getLink();
            final String description = myRssFeed.getList().get(position).getRowDescription();
            final String date = myRssFeed.getList().get(position).getPubdate().trim();

            String[] Columns = {
                    GlobalVariables.COLUMN_HEADING

            };

            // Mon, 04 Apr 2016 00:01:53 GMT    "EEE, dd MMM yyyy hh:mm:ss GMT"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date dat = null;
            try {
                dat = sdf.parse(date);
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                String newFormatTime = formatter.format(dat);
                holder.time.setText(newFormatTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


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

                        String linkvalue = getResources().getString(R.string.item_share_begin);
                        String seenvalue = getResources().getString(R.string.item_share_middle);
                        String appvalue = getResources().getString(R.string.item_share_end);
                        String applicationName = getResources().getString(R.string.app_name);
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        //this is the text that will be shared
                        sendIntent.putExtra(Intent.EXTRA_TEXT, (title + linkvalue + link + seenvalue + applicationName + appvalue));
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

            holder.listTitle.setText(Html.fromHtml(myRssFeed.getList().get(position).getTitle()));

            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
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


            String html = myRssFeed.getList().get(position).getRowDescription();

            holder.listDescription.setText(html);
//            holder.listDescription.setTextColor(color_text);

            try {
                String thumburl = myRssFeed.getList().get(position).getThumburl();
//                thumburl = thumburl.replaceAll("jpghttp", "jpg http");
                String array_thumburl[] = thumburl.split(" ");
//                if(array_thumburl.length>1) {
//                    holder.slide_show.setVisibility(View.VISIBLE);
//                }else{
//                    holder.slide_show.setVisibility(View.GONE);
//                }
                if (array_thumburl.length > 0) {
                    mPagerAdapter = new SlidePagerAdapter(mAct, array_thumburl, position);
                    holder.viewPager.setAdapter(mPagerAdapter);
                } else {
                    mPagerAdapter = new SlidePagerAdapter(mAct, thumburl, position);
                    holder.viewPager.setAdapter(mPagerAdapter);
                }

                holder.pager_indicator.removeAllViews();

                if (array_thumburl.length > 1) {
                    holder.slide_show.setVisibility(View.VISIBLE);
                    dotsCount = array_thumburl.length;
                    dots = new ImageView[dotsCount];

                    for (int i = 0; i < dotsCount; i++) {
                        dots[i] = new ImageView(mAct);
                        dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.setMargins(4, 0, 4, 0);

                        holder.pager_indicator.addView(dots[i], params);
                    }

                    dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                } else {
                    holder.slide_show.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return row;
        }

        private class SlidePagerAdapter extends PagerAdapter {
            Activity activity;
            String[] imageArray;
//            int item_position;

            public SlidePagerAdapter(Activity act, String[] imgArra, int position) {
                imageArray = imgArra;
                activity = act;
//                this.item_position = position;
            }

            public SlidePagerAdapter(Activity act, String imgArra, int position) {
                imageArray = new String[1];
                imageArray[0] = imgArra;
                activity = act;
//                this.item_position = position;
            }

            public int getCount() {
                return imageArray.length;
            }

            public Object instantiateItem(View collection, final int position) {
                LayoutInflater inflater = activity.getLayoutInflater();
                ImageView rootView = (ImageView) inflater.inflate(R.layout.myimageview, null);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(5, 5, 5, 5);

//				rootView.setClickable(false);
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent zoom = new Intent(mAct, ScaleImageViewActivity.class);
                        zoom.putExtra("url", imageArray[position]);
                        startActivity(zoom);
                    }
                });
                rootView.setFocusable(false);
                rootView.setFocusableInTouchMode(false);
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                rootView.setMinimumWidth(size.x);
                rootView.setLayoutParams(params);
                try {
                    String thumburl = imageArray[position];
                    if (thumburl != "" && thumburl != null) {
                        Picasso.with(getActivity()).load(thumburl).into(rootView);
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
        int position;
        LinearLayout bottom_layout, pager_indicator;
        CardView cardView;
        ViewPager viewPager;
        FrameLayout frameLayout;
        AdView adView;
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

        mSwipeRefreshLayout = (SwipeRefreshLayout) mSwipeRefreshLayout
                .findViewById(R.id.activity_main_swipe_refresh_layout);

//		mSwipeRefreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
//				android.R.color.holo_green_light,
//				android.R.color.holo_orange_light,
//				android.R.color.holo_red_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new MyTask().execute();
                    }

                }, 200);

            }
        });

//        int color_bg;
//        if (isNight_Mode) {
//            color_bg = Color.parseColor("#000000");
//        } else {
//            color_bg = Color.parseColor("#ffffff");
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

        new MyTask().execute();
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            pDialog = (RelativeLayout) ll.findViewById(R.id.progressBarHolder);
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
//            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                String weburl = CartoonsFragment.this.getArguments().getString(MainActivity.DATA);
                URL rssUrl = new URL(weburl);
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                RSSHandler myRSSHandler = new RSSHandler();
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource(rssUrl.openStream());
                myXMLReader.parse(myInputSource);

                myRssFeed = myRSSHandler.getFeed();

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
                mListview = getListView();//(ListView) ll.findViewById(R.id.rsslist);
//                int color_bg;
//                if (isNight_Mode) {
//                    color_bg = Color.parseColor("#000000");
//                } else {
//                    color_bg = Color.parseColor("#ffffff");
//                }
                mListview.setBackgroundColor(color_bg);
                mListview.setOnScrollListener(new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {

                        int topRowVerticalPosition = (mListview == null || mListview.getChildCount() == 0) ? 0 : mListview.getChildAt(0).getTop();
                        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);


                    }
                });

                if (myRssFeed != null) {
                    mAdapter = new MyCustomAdapter(mAct,
                            R.layout.fragment_rss_row, myRssFeed.getList());
                    mListview.setAdapter(mAdapter);

                    mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View v,
                                                int position, long id) {

                            Intent zoom = new Intent(mAct, ScaleImageViewActivity.class);
                            zoom.putExtra("url", myRssFeed.getList().get(position).getThumburl());
                            startActivity(zoom);

                        }
                    });

                } else {
                    Helper.noConnection(mAct, true);
                }

                if (pDialog.getVisibility() == View.VISIBLE) {
                    pDialog.setVisibility(View.GONE);
                    Helper.revealView(mListview, ll);
                }

                mSwipeRefreshLayout.setRefreshing(false);
            } catch (IllegalStateException e) {
                mSwipeRefreshLayout.setRefreshing(false);
            } catch (Exception e) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            super.onPostExecute(result);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.night_mode).setChecked(isNight_Mode);
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.rss_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.night_mode:
                isNight_Mode = !isNight_Mode;
                // Toggle the checkbox.

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
//                    color_bg = Color.parseColor("#ffffff");
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
                mSwipeRefreshLayout.setRefreshing(true);
                new MyTask().execute();
                return true;
            case R.id.info:
                //show information about the feed in general in a dialog
                if (myRssFeed != null) {
                    String FeedTitle = (myRssFeed.getTitle());
                    String FeedDescription = (myRssFeed.getDescription());
                    //String FeedPubdate = (myRssFeed.getPubdate()); most times not present
                    String FeedLink = (myRssFeed.getLink());

                    AlertDialog.Builder builder = new AlertDialog.Builder(mAct);

                    String titlevalue = getResources().getString(R.string.feed_title_value);
                    String descriptionvalue = getResources().getString(R.string.feed_description_value);
                    String linkvalue = getResources().getString(R.string.feed_link_value);

                    if (FeedLink.equals("")) {
                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
                                "\n\n" + descriptionvalue + ": \n" + FeedDescription);
                    } else {
                        builder.setMessage(titlevalue + ": \n" + FeedTitle +
                                "\n\n" + descriptionvalue + ": \n" + FeedDescription +
                                "\n\n" + linkvalue + ": \n" + FeedLink);
                    }
                    ;

                    builder.setNegativeButton(getResources().getString(R.string.ok), null)
                            .setCancelable(true);
                    builder.create();
                    builder.show();

                } else {

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

//		if (position + 1 == dotsCount) {
//			btnNext.setVisibility(View.GONE);
//			btnFinish.setVisibility(View.VISIBLE);
//		} else {
//			btnNext.setVisibility(View.VISIBLE);
//			btnFinish.setVisibility(View.GONE);
//		}
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}