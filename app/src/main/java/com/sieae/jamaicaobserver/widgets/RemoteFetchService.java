package com.sieae.jamaicaobserver.widgets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.sieae.jamaicaobserver.rss.RSSFeed;
import com.sieae.jamaicaobserver.rss.RSSHandler;
import com.sieae.jamaicaobserver.rss.RSSItem;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import static com.sieae.jamaicaobserver.widgets.WidgetProvider.ACTION_TOAST;
import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_DATA;
import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_TYPE;

public class RemoteFetchService extends Service {

	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	private String type, data;
	public static List<RSSItem> mCollections = new ArrayList<RSSItem>();
	public static RSSFeed myRssFeed = null;
	private SharedPreferences sharedPreferences;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/*
	 * Retrieve appwidget id from intent it is needed to update widget later
	 * initialize our AQuery class
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
			appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

//			type = intent.getExtras().getString(KEY_TYPE);
//			data = intent.getExtras().getString(KEY_DATA);
			type = sharedPreferences.getString(KEY_TYPE, null);
			data = sharedPreferences.getString(KEY_DATA, null);

			boolean onUpdate = intent.getExtras().getBoolean("onUpdate", false);
			Toast.makeText(getApplicationContext(), "onUpdate: "+onUpdate+"\ndata: "+data, Toast.LENGTH_LONG).show();
		}

		if(data!=null)
			fetchDataFromWeb();

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * method which fetches data(json) from web aquery takes params
	 * remoteJsonUrl = from where data to be fetched String.class = return
	 * format of data once fetched i.e. in which format the fetched data be
	 * returned AjaxCallback = class to notify with data once it is fetched
	 */
	private void fetchDataFromWeb() {
		new GetNews().execute(data);

	}

	/**
	 * Method which sends broadcast to WidgetProvider
	 * so that widget is notified to do necessary action
	 * and here action == WidgetProvider.DATA_FETCHED
	 */
	private void populateWidget() {

		Intent widgetUpdateIntent = new Intent();
		widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
		widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		widgetUpdateIntent.putExtra(KEY_TYPE,type);
		widgetUpdateIntent.putExtra(KEY_DATA,data);
		sendBroadcast(widgetUpdateIntent);

		this.stopSelf();
	}

	private class GetNews extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				String weburl = arg0[0];
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

				mCollections = myRssFeed.getList();
				JamaicaAppWidgetConfigure.setmCollections(mCollections);
				populateWidget();

			} catch (IllegalStateException e) {

			} catch (Exception e) {

			}
			super.onPostExecute(result);
		}

	}
}
