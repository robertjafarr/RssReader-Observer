package com.sieae.jamaicaobserver.rss.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSHandler;
import com.sieae.jamaicaobserver.rss.RSSItem;

import org.parceler.Parcels;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import com.adtech.mobilesdk.publisher.configuration.AdtechAdConfiguration;
//import com.adtech.mobilesdk.publisher.view.AdtechBannerView;
//import com.adtech.mobilesdk.publisher.view.AdtechInterstitialView;

/**
 *  This activity is used to display details of a rss item
 */

public class RssDetailActivity extends ActionBarActivity {

	private static final String KEY_FEED = "feed";
	private static final String KEY_POSITION = "position";

	private static final String KEY_FAV_RSS = "is_fav_rss";
	private static final String KEY_FAV_TITLE = "fav_title";
	private static final String KEY_FAV_BODY = "fav_body";
	private static final String KEY_FAV_URL = "fav_url";
	private static final String KEY_FAV_DATE = "fav_date";
	private HashMap<String, String> topiclist = new HashMap<String, String>();
	private RSSFeed rssFeed;
//	AdtechBannerView banner;
	private AdView adView;
	private InterstitialAd mInterstitialAd;
	private String topicfeedurl;
	private RSSFeed myRssFeed = null;
	private List<RSSItem> list;
	private String heading;

	public static Intent getLaunchIntent(Context context, RSSFeed feed, int position) {
		Intent intent = new Intent(context, RssDetailActivity.class);
		intent.putExtra(KEY_FEED, Parcels.wrap(feed));
		intent.putExtra(KEY_POSITION, position);
		return intent;
	}

	public static Intent getLaunchIntent(Context context, String title, String body, String url, String date) {
		Intent intent = new Intent(context, RssDetailActivity.class);
		intent.putExtra(KEY_FAV_RSS, true);
		intent.putExtra(KEY_FAV_TITLE, title);
		intent.putExtra(KEY_FAV_BODY, body);
		intent.putExtra(KEY_FAV_URL, url);
		intent.putExtra(KEY_FAV_DATE, date);
		return intent;
	}

	@SuppressLint("SetJavaScriptEnabled")@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rss_details);
//		banner = (AdtechBannerView) findViewById(R.id.ad_container);
//		final AdtechAdConfiguration config = new AdtechAdConfiguration("AppName");
//		config.setDomain("a.adtechus.com");
//		config.setAlias("jaobsmainarticlembt_android");
//		config.setNetworkId(5469);
//		config.setSubnetworkId(1);
//		config.enableImageBannerResize(false);
//		banner.setAdConfiguration(config);

		// AdMob ad
		adView = (AdView) this.findViewById(R.id.detailAdView);
		setAd();

		// Interstitial Ad
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}

			@Override
			public void onAdFailedToLoad(int i) {
				super.onAdFailedToLoad(i);
			}

			@Override
			public void onAdClosed() {
				requestNewInterstitial();
			}
		});

		requestNewInterstitial();

		// data: http://www.jamaicaobserver.com/latestnews/Gov-t-to-sign--sister-airport--agreement-with-Atlanta-airport
		// Url "http://www.jamaicaobserver.com/latestnews/Gov-t-to-sign--sister-airport--agreement-with-Atlanta-airport"
		// Path object now is equal to "near=Mountain%20View&q=Dinner"
		Intent intent = getIntent();
		Uri data = intent.getData();
		if (data!=null){
//			String path = data.getPath();
			setTopics();
			String flt1 = data.toString().replace("http://www.jamaicaobserver.com/", ""); // latestnews/Gov-t-to-sign--sister-airport--agreement-with-Atlanta-airport
			String [] split1 = flt1.split("/");
			if(split1.length==2){
				String topic = split1[0];
				heading = split1[1];
				if (topiclist.containsKey(topic)){
					topicfeedurl = topiclist.get(topic);
				}

				if(topicfeedurl!=null){
					new FetchFeeds().execute();
				}
			}
		}else{
			loadData(intent);
		}


		

	}

	private void loadData(Intent intent){
		boolean isFavRSS = intent.getBooleanExtra(KEY_FAV_RSS, false);
		if (isFavRSS) {
			// create new RSS feed object from fav data
			String title = getIntent().getStringExtra(KEY_FAV_TITLE);
			String description = getIntent().getStringExtra(KEY_FAV_BODY);
			String link = getIntent().getStringExtra(KEY_FAV_URL);
			String pubDate = getIntent().getStringExtra(KEY_FAV_DATE);

			RSSItem rssItem = new RSSItem(title, description, link, pubDate);
			rssFeed = new RSSFeed(rssItem);
		}
		else
			rssFeed = Parcels.unwrap(getIntent().getParcelableExtra(KEY_FEED));

		if (rssFeed != null && rssFeed.getList() != null) {
			int position = getIntent().getIntExtra(KEY_POSITION, 0);
			ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
			PagerAdapter adapter = new FeedPagerAdapter();
			viewPager.setOffscreenPageLimit(1);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(position, false);
			viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_detail_margin));
			viewPager.setPageMarginDrawable(new ColorDrawable(getResources().getColor(R.color.light_gray)));

			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					setAd();
				}

				@Override
				public void onPageSelected(int position) {
					if((position+1)%8==0){
						showRewardAd();
					}
				}

				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
		}
	}

	private void setAd(){

		AdRequest adRequest = new AdRequest.Builder()
				.build();
		adView.loadAd(adRequest);
	}
	private class FeedPagerAdapter extends FragmentStatePagerAdapter {
		public FeedPagerAdapter() {
			super(getSupportFragmentManager());
		}

		@Override
		public Fragment getItem(int i) {
			return RssDetailFragment.newInstance(rssFeed.getList().get(i));
		}

		@Override
		public int getCount() {
			return rssFeed.getList().size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return (position + 1) + " of " + getCount();
		}
	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
				.build();

		mInterstitialAd.loadAd(adRequest);
	}

	public void showRewardAd() {
		if (mInterstitialAd != null) {
			if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
		}
	}

	private void setTopics(){
		topiclist.put("latestnews", "http://www.jamaicaobserver.com/app/latest");
		topiclist.put("news", "http://www.jamaicaobserver.com/app/news");
		topiclist.put("business", "http://www.jamaicaobserver.com/app/business");
		topiclist.put("sport", "http://www.jamaicaobserver.com/app/sport");
		topiclist.put("lifestyle", "http://www.jamaicaobserver.com/app/lifestyle");

		topiclist.put("allwoman", "http://www.jamaicaobserver.com/app/allwoman");
		topiclist.put("entertainment", "http://www.jamaicaobserver.com/app/entertainment");
		topiclist.put("editorial", "http://www.jamaicaobserver.com/app/editorial");
		topiclist.put("columns", "http://www.jamaicaobserver.com/app/columns");
		topiclist.put("career", "http://www.jamaicaobserver.com/app/career");

		topiclist.put("food", "http://www.jamaicaobserver.com/app/food");
		topiclist.put("teenage", "http://www.jamaicaobserver.com/app/teenage");
		topiclist.put("auto", "http://www.jamaicaobserver.com/app/auto");
		topiclist.put("environment", "http://www.jamaicaobserver.com/app/environment");
		topiclist.put("cartoon", "http://www.jamaicaobserver.com/app/cartoons");
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		banner.load();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		banner.stop();
//	}

	private class FetchFeeds extends AsyncTask<Void, Void, Void> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(RssDetailActivity.this,"", "Loading...");
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {

				URL rssUrl = new URL(topicfeedurl);
				SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
				XMLReader myXMLReader = mySAXParser.getXMLReader();
				RSSHandler myRSSHandler = new RSSHandler();
				InputSource myInputSource = new InputSource(rssUrl.openStream());
				myInputSource.setEncoding("iso-8859-1");


				mySAXParser.parse(myInputSource, myRSSHandler);

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
				pd.dismiss();

				if (myRssFeed != null) {
					list = myRssFeed.getList();

				}
				//heading: Dominica-PM-says-his-government-will-not-be-overthrown
				if (list!=null){
					for (int p=0; p<list.size(); p++){
						try {
							RSSItem rssItem = list.get(p);
							String link = rssItem.getLink();
							String flt1 = link.replace("http://www.jamaicaobserver.com/", "");
							String [] split1 = flt1.split("/");
							String title = null;
							if(split1.length==2){
                                String topic = split1[0];
                                title = split1[1];
                                if (title.contentEquals(heading)){
                                    startActivity(RssDetailActivity.getLaunchIntent(RssDetailActivity.this, myRssFeed, p));
                                    finish();
                                }
                            }
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			} catch (IllegalStateException e) {

			} catch (Exception e) {

			}
			super.onPostExecute(result);
		}

	}

}



