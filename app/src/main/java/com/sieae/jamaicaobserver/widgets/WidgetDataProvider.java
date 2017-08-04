package com.sieae.jamaicaobserver.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.rss.RSSItem;

import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_DATA;
import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_TYPE;

@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsFactory {

	Context mContext = null;
	private String type, data;
	List<RSSItem> mCollections = new ArrayList<RSSItem>();
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
//	int [] appWidgetIds;
private SharedPreferences sharedPreferences;
	public WidgetDataProvider(Context context, Intent intent) {
//		appWidgetIds = new int[1];
		mContext = context;
		appWidgetId = intent.getIntExtra(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
//		appWidgetIds[0] = appWidgetId;
//		type = intent.getExtras().getString(KEY_TYPE);
//		data = intent.getExtras().getString(KEY_DATA);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		type = sharedPreferences.getString(KEY_TYPE, null);
		data = sharedPreferences.getString(KEY_DATA, null);
	}

	private void populateListItem() {
		if(JamaicaAppWidgetConfigure.getmCollections() !=null )
			mCollections = JamaicaAppWidgetConfigure.getmCollections();
//			mCollections = (ArrayList<RSSItem>) RemoteFetchService.mCollections;
//					.clone();
		else
			mCollections = new ArrayList<RSSItem>();


		if(mCollections.size()==0){
			Intent serviceIntent = new Intent(mContext, RemoteFetchService.class);
			serviceIntent
					.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			serviceIntent.putExtra(KEY_TYPE,type);
			serviceIntent.putExtra(KEY_DATA,data);
			mContext.startService(serviceIntent);
		}
	}

	@Override
	public int getCount() {
		return mCollections.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {

		RemoteViews mView = new RemoteViews(mContext.getPackageName(),
				R.layout.widget_gridview_items);

		if(mCollections.size()==0)return mView;
		final String title = Html.fromHtml(mCollections.get(position).getTitle()).toString();
//		final String link = mCollections.get(position).getLink();
//		final String description = mCollections.get(position).getRowDescription();
//		final String date = mCollections.get(position).getPubdate().trim();
		String thumburl = mCollections.get(position).getThumburl();
		String array_thumburl[] = thumburl.split(" ");


		mView.setTextViewText(R.id.widgetheading, title); //mCollections.get(position)
		mView.setTextColor(R.id.widgetheading, Color.BLACK);

//		mView.setImageViewResource(R.id.widgetnewspic, R.drawable.ic_launcher);

		if (array_thumburl.length > 0) {
			mView.setImageViewBitmap(R.id.widgetnewspic, getBitmapFromURL(array_thumburl[0]));
//			Picasso.with(mContext).load(array_thumburl[0]).into(mView,R.id.widgetnewspic,appWidgetIds);
		} else {
			mView.setImageViewBitmap(R.id.widgetnewspic, getBitmapFromURL(thumburl));
//			Picasso.with(mContext).load(thumburl).into(mView,R.id.widgetnewspic,appWidgetIds);
		}

		final Intent fillInIntent = new Intent();
		fillInIntent.setAction(WidgetProvider.ACTION_RSSDETAIL);
		final Bundle bundle = new Bundle();
		bundle.putInt(WidgetProvider.EXTRA_INT_POSITION, position);
		bundle.putString(WidgetProvider.EXTRA_STRING,
				String.valueOf(position)); // mCollections.get(position)
		fillInIntent.putExtras(bundle);
		mView.setOnClickFillInIntent(R.id.stackWidgetItem, fillInIntent);
		return mView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
//		populateListItem();
		//initData();
	}

	@Override
	public void onDataSetChanged() {
		populateListItem();
//		initData();
	}

//	private void initData() {
////		mCollections.clear();
////		for (int i = 1; i <= 10; i++) {
////			mCollections.add("ListView item "+String.valueOf(i));
////		}
//
////		List<RSSItem> mColl = WidgetProvider.mCollections;
////		mCollections.addAll(mColl);
//
//
////		new GetNews().execute(data);
//	}

	@Override
	public void onDestroy() {

	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}

}
