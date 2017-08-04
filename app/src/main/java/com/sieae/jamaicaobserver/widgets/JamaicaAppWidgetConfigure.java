package com.sieae.jamaicaobserver.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sieae.jamaicaobserver.NavItem;
import com.sieae.jamaicaobserver.R;
import com.sieae.jamaicaobserver.cartoons.CartoonsFragment;
import com.sieae.jamaicaobserver.rss.RSSItem;
import com.sieae.jamaicaobserver.rss.ui.RssFragment;

import java.util.ArrayList;
import java.util.List;

//import static com.sieae.jamaicaobserver.widgets.WidgetProvider.ACTION_TOAST;
import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_DATA;
import static com.sieae.jamaicaobserver.widgets.WidgetProvider.KEY_TYPE;

/**
 * Created by Mohammad Arshi Khan on 02-10-2016.
 */

public class JamaicaAppWidgetConfigure extends AppCompatActivity{
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID ;
    private List<NavItem> mConfiguration = new ArrayList<NavItem>();
    public static String type, data;
    public static List<RSSItem> mCollections;

    private SharedPreferences sharedPreferences;
    public static List<RSSItem> getmCollections() {
        return mCollections;
    }

    public static void setmCollections(List<RSSItem> mCollect) {
        mCollections = new ArrayList<RSSItem>();
        mCollections.addAll(mCollect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mConfiguration = configuration();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        ListView mNewsTypeList = (ListView)findViewById(R.id.configure_list);
        NewsTypeAdapter newsTypeAdapter = new NewsTypeAdapter(mConfiguration);
        mNewsTypeList.setAdapter(newsTypeAdapter);

        mNewsTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                type = mConfiguration.get(position).getText();
                data = mConfiguration.get(position).getData();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_TYPE,type);
                editor.putString(KEY_DATA,data);
                editor.commit();

                startWidget(type, data);

            }
        });
    }

    public List<NavItem> configuration() {

        List<NavItem> i = new ArrayList<NavItem>();

        //DONT MODIFY ABOVE THIS LINE

        i.add(new NavItem("Latest News", R.drawable.newspaper, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/latest"));
        i.add(new NavItem("Main", R.drawable.document, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/news"));
        i.add(new NavItem("Business", R.drawable.business_tie, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/business"));
        i.add(new NavItem("Sports", R.drawable.tennis, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/sport"));
        i.add(new NavItem("Lifestyle", R.drawable.business_tie, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/lifestyle"));
        i.add(new NavItem("All Woman", R.drawable.woman_figure, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/allwoman"));
        i.add(new NavItem("Entertainment", R.drawable.drama, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/entertainment"));
        i.add(new NavItem("Editorial", R.drawable.quote, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/editorial"));
        i.add(new NavItem("Columns", R.drawable.write, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/columns"));
        i.add(new NavItem("Career", R.drawable.document, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/career"));
        i.add(new NavItem("Food", R.drawable.food, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/food"));
        i.add(new NavItem("Teenage", R.drawable.newspaper, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/teenage"));
        i.add(new NavItem("Auto", R.drawable.auto, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/auto"));
        i.add(new NavItem("Environment", R.drawable.earth_usa, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/environment"));
        i.add(new NavItem("Clovis Toons", R.drawable.smiley, NavItem.ITEM, CartoonsFragment.class, "http://www.jamaicaobserver.com/app/cartoons")); // CartoonsFragment //http:/m.jamaicaobserver.com/tools/cartoons

        return i;

    }

    class NewsTypeAdapter extends BaseAdapter{
        private List<NavItem> mConfig = new ArrayList<NavItem>();
        NewsTypeAdapter(List<NavItem> mConfig){
            this.mConfig = mConfig;
        }

        @Override
        public int getCount() {
            return mConfig.size();
        }

        @Override
        public NavItem getItem(int position) {
            return mConfig.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NavItem item = getItem(position);
            TextView textView = new TextView(JamaicaAppWidgetConfigure.this);
            textView.setText(item.getText());
            textView.setTextSize(20);
            textView.setPadding((int)getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int)getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int)getResources().getDimension(R.dimen.activity_horizontal_margin),
                    (int)getResources().getDimension(R.dimen.activity_horizontal_margin));
            return textView;
        }
    }

    /**
     * This method right now displays the widget and starts a Service to fetch
     * remote data from Server
     */
    private void startWidget(String type, String data) {

        // this intent is essential to show the widget
        // if this intent is not included,you can't show
        // widget on homescreen
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        intent.putExtra(KEY_TYPE,type);
        intent.putExtra(KEY_DATA,data);
        setResult(Activity.RESULT_OK, intent);

        // start your service
        // to fetch data from web
        Intent serviceIntent = new Intent(this, RemoteFetchService.class);
        serviceIntent
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        serviceIntent.putExtra(KEY_TYPE,type);
        serviceIntent.putExtra(KEY_DATA,data);
        startService(serviceIntent);

        // finish this activity
        this.finish();

    }

}
