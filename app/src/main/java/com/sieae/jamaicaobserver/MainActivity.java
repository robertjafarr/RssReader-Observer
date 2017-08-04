package com.sieae.jamaicaobserver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sieae.jamaicaobserver.Vuforia.ui.ActivityList.UserDefineTargetAboutScreen;
import com.sieae.jamaicaobserver.cartoons.CartoonsFragment;
import com.sieae.jamaicaobserver.fav.ui.FavFragment;
import com.sieae.jamaicaobserver.media.ui.MediaFragment;
import com.sieae.jamaicaobserver.rss.ServiceStarter;
import com.sieae.jamaicaobserver.rss.ui.RssFragment;
import com.sieae.jamaicaobserver.web.WebviewFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.adtech.mobilesdk.publisher.configuration.AdtechAdConfiguration;
//import com.adtech.mobilesdk.publisher.view.AdtechBannerView;
//import com.sieae.jamaicaobserver.metaio.MetaioActivity;
import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT;
import static com.sieae.jamaicaobserver.GlobalVariables.ABOUT_TEXT_TITLE;
import static com.sieae.jamaicaobserver.GlobalVariables.ACTIVITY_TO_LAUNCH;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.SyncListener;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;

public class MainActivity extends ActionBarActivity implements NavDrawerCallback {

    private Toolbar mToolbar;
    private NavDrawerFragment mNavigationDrawerFragment;


    public static String DATA = "transaction_data";

    SharedPreferences prefs;
    String mWebUrl = null;
    boolean openedByBackPress = false;

//    AdtechBannerView banner;

    public static List<NavItem> mConfiguration = new ArrayList<NavItem>();
    private static final String ALL_DETAIL = "navitem";
    private static final String ITEM_NAME = "itemname";
    private static final String ITEM_TYPE = "itemtype";
    private static final String ICON_URL = "itemiconurl";
    private static final String ITEM_URL = "itemurl";
    private static final String SECTION_ITEMS = "sectionitems";

    public static List<NavItem> getmConfiguration() {
        return mConfiguration;
    }

    public static void setmConfiguration(List<NavItem> mConfig) {
        mConfiguration = mConfig;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavItems(GlobalVariables.STRING_JSON);
        boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);

        if (newDrawer == true) {
            setContentView(R.layout.activity_main_alternate);
        } else {
            setContentView(R.layout.activity_main);
            Helper.setStatusBarColor(MainActivity.this, getResources().getColor(R.color.myPrimaryDarkColor));
        }

//        banner = (AdtechBannerView) findViewById(R.id.ad_container);
//        final AdtechAdConfiguration config = new AdtechAdConfiguration("AppName");
//        config.setDomain("a.adtechus.com");
//        config.setAlias("jaobslatestnewshomepagembt_android");
//        config.setNetworkId(5469);
//        config.setSubnetworkId(1);
//        config.enableImageBannerResize(false);
//        banner.setAdConfiguration(config);

        // AdMob ad
        AdView adView = (AdView) this.findViewById(R.id.mainAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mNavigationDrawerFragment = (NavDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        if (newDrawer == true) {
            mNavigationDrawerFragment.setup(R.id.scrimInsetsFrameLayout, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
            mNavigationDrawerFragment.getDrawerLayout().setStatusBarBackgroundColor(
                    getResources().getColor(R.color.myPrimaryDarkColor));
        } else {
            mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        }

        prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        //setting push enabled
        String push = getString(R.string.rss_push_url);
        if (null != push && !push.equals("")) {
            // Create object of SharedPreferences.
            boolean firstStart = prefs.getBoolean("firstStart", true);

            if (firstStart) {

                final ServiceStarter alarm = new ServiceStarter();

                SharedPreferences.Editor editor = prefs.edit();

                alarm.setAlarm(this);
                //now, just to be sure, where going to set a value to check if notifications is really enabled
                editor.putBoolean("firstStart", false);
                //commits your edits
                editor.commit();
            }

        }

        //Checking if the user would prefer to show the menu on start
        boolean checkBox = prefs.getBoolean("menuOpenOnStart", false);
        if (checkBox == true && null == mWebUrl) {
            mNavigationDrawerFragment.openDrawer();
        }

        // New imageloader
        Helper.initializeImageLoader(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            CleverTapAPI.getInstance(this).event.pushNotificationEvent(intent.getExtras());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.rss_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, NavItem item) {
        // For metaio
//        if (item.getText().equals("Home")) {
//
//            return;
//        }
//        else
        if (item.getText().equals("Metaio")) {
//            startActivity(new Intent(MainActivity.this, MetaioActivity.class));
            return;
        }
        else if (item.getText().equals("AReality")) {
            /*User Define Target*/
            Intent intent = new Intent(MainActivity.this,
                    UserDefineTargetAboutScreen.class);
            intent.putExtra(ACTIVITY_TO_LAUNCH,
                    "Vuforia.app.VideoPlayback.VideoPlayback");
            intent.putExtra(ABOUT_TEXT_TITLE, "AReality");
            intent.putExtra(ABOUT_TEXT,
                    "UserDefinedTargets/UD_about.html");
            startActivity(intent);

            /*AR VR*/
//            Intent intent = new Intent(MainActivity.this,
//                    AboutScreen.class);
//            intent.putExtra(ACTIVITY_TO_LAUNCH,
//                    "Vuforia.app.ARVR.ARVR");
//            intent.putExtra(ABOUT_TEXT_TITLE, "ARVR");
//            intent.putExtra(ABOUT_TEXT, "ARVR/ARVR_about.html");
//            startActivity(intent);

            /*Image Target*/
//            Intent intent = new Intent(this, ImageTargetsAboutScreen.class);
//            intent.putExtra("ABOUT_TEXT_TITLE", "Image Targets");
//            intent.putExtra("ACTIVITY_TO_LAUNCH",
//                    "Vuforia.app.ImageTargets.ImageTargets");
//            intent.putExtra("ABOUT_TEXT", "ImageTargets/IT_about.html");
//            startActivity(intent);

//            if (Utils.hasInternetCameraAndStoragePermission(this)) {
//
//
//
//            } else {
//                setPermission();
//
//            }

            return;
        }
        Fragment fragment;
        try {
            fragment = item.getFragment().newInstance();
            if (fragment != null && null == mWebUrl) {
                //adding the data
                Bundle bundle = new Bundle();
                String extra = item.getData();
                bundle.putString(DATA, extra);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();

                setTitle(item.getText());

                if (null != MainActivity.this.getSupportActionBar() && null != MainActivity.this.getSupportActionBar().getCustomView()) {
                    MainActivity.this.getSupportActionBar().setDisplayOptions(
                            ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
                }

            } else {
                // error in creating fragment
                Log.e("MetaioActivity", "Error in creating fragment");
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment webview = getSupportFragmentManager().findFragmentById(R.id.container);

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else if (webview instanceof WebviewFragment) {
            boolean goback = ((WebviewFragment) webview).canGoBack();
            if (!goback)
                super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        banner.load();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        banner.stop();
//    }

    private void getNavItems(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObject.getJSONArray(ALL_DETAIL);

                if (mConfiguration != null) mConfiguration.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String itemname = jsonObj.getString(ITEM_NAME);
                    String itemtype = jsonObj.getString(ITEM_TYPE);
                    String itemiconurl = jsonObj.getString(ICON_URL);
                    String itemurl = jsonObj.getString(ITEM_URL);

                    int p = 0;
                    if (itemtype.contentEquals("section")) {
                        p = NavItem.SECTION;
                    } else if (itemtype.contentEquals("extra")) {
                        p = NavItem.EXTRA;
                    }
                    JSONArray jsonArray1 = jsonObj.getJSONArray(SECTION_ITEMS);
                    if (jsonArray1 != null) {
                        if (jsonArray1.length() > 0) {
                            mConfiguration.add(new NavItem(itemname, NavItem.SECTION));
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObj2 = jsonArray1.getJSONObject(j);
                                String itemname2 = jsonObj2.getString(ITEM_NAME);
                                String itemtype2 = jsonObj2.getString(ITEM_TYPE);
                                String itemiconurl2 = jsonObj2.getString(ICON_URL);
                                String itemurl2 = jsonObj2.getString(ITEM_URL);

                                if (itemname2.contentEquals("Clovis Toons")) {
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            CartoonsFragment.class,
                                            itemurl2));
                                } else if (itemname2.contentEquals("AReality")) {
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            null, null));
                                } else if (itemname2.contentEquals("Fyah 105 Live")) {
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            MediaFragment.class,
                                            itemurl2));
                                } else {
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            RssFragment.class,
                                            itemurl2));
                                }

                            }
                        } else {
                            if (itemtype.contentEquals("extra") && itemname.contentEquals("Favorites")) {
                                mConfiguration.add(new NavItem(itemname,
                                        R.drawable.newspaper,
                                        NavItem.EXTRA,
                                        FavFragment.class,
                                        null));
                            } else if (itemtype.contentEquals("extra") && itemname.contentEquals("Settings")) {
                                mConfiguration.add(new NavItem(itemname,
                                        R.drawable.newspaper,
                                        NavItem.EXTRA,
                                        SettingsFragment.class,
                                        null));
                            } else {
                                mConfiguration.add(new NavItem(itemname,
                                        R.drawable.newspaper,
                                        NavItem.SECTION,
                                        RssFragment.class,
                                        itemurl));
                            }

                        }
                    }
//                    NavItem navItem = new NavItem();

//                    mConfiguration.add(navItem);

                }
//                NavItem navItem;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);
//
//        if (newDrawer == true){
//            mConfiguration.add(0, new NavItem("Header", NavItem.TOP));
//        }

    }
}