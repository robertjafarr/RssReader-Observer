package com.sieae.jamaicaobserver.rss.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sieae.jamaicaobserver.DBHelper;
import com.sieae.jamaicaobserver.GlobalVariables;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.fav.FavDbAdapter;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.sieae.jamaicaobserver.util.WebHelper;
import com.sieae.jamaicaobserver.web.WebviewActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *  This activity is used to display details of a rss item
 */

public class RssDetailFragment extends android.support.v4.app.Fragment implements ViewPager.OnPageChangeListener {

	public static final String KEY_RSS_ITEM = "rss_item";

	private RSSItem rssItem;
	private Activity mAct;
	private WebView wb;
	private FavDbAdapter mDbHelper;
	private Toolbar mToolbar;

	String date;
	String link;
	String title;
	String description;
	String favorite;
	String listThumb;
	private boolean isNight_Mode = false;
	private View mMainView;
	private SharedPreferences mSettings;
	private String html;
	private SlidePagerAdapter mPagerAdapter;
	private int dotsCount;
	private ImageView[] dots;
	private DBHelper db;

	public static RssDetailFragment newInstance(RSSItem rssItem) {
		RssDetailFragment fragment = new RssDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_RSS_ITEM, Parcels.wrap(rssItem));
		fragment.setArguments(bundle);
		return fragment;
	}

	@SuppressLint("SetJavaScriptEnabled")@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rssItem = Parcels.unwrap(getArguments().getParcelable(KEY_RSS_ITEM));
		mAct = getActivity();
		mSettings = PreferenceManager.getDefaultSharedPreferences(mAct);
		isNight_Mode = mSettings.getBoolean("isNight_Mode", false);

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
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		mMainView = inflater.inflate(R.layout.frag_rss_details, container, false); //fragment_rss_details
		setHasOptionsMenu(true);

		mToolbar = (Toolbar) mMainView.findViewById(R.id.toolbar_actionbar);
		((RssDetailActivity)getActivity()).setSupportActionBar(mToolbar);
		((RssDetailActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((RssDetailActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((RssDetailActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
		((RssDetailActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAct.onBackPressed();
			}
		});

		((RssDetailActivity)getActivity()).getSupportActionBar().invalidateOptionsMenu();
		int color_bg;
		if(isNight_Mode){
			color_bg = Color.parseColor("#000000");
		}else{
			color_bg = Color.parseColor("#ffffff");
		}
		mMainView.setBackgroundColor(color_bg);

		return mMainView;

//		View view = inflater.inflate(R.layout.frag_rss_details, container, false); //fragment_rss_details
//		setHasOptionsMenu(true);
//
//		mToolbar = (Toolbar) view.findViewById(R.id.toolbar_actionbar);
//		((RssDetailActivity)getActivity()).setSupportActionBar(mToolbar);
//		((RssDetailActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//		((RssDetailActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView detailsTitle = (TextView) view.findViewById(R.id.detailstitle);
		TextView detailsPubdate = (TextView) view.findViewById(R.id.detailspubdate);
		ImageView detailsThumbnail = (ImageView) view.findViewById(R.id.listthumb);
		final ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager_details);
		final LinearLayout pager_indicator = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);
		ImageView slide_show =(ImageView)view.findViewById(R.id.slide_show);
//		ScrollView scroll_view = (ScrollView)view.findViewById(R.id.scroll_view);

//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				R.drawable.header);
//
//		Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//			@Override
//			public void onGenerated(Palette palette) {
//
//				mutedColor = palette.getMutedColor(R.attr.colorPrimary);
//				collapsingToolbar.setContentScrimColor(mutedColor);
//			}
//		});

		viewPager.setOnPageChangeListener(RssDetailFragment.this);

		detailsTitle.setText(Html.fromHtml(rssItem.getTitle()));
		detailsPubdate.setText(rssItem.getPubdate());

		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a");
		Date dat = null;
		try
		{
			dat = sdf.parse(rssItem.getPubdate());
			SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
			String newFormat = formatter.format(dat);
			detailsPubdate.setText(newFormat);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		date = rssItem.getPubdate();
		link = rssItem.getLink();
		title = Html.fromHtml(rssItem.getTitle()).toString();
		description = rssItem.getDescription();//Html.fromHtml(rssItem.getDescription()).toString();


		String new_url = link.replace("http://","");
		String [] url_splite = new_url.split("/");
		String heading = url_splite[url_splite.length-1];
		if(rssItem.isRead())
		{
			String [] Columns = {
					GlobalVariables.COLUMN_HEADING

			};

			String [] ColumnsValue = {
					heading

			};

			long row = db.adddata(GlobalVariables.DATABASE_TABLE, Columns, ColumnsValue);

		}

		listThumb = rssItem.getThumburl();
		try {
			String thumburl = rssItem.getThumburl();
//			thumburl = thumburl.replaceAll("jpghttp", "jpg http");
			String array_thumburl [] = thumburl.split(" ");
			if(array_thumburl.length>0){
				mPagerAdapter = new SlidePagerAdapter(mAct, array_thumburl);
				viewPager.setAdapter(mPagerAdapter);
			}else{
				mPagerAdapter = new SlidePagerAdapter(mAct, thumburl);
				viewPager.setAdapter(mPagerAdapter);
			}

			pager_indicator.removeAllViews();

			if(array_thumburl.length>1) {
				slide_show.setVisibility(View.VISIBLE);
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

					pager_indicator.addView(dots[i], params);
				}

				dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
			}else{
				slide_show.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}



		wb = (WebView) view.findViewById(R.id.descriptionwebview);

//		int color_bg;
//		String color_text;
//		if(isNight_Mode){
//			color_bg = Color.parseColor("#000000");
//			color_text = "white";
//		}else{
//			color_bg = Color.parseColor("#ffffff");
//			color_text = "black";
//		}

		reLoadWebView();

//		//parse the html and apply some styles
//		Document doc = Jsoup.parse(description);
//		html = WebHelper.docToBetterHTML(doc, mAct);;
//		html = html.replace("<body>","<body><font color=\""+color_text+"\">");
//		html = html.replace("</body>","</font></body>");
//		wb.getSettings().setJavaScriptEnabled(true);
//		wb.loadDataWithBaseURL(link, html , "text/html", "UTF-8", "");
//		Log.v("INFO", "Wordpress HTML: " + html);
//		wb.setBackgroundColor(Color.argb(1, 0, 0, 0));
//		wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//		wb.getSettings().setDefaultFontSize(WebHelper.getWebViewFontSize(mAct));
//		wb.setWebViewClient(new WebViewClient(){
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				if (url != null && (url.startsWith("http://") || url.startsWith("http://"))) {
//					Intent mIntent = new Intent(mAct, WebviewActivity.class);
//					mIntent.putExtra(WebviewActivity.URL, url);
//					startActivity(mIntent);
//					return true;
//				} else {
//					Uri uri = Uri.parse(url);
//					Intent ViewIntent = new Intent(Intent.ACTION_VIEW, uri);
//
//					// Verify it resolves
//					PackageManager packageManager = mAct.getPackageManager();
//					List<ResolveInfo> activities = packageManager.queryIntentActivities(ViewIntent, 0);
//					boolean isIntentSafe = activities.size() > 0;
//
//					// Start an activity if it's safe
//					if (isIntentSafe) {
//						startActivity(ViewIntent);
//					}
//					return true;
//				}
//			}
//		});

		if ((getResources().getString(R.string.ad_visibility).equals("0"))) {
			// Look up the AdView as a resource and load a request.
			AdView adView = (AdView) view.findViewById(R.id.adView);
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		Button btnOpen = (Button) view.findViewById(R.id.openbutton);

		//Listening to button event
		btnOpen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(link));
				startActivity(intent);

			}
		});

//		Button btnFav = (Button) view.findViewById(R.id.favoritebutton);
//
//		//Listening to button event
//		btnFav.setOnClickListener(new View.OnClickListener() {
//
//			public void onClick(View arg0) {
//				mDbHelper = new FavDbAdapter(mAct);
//				mDbHelper.open();
//
//				if (mDbHelper.checkEvent(title, description, date, link, "", "", "rss")) {
//					// Item is new
//					mDbHelper.addFavorite(title, description, date, link, "", "", "rss");
//					Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_success), Toast.LENGTH_LONG);
//					toast.show();
//				} else {
//					Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_duplicate), Toast.LENGTH_LONG);
//					toast.show();
//				}
//			}
//		});

	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.night_mode:
				isNight_Mode = !isNight_Mode;
				SharedPreferences.Editor editor = mSettings.edit();
				editor.putBoolean("isNight_Mode", isNight_Mode);
				editor.commit();
				// Toggle the checkbox.
				mAct.invalidateOptionsMenu();

				int color_bg;
				if(isNight_Mode){
					color_bg = Color.parseColor("#000000");
				}else{
					color_bg = Color.parseColor("#ffffff");
				}
				mMainView.setBackgroundColor(color_bg);
				reLoadWebView();
//				mAdapter.notifyDataSetChanged();
				return true;
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
//				NavUtils.navigateUpFromSameTask(mAct);
//				mAct.onBackPressed();
				mAct.finish();
				return true;
			case R.id.share:
				String html = description;
				html = html.replaceAll("<(.*?)\\>", ""); //Removes all items in brackets
				html = html.replaceAll("<(.*?)\\\n", ""); //Must be undeneath
				html = html.replaceFirst("(.*?)\\>", ""); //Removes any connected item to the last bracket
				html = html.replaceAll("&nbsp;", "");
				html = html.replaceAll("&amp;", "");
				html = html.replaceAll("&lsquo;", "‘");

//				String linkvalue = getResources().getString(R.string.item_share_begin);
//				String seenvalue = getResources().getString(R.string.item_share_middle);
//				String appvalue = getResources().getString(R.string.item_share_end);
//				String applicationName = getResources().getString(R.string.app_name);
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				//this is the text that will be shared
//				sendIntent.putExtra(Intent.EXTRA_TEXT, (html + linkvalue + link + seenvalue + applicationName + appvalue));
				sendIntent.putExtra(Intent.EXTRA_TEXT, (title + "\n"+link));
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, title); //you can replace title with a string of your choice
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.share_header)));
				return true;
			case R.id.menu_favorite:
				mDbHelper = new FavDbAdapter(mAct);
				mDbHelper.open();

				if (mDbHelper.checkEvent(title, description, date, link, "", "", "rss")) {
					// Item is new
					mDbHelper.addFavorite(title, description, date, link, "", "", "rss");
					Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_success), Toast.LENGTH_LONG);
					toast.show();
				} else {
					Toast toast = Toast.makeText(mAct, getResources().getString(R.string.favorite_duplicate), Toast.LENGTH_LONG);
					toast.show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.rss_detail_menu, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.night_mode).setChecked(isNight_Mode);
		super.onPrepareOptionsMenu(menu);

	}

	private void reLoadWebView(){
		int color_bg;
		String color_text;
		if(isNight_Mode){
			color_bg = Color.parseColor("#000000");
			color_text = "white";
		}else{
			color_bg = Color.parseColor("#ffffff");
			color_text = "black";
		}
		//parse the html and apply some styles
		Document doc = Jsoup.parse(description);
		html = WebHelper.docToBetterHTML(doc, mAct);;
		html = html.replace("<body>","<body><font color=\""+color_text+"\">");
		html = html.replace("</body>","</font></body>");
		html = html.replace("<br />","<br /><br />");
		wb.getSettings().setJavaScriptEnabled(true);
		wb.loadDataWithBaseURL(link, html , "text/html", "UTF-8", "");
		Log.v("INFO", "Wordpress HTML: " + html);
		wb.setBackgroundColor(Color.argb(1, 0, 0, 0));
		wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wb.getSettings().setDefaultFontSize(WebHelper.getWebViewFontSize(mAct));
		wb.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url != null && (url.startsWith("http://") || url.startsWith("http://"))) {
					Intent mIntent = new Intent(mAct, WebviewActivity.class);
					mIntent.putExtra(WebviewActivity.URL, url);
					startActivity(mIntent);
					return true;
				} else {
					Uri uri = Uri.parse(url);
					Intent ViewIntent = new Intent(Intent.ACTION_VIEW, uri);

					// Verify it resolves
					PackageManager packageManager = mAct.getPackageManager();
					List<ResolveInfo> activities = packageManager.queryIntentActivities(ViewIntent, 0);
					boolean isIntentSafe = activities.size() > 0;

					// Start an activity if it's safe
					if (isIntentSafe) {
						startActivity(ViewIntent);
					}
					return true;
				}
			}
		});

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

	class SlidePagerAdapter extends PagerAdapter {
		Activity activity;
		String[]  imageArray;

		public SlidePagerAdapter(Activity act, String[] imgArra) {
			imageArray = imgArra;
			activity = act;

		}

		public SlidePagerAdapter(Activity act, String imgArra) {
			imageArray = new String[1];
			imageArray[0] = imgArra;
			activity = act;

		}

		public int getCount() {
			return imageArray.length;
		}

		public Object instantiateItem(View collection, int position) {
			LayoutInflater inflater = activity.getLayoutInflater();
			ImageView rootView = (ImageView) inflater.inflate(R.layout.myimageview, null);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.CENTER;
			params.setMargins(5, 5, 5, 5);

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

	public interface OnArticleReadListener{
		void setOnArticleReadListener(boolean isRead);
	}
}



