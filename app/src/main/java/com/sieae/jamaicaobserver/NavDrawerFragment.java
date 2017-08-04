package com.sieae.jamaicaobserver;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sieae.jamaicaobserver.cartoons.CartoonsFragment;
import com.sieae.jamaicaobserver.fav.ui.FavFragment;
import com.sieae.jamaicaobserver.media.ui.MediaFragment;
import com.sieae.jamaicaobserver.rss.ui.RssFragment;
import com.sieae.jamaicaobserver.util.AsyncWebRequest;
import com.zenit.lib.Constants;
import com.zenit.lib.ViewBinder;
import com.zenit.lib.ZenitRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NavDrawerFragment extends Fragment implements NavDrawerCallback,
        AsyncWebRequest.OnAsyncRequestComplete{
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private static final String ALL_DETAIL = "navitem";
    private static final String ITEM_NAME = "itemname";
    private static final String ITEM_TYPE = "itemtype";
    private static final String ICON_URL = "itemiconurl";
    private static final String ITEM_URL = "itemurl";
    private static final String SECTION_ITEMS = "sectionitems";

    private NavDrawerCallback mCallbacks;
    private RecyclerView mDrawerList;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private int mCurrentSelectedPosition;
    
    private List<NavItem> mConfiguration;// = new ArrayList<NavItem>();

    NavDrawerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view;
        view = inflater.inflate(R.layout.drawer_fragment, container, false);

        mDrawerList = (RecyclerView) view.findViewById(R.id.drawerList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);

        final List<NavItem> NavItems = getConfiguration();
        adapter = new NavDrawerAdapter(NavItems, NavDrawerFragment.this);
        adapter.setNavigationDrawerCallbacks(this);

        //*****
//        ZenitRecyclerAdapter zenitAdapter = new ZenitRecyclerAdapter(adapter, NavItems,
//                getActivity(), mDrawerList);
//
//        ViewBinder binder = new ViewBinder.Builder(R.layout.list_tiletext_layout)
//                .titleId(R.id.title)
//                .subtitleId(R.id.subtitle)
//                .iconId(R.id.icon)
//                .build();
//        zenitAdapter.setViewBinder(binder);
//        mDrawerList.setAdapter(zenitAdapter);
//        Constants.DEVELOP_MODE = true;
//        zenitAdapter.loadAds("854fdbde3d");
        //*****

        mDrawerList.setAdapter(adapter);
        if(NavItems.size()>0)
        selectItem(mCurrentSelectedPosition, NavItems.get(mCurrentSelectedPosition));
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavDrawerCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavDrawerCallback.");
        }
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public void setActionBarDrawerToggle(ActionBarDrawerToggle actionBarDrawerToggle) {
        mActionBarDrawerToggle = actionBarDrawerToggle;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu();
            }
        };

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        
        //TODO This works (hides the drawer if there is only 1 item), but makes settings and favorites unreachable
        boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);
        
        if (newDrawer == false && getConfiguration().size() == 1){
        	mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        
	    if (newDrawer == true && getConfiguration().size() == 2){
	    	mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        	mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	    }
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    void selectItem(int position, NavItem item) {
    	//If on start, item is section, change it.
    	if (item.getType() == NavItem.SECTION || item.getType() ==  NavItem.TOP){
    		position = position + 1;
    		item = getConfiguration().get(position);
    		selectItem(position, item);
    		return;
    	}
    	
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position, item);
        }
        ((NavDrawerAdapter) mDrawerList.getAdapter()).selectPosition(position);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, NavItem item) {
    	//TODO we can also call this method here, but that won't set an initial item. 
        //mCallbacks.onNavigationDrawerItemSelected(position, item);
    	if (item.getType() != NavItem.SECTION){
    		selectItem(position, item);
    	}
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

  /*  private List<NavItem> getConfiguration(){
        if (null == mConfiguration){
//    		mConfiguration = Config.configuration();
//            callAPI(getActivity(), GlobalVariables.NAV_MENU_URL);



//            mConfiguration.addAll(MainActivity.mConfiguration);//getmConfiguration();
//            boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);
//
//            if (newDrawer == true){
//                mConfiguration.add(0, new NavItem("Header", NavItem.TOP));
//            }
        }

        return getNavItems(GlobalVariables.STRING_JSON);//mConfiguration;
    }*/

    private List<NavItem> getConfiguration(){
    	if (null == mConfiguration){
    		mConfiguration = Config.configuration();

    		boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);
    	        
    	    if (newDrawer == true){
    	    	mConfiguration.add(0, new NavItem("Header", NavItem.TOP));
    	    }
    	}
    	
    	return mConfiguration;
    }

    private void callAPI(Context context, String apiURL){
        // download all marsters
        AsyncWebRequest getPosts = new AsyncWebRequest(context,
                "GET");
        getPosts.execute(apiURL);
    }

    @Override
    public void asyncResponse(String response) {
        // TODO Auto-generated method stub
        // create a JSON array from the response string
        if (response == null) {
            Toast.makeText(getActivity(), "Network Error",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Log.v("", "output string =" + response.toString());

        String jsonStr = response.toString();

//        getNavItems(jsonStr);


    }

    private List<NavItem> getNavItems(String jsonStr){
        List<NavItem> mConfiguration = new ArrayList<NavItem>();
        if (jsonStr != null) {

            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObject.getJSONArray(ALL_DETAIL);

                if(mConfiguration!=null)mConfiguration.clear();
                for (int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String itemname = jsonObj.getString(ITEM_NAME);
                    String itemtype = jsonObj.getString(ITEM_TYPE);
                    String itemiconurl = jsonObj.getString(ICON_URL);
                    String itemurl = jsonObj.getString(ITEM_URL);

                    int p = 0;
                    if(itemtype.contentEquals("section")){
                        p = NavItem.SECTION;
                    }else if(itemtype.contentEquals("extra")){
                        p = NavItem.EXTRA;
                    }
                    JSONArray jsonArray1 = jsonObj.getJSONArray(SECTION_ITEMS);
                    if(jsonArray1!=null){
                        if(jsonArray1.length()>0){
                            mConfiguration.add(new NavItem(itemname, NavItem.SECTION));
                            for (int j=0; j<jsonArray1.length(); j++) {
                                JSONObject jsonObj2 = jsonArray1.getJSONObject(j);
                                String itemname2 = jsonObj2.getString(ITEM_NAME);
                                String itemtype2 = jsonObj2.getString(ITEM_TYPE);
                                String itemiconurl2 = jsonObj2.getString(ICON_URL);
                                String itemurl2 = jsonObj2.getString(ITEM_URL);

                                if(itemname2.contentEquals("Clovis Toons")){
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            CartoonsFragment.class,
                                            itemurl2));
                                }else if(itemname2.contentEquals("AReality")){
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            null, null));
                                }else if(itemname2.contentEquals("Fyah 105 Live")){
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            MediaFragment.class,
                                            itemurl2));
                                }else{
                                    mConfiguration.add(new NavItem(itemname2,
                                            R.drawable.newspaper,
                                            NavItem.ITEM,
                                            RssFragment.class,
                                            itemurl2));
                                }

                            }
                        }else{
                            if(itemtype.contentEquals("extra") && itemname.contentEquals("Favorites")){
                                mConfiguration.add(new NavItem(itemname,
                                        R.drawable.newspaper,
                                        NavItem.EXTRA,
                                        FavFragment.class,
                                        null));
                            }else if(itemtype.contentEquals("extra") && itemname.contentEquals("Settings")){
                                mConfiguration.add(new NavItem(itemname,
                                        R.drawable.newspaper,
                                        NavItem.EXTRA,
                                        SettingsFragment.class,
                                        null));
                            }else{
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

        if (newDrawer == true){
            mConfiguration.add(0, new NavItem("Header", NavItem.TOP));
        }

        return mConfiguration;
    }
//    private List<NavItem> getNavItems(String jsonStr){
//        if (jsonStr != null) {
//            try {
//                JSONObject jsonObject = new JSONObject(jsonStr);
//                JSONArray jsonArray = jsonObject.getJSONArray(ALL_DETAIL);
//
//                if(mConfiguration!=null)mConfiguration.clear();
//                for (int i=0; i<jsonArray.length(); i++){
//                    JSONObject jsonObj = jsonArray.getJSONObject(i);
//                    String itemname = jsonObj.getString(ITEM_NAME);
//                    String itemtype = jsonObj.getString(ITEM_TYPE);
//                    String itemiconurl = jsonObj.getString(ICON_URL);
//                    String itemurl = jsonObj.getString(ITEM_URL);
//
//                    int p = 0;
//                    if(itemtype.contentEquals("section")){
//                        p = NavItem.SECTION;
//                    }else if(itemtype.contentEquals("extra")){
//                        p = NavItem.EXTRA;
//                    }
//                    JSONArray jsonArray1 = jsonObj.getJSONArray(SECTION_ITEMS);
//                    if(jsonArray1!=null){
//                        if(jsonArray1.length()>0){
//                            mConfiguration.add(new NavItem(itemname, NavItem.SECTION));
//                            for (int j=0; j<jsonArray1.length(); j++) {
//                                JSONObject jsonObj2 = jsonArray1.getJSONObject(j);
//                                String itemname2 = jsonObj2.getString(ITEM_NAME);
//                                String itemtype2 = jsonObj2.getString(ITEM_TYPE);
//                                String itemiconurl2 = jsonObj2.getString(ICON_URL);
//                                String itemurl2 = jsonObj2.getString(ITEM_URL);
//
//                                if(itemname2.contentEquals("Clovis Toons")){
//                                    mConfiguration.add(new NavItem(itemname2,
//                                            R.drawable.newspaper,
//                                            NavItem.ITEM,
//                                            CartoonsFragment.class,
//                                            itemurl2));
//                                }else if(itemname2.contentEquals("AReality")){
//                                    mConfiguration.add(new NavItem(itemname2,
//                                            R.drawable.newspaper,
//                                            NavItem.ITEM,
//                                            null, null));
//                                }else if(itemname2.contentEquals("Fyah 105 Live")){
//                                    mConfiguration.add(new NavItem(itemname2,
//                                            R.drawable.newspaper,
//                                            NavItem.ITEM,
//                                            MediaFragment.class,
//                                            itemurl2));
//                                }else{
//                                    mConfiguration.add(new NavItem(itemname2,
//                                            R.drawable.newspaper,
//                                            NavItem.ITEM,
//                                            RssFragment.class,
//                                            itemurl2));
//                                }
//
//                            }
//                        }else{
//                            if(itemtype.contentEquals("extra") && itemname.contentEquals("Favorites")){
//                                mConfiguration.add(new NavItem(itemname,
//                                        R.drawable.newspaper,
//                                        NavItem.EXTRA,
//                                        FavFragment.class,
//                                        null));
//                            }else if(itemtype.contentEquals("extra") && itemname.contentEquals("Settings")){
//                                mConfiguration.add(new NavItem(itemname,
//                                        R.drawable.newspaper,
//                                        NavItem.EXTRA,
//                                        SettingsFragment.class,
//                                        null));
//                            }else{
//                                mConfiguration.add(new NavItem(itemname,
//                                        R.drawable.newspaper,
//                                        NavItem.SECTION,
//                                        RssFragment.class,
//                                        itemurl));
//                            }
//
//                        }
//                    }
////                    NavItem navItem = new NavItem();
//
////                    mConfiguration.add(navItem);
//
//                }
//
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//
//        }
//
////        boolean newDrawer = getResources().getBoolean(R.bool.newdrawer);
////
////        if (newDrawer == true){
////            mConfiguration.add(0, new NavItem("Header", NavItem.TOP));
////        }
//
//        adapter.notifyDataSetChanged();
//
////        final List<NavItem> NavItems = getConfiguration();
////        adapter = new NavDrawerAdapter(mConfiguration, NavDrawerFragment.this);
////        adapter.setNavigationDrawerCallbacks(this);
////
////        mDrawerList.setAdapter(adapter);
////        selectItem(mCurrentSelectedPosition, mConfiguration.get(mCurrentSelectedPosition));
//
//        return mConfiguration;
//    }
}
