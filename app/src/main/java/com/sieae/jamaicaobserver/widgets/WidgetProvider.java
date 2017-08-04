package com.sieae.jamaicaobserver.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.rss.ui.RssDetailActivity;


public class WidgetProvider extends AppWidgetProvider {

//	public static final String ACTION_TOAST = "com.dharmangsoni.widgets.ACTION_TOAST";
	public static final String ACTION_RSSDETAIL = "com.dharmangsoni.widgets.ACTION_RSSDETAIL";
	public static final String EXTRA_STRING = "com.dharmangsoni.widgets.EXTRA_STRING";
	public static final String EXTRA_INT_POSITION = "com.dharmangsoni.widgets.EXTRA_INT_POSITION";
	public static final String DATA_FETCHED = "com.sieae.jamaicaobserver.DATA_FETCHED";
	public static final String KEY_TYPE = "type";
	public static final String KEY_DATA = "data";
	private String type, data;
	private Context mContext;

	/*
	 * this method is called every 30 mins as specified on widgetinfo.xml this
	 * method is also called on every phone reboot from this method nothing is
	 * updated right now but instead RetmoteFetchService class is called this
	 * service will fetch data,and send broadcast to WidgetProvider this
	 * broadcast will be received by WidgetProvider onReceive which in turn
	 * updates the widget
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
						 int[] appWidgetIds) {

		mContext = context;
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {

//			type = JamaicaAppWidgetConfigure.type;
//			data = JamaicaAppWidgetConfigure.data;
//
			int appWidgetId = appWidgetIds[i];
			RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

//			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetCollectionList);

			Intent serviceIntent = new Intent(context, RemoteFetchService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
//			serviceIntent.putExtra(KEY_TYPE,JamaicaAppWidgetConfigure.type);
//			serviceIntent.putExtra(KEY_DATA,JamaicaAppWidgetConfigure.data);
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			type = sharedPreferences.getString(KEY_TYPE, null);
			data = sharedPreferences.getString(KEY_DATA, null);
			serviceIntent.putExtra(KEY_TYPE,type);
			serviceIntent.putExtra(KEY_DATA,data);

			serviceIntent.putExtra("onUpdate",true);
//			Toast.makeText(context, JamaicaAppWidgetConfigure.type+"\n"+JamaicaAppWidgetConfigure.data, Toast.LENGTH_LONG).show();
			context.startService(serviceIntent);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

		// which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_provider_layout);

		// RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, WidgetService.class);
		// passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// setting a unique Uri to the intent
		// don't know its purpose to me right now
		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		// setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(R.id.widgetCollectionList,
				svcIntent);

		String name = context.getResources().getString(R.string.app_name)+" - "+type;
		remoteViews.setTextViewText(R.id.txvWidgetTitle, name);
		// setting an empty view in case of no data
		remoteViews.setEmptyView(R.id.widgetCollectionList, R.id.empty_view);

		// Adding collection list item handler
		final Intent onItemClick = new Intent(context, WidgetProvider.class);
		onItemClick.setAction(ACTION_RSSDETAIL);
		onItemClick.setData(Uri.parse(onItemClick
				.toUri(Intent.URI_INTENT_SCHEME)));
		final PendingIntent onClickPendingIntent = PendingIntent
				.getBroadcast(context, 0, onItemClick,
						PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.widgetCollectionList,
				onClickPendingIntent);

		return remoteViews;
	}

	/*
	 * It receives the broadcast as per the action set on intent filters on
	 * Manifest.xml once data is fetched from RemotePostService,it sends
	 * broadcast and WidgetProvider notifies to change the data the data change
	 * right now happens on ListProvider as it takes RemoteFetchService
	 * listItemList as data
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(DATA_FETCHED)) {
			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
//			type = intent.getExtras().getString(KEY_TYPE);
//			data = intent.getExtras().getString(KEY_DATA);
//			type = JamaicaAppWidgetConfigure.type;
//			data = JamaicaAppWidgetConfigure.data;
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			type = sharedPreferences.getString(KEY_TYPE, null);
			data = sharedPreferences.getString(KEY_DATA, null);

			RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

//			Intent initialUpdateIntent = new Intent(
//					AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			initialUpdateIntent
//					.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			context.sendBroadcast(initialUpdateIntent);

//			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetCollectionList);


			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,  R.id.widgetCollectionList);
//			Log.e("finally after a whole day", "working :");
		}
		else if (intent.getAction().equals(ACTION_RSSDETAIL)) {

			int position = intent.getIntExtra(EXTRA_INT_POSITION, 0);

			RemoteFetchService.myRssFeed.getItem(position).setIsRead(true);
			Intent intent1 = RssDetailActivity.getLaunchIntent(context, RemoteFetchService.myRssFeed, position);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		}

	}

//	@Override
//	public void onReceive(Context context, Intent intent) {
//		if (intent.getAction().equals(ACTION_TOAST)) {
//			String item = intent.getExtras().getString(EXTRA_STRING);
////			type = intent.getExtras().getString(KEY_TYPE);
////			data = intent.getExtras().getString(KEY_DATA);
//			Toast.makeText(context, item, Toast.LENGTH_LONG).show();
//		}
//		super.onReceive(context, intent);
//	}


}
