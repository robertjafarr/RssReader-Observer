package com.sieae.jamaicaobserver;

import java.util.ArrayList;
import java.util.List;

import com.sieae.jamaicaobserver.cartoons.CartoonsFragment;
import com.sieae.jamaicaobserver.fav.ui.FavFragment;
import com.sieae.jamaicaobserver.media.ui.MediaFragment;
import com.sieae.jamaicaobserver.rss.ui.AllRssFragment;
import com.sieae.jamaicaobserver.rss.ui.RssFragment;
import com.sieae.jamaicaobserver.web.WebviewFragment;
import com.sieae.jamaicaobserver.wordpress.ui.WordpressFragment;

public class Config {
	
	public static List<NavItem> configuration() {
		
		List<NavItem> i = new ArrayList<NavItem>();
        
		//DONT MODIFY ABOVE THIS LINE
		
		i.add(new NavItem("News", NavItem.SECTION));
       // i.add(new NavItem("Uploaded Videos", R.drawable.ic_details, NavItem.ITEM, VideosFragment.class, "UU7V6hW6xqPAiUfataAZZtWA,UC7V6hW6xqPAiUfataAZZtWA"));
       // i.add(new NavItem("Liked Videos", R.drawable.ic_details, NavItem.ITEM, VideosFragment.class, "LL7V6hW6xqPAiUfataAZZtWA"));

        i.add(new NavItem("Home", R.drawable.newspaper, NavItem.ITEM, AllRssFragment.class,
                "http://www.jamaicaobserver.com/app/latest," +
                "http://www.jamaicaobserver.com/app/news," +
                        "http://www.jamaicaobserver.com/app/business," +
                        "http://www.jamaicaobserver.com/app/sport," +
                        "http://www.jamaicaobserver.com/app/lifestyle," +
                        "http://www.jamaicaobserver.com/app/allwoman," +
                        "http://www.jamaicaobserver.com/app/entertainment," +
                        "http://www.jamaicaobserver.com/app/editorial," +

                        "http://www.jamaicaobserver.com/app/columns," +
                        "http://www.jamaicaobserver.com/app/career," +
                        "http://www.jamaicaobserver.com/app/food," +
                        "http://www.jamaicaobserver.com/app/teenage," +
                        "http://www.jamaicaobserver.com/app/auto," +
                        "http://www.jamaicaobserver.com/app/environment"
//                        +"http://www.jamaicaobserver.com/app/cartoons"
        ));
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
          //  i.add(new NavItem("Letters", R.drawable.file, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/letters"));
            i.add(new NavItem("Auto", R.drawable.auto, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/auto"));
            i.add(new NavItem("Environment", R.drawable.earth_usa, NavItem.ITEM, RssFragment.class, "http://www.jamaicaobserver.com/app/environment"));
            i.add(new NavItem("Clovis Toons", R.drawable.smiley, NavItem.ITEM, CartoonsFragment.class, "http://www.jamaicaobserver.com/app/cartoons")); // CartoonsFragment //http:/m.jamaicaobserver.com/tools/cartoons
            i.add(new NavItem("AReality", R.drawable.document, NavItem.ITEM, null,null));

       // i.add(new NavItem("Tip Us", R.drawable.ic_details, NavItem.ITEM, WebviewFragment.class, "http://www.androidpolice.com/contact/"));
        
       // i.add(new NavItem("Recent Posts", R.drawable.ic_details, NavItem.ITEM, WordpressFragment.class, "http://androidpolice.com/api/"));
       // i.add(new NavItem("Cat: Radio", R.drawable.ic_details, NavItem.ITEM, WordpressFragment.class, "http://moma.org/wp/inside_out/api/,conservation"));
        
       // i.add(new NavItem("Wallpaper Tumblr", R.drawable.ic_details, NavItem.ITEM, TumblrFragment.class, "androidbackgrounds"));

        i.add(new NavItem("Media", NavItem.SECTION));
        i.add(new NavItem("Fyah 105 Live ", R.drawable.radio, NavItem.ITEM, MediaFragment.class, "http://live.str3am.com:9210/live2.m3u"));
       // i.add(new NavItem("Official Twitter", R.drawable.ic_details, NavItem.ITEM, TweetsFragment.class, "Android"));
       // i.add(new NavItem("Maps", R.drawable.ic_details, NavItem.ITEM, MapsFragment.class, "drogisterij"));

       // i.add(new NavItem("Jamaica Observer Events", NavItem.SECTION));
       // i.add(new NavItem("Jamaica Food Awards ", R.drawable.drama, NavItem.ITEM, MediaFragment.class, "http://live.str3am.com:9210/live2.m3u"));

        //It's Suggested to not change the content below this line
        
        i.add(new NavItem("Device", NavItem.SECTION));
        i.add(new NavItem("Favorites", R.drawable.ic_action_favorite, NavItem.EXTRA, FavFragment.class, null));
        i.add(new NavItem("Settings", R.drawable.ic_action_settings, NavItem.EXTRA, SettingsFragment.class, null));
        
        //DONT MODIFY BELOW THIS LINE
        
        return i;
			
	}
	
}