package com.sieae.jamaicaobserver.rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.sieae.jamaicaobserver.MainActivity;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.rss.ui.RssDetailActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;
import android.util.Xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * RSS Service Executer
 */

public class RssService extends Service {
	
	public static final int NOTIFICATION_ID = 1;
	private RSSFeed myRssFeed = null;
    Notification.Builder builder;
    
    // -- test 
    private URL feedUrl;
	private URLConnection feedURLConnection;
	private InputStream inputStream;
	// -- end

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	   Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
	   Log.v("RSS Service", "started");
       //new Operation().execute();
	   //DoSomething(intent);
	   new ProgressFactory().execute();
	    
	   return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	    Log.v("RSS Service", "stopped");
	}
    
    private class ProgressFactory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

        		SharedPreferences spUpdate = getSharedPreferences("rssUpdate", 0);
        		String lastTitle = spUpdate.getString("lastTitle", "No feed");
        		String feedLink = spUpdate.getString("feedLink",
        				getString(R.string.rss_push_url));// "http://www.contra.gr/?widget=rssfeed&view=feed&contentId=1169269");
        		String rssFeed = "";

//			try {
//
//				URL rssUrl = new URL(feedLink);
//				SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
//				SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
//				XMLReader myXMLReader = mySAXParser.getXMLReader();
//				RSSHandler myRSSHandler = new RSSHandler();
//				myXMLReader.setContentHandler(myRSSHandler);
//				InputSource myInputSource = new InputSource(rssUrl.openStream());
//				myXMLReader.parse(myInputSource);
//
//				myRssFeed = myRSSHandler.getFeed();
//				if (myRssFeed != null) {
//					String title = myRssFeed.getTitle();
//					String summary = myRssFeed.getDescription();
//					String feedTitle= myRssFeed.getTitle();
//					String url = myRssFeed.getLink();
//					String date = myRssFeed.getPubdate();
//
//					if (!lastTitle.equalsIgnoreCase(title)) {
//        				showNotification(title, summary, feedTitle, url, date);
//        				// store last title so next time will not notify for same rss
//        				Editor editor = spUpdate.edit();
//        				editor.putString("lastTitle", title);
//        				editor.commit();
//        			}
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (ParserConfigurationException e) {
//				e.printStackTrace();
//			} catch (SAXException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}


        		// Initialize Connection
        		try {
        			feedUrl = new URL(feedLink);
        			feedURLConnection = feedUrl.openConnection();
        			inputStream = feedURLConnection.getInputStream();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        		String title = "";
        		String summary = "";
        		String feedTitle= "";
				String url = "";
				String date = "";
				String image = "";
        		try {
        			CharSequence[] entry = getEntry(rssFeed);
        			title = (String) entry[0];
					title = (String)Html.fromHtml(title).toString();
        			summary = (String) entry[1];
        			feedTitle=(String) entry[2];
					url=(String)entry[3];
					image=(String)entry[4];
					date=(String)entry[5];
        			if (!lastTitle.equalsIgnoreCase(title)) {
						showNotification(title, summary, feedTitle, url, date, image);
        				// store last title so next time will not notify for same rss
        				Editor editor = spUpdate.edit();
        				editor.putString("lastTitle", title);
        				editor.commit();
        			}
        		} catch (XmlPullParserException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		try {
        			inputStream.close();
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) { }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

	private CharSequence[] getEntry(String rssFeed)
			throws XmlPullParserException, IOException {
		CharSequence[] title = { "Notify Error", "Turn off notifications", "Turn off notifications",
				"link", "image url", "pubdate"};
		XmlPullParser rssParser = Xml.newPullParser();
		rssParser.setInput(inputStream, null);

		// Parse the XML
		int eventType = -1;
		boolean foundEntry = false;
		boolean firstEntry = false;

		while (eventType != XmlPullParser.END_DOCUMENT && !foundEntry) {
			if (eventType == XmlPullParser.START_TAG) {

				String strName = rssParser.getName();
				if(!firstEntry && strName.equals("title")) {
					title[2] = rssParser.nextText();
				} else if(strName.equals("item") || strName.equals("entry")) {
					firstEntry = true;
				} else if (firstEntry) {
					if (strName.equalsIgnoreCase("title")) {
						title[0] = rssParser.nextText();
					} else if (strName.equalsIgnoreCase("description") || strName.equalsIgnoreCase("summary")) {
						title[1] = rssParser.nextText();
					} else if (strName.equalsIgnoreCase("link")) {
						title[3] = rssParser.nextText();
					} else if (strName.equalsIgnoreCase("image")) {
						title[4] = rssParser.nextText();
					} else if (strName.equalsIgnoreCase("pubDate")) {
						title[5] = rssParser.nextText();
					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				String strName = rssParser.getName();
				if (strName.equals("item")|| strName.equals("entry"))
					foundEntry = true;
			}
			eventType = rssParser.next();
		}
		return title;
	}

	private void showNotification(String title, String summary, String feedTitle, String url, String date, String image) {
		Bitmap icon = BitmapFactory.decodeResource(getResources(),  
                R.drawable.ic_launcher);  
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher).setLargeIcon(icon)
				.setContentTitle(feedTitle) //
//				.setContentTitle(title)
//				.setTicker(title)
				.setContentText(title) //
				.setAutoCancel(true);
		
		NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();  
        bigText.bigText(Html.fromHtml(summary));
        bigText.setBigContentTitle(title);
        bigText.setSummaryText(feedTitle);
//		bigText.setSummaryText(title);
        mBuilder.setStyle(bigText);

		Intent resultIntent;
		if(date==null){
			// Creates an Intent that shows the title and a description of the feed
			resultIntent = new Intent(this, MainActivity.class);
			resultIntent.putExtra("title", title);
			//resultIntent.putExtra("summary", summary);

		}else{
//			resultIntent = RssDetailActivity.getLaunchIntent(this, title,summary, url, date);
			RSSItem rssItem = new RSSItem(title, summary, url, date);
			rssItem.setThumburl(image);
			rssItem.setIsRead(true);
			RSSFeed rssFeed = new RSSFeed(rssItem);
			resultIntent = RssDetailActivity.getLaunchIntent(this, rssFeed, 0);
		}


		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		if(date==null){
			stackBuilder.addParentStack(MainActivity.class);
		}else{
			stackBuilder.addParentStack(RssDetailActivity.class);
		}

		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	
}