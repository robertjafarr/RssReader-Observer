/**
 * 
 */
package com.sieae.jamaicaobserver;

import android.os.Environment;

/**
 * @author Mohammad Arshi Khan
 *
 */
public class GlobalVariables {
	public static final String DATABASE_TABLE = "articlereadstatus";
	public static final String DB_PATH = "data/data/com.sieae.jamaicaobserver/databases/";
	
	private static String path = Environment.getExternalStorageDirectory().toString();

	public static final String COLUMN_CATEGORY_ID = "id";
	public static final String COLUMN_HEADING = "readrssheading";

	public static final String NAV_MENU_URL = "";

	public static final String STRING_JSON = "{" +
			"\"navitem\":[" +
			"{" +
			"\"itemname\":\"News\"," +
			"\"itemtype\":\"section\"," +
			"\"itemiconurl\":\"\"," +
			"\"itemurl\":\"\"," +
			"\"sectionitems\":[" +
			"{" +
			"\"itemname\":\"Latest News\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.newspaper\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/latest\"" +
			"}," +
			"{" +
			"\"itemname\":\"Main\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.document\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/news\"" +
			"}," +
			"{" +
			"\"itemname\":\"Business\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.business_tie\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/business\"" +
			"}," +
			"{" +
			"\"itemname\":\"Sports\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.tennis\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/sport\"" +
			"}," +
			"{" +
			"\"itemname\":\"Lifestyle\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.business_tie\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/lifestyle\"" +
			"}," +
			"{" +
			"\"itemname\":\"All Woman\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.woman_figure\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/allwoman\"" +
			"}," +
			"{" +
			"\"itemname\":\"Entertainment\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.drama\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/entertainment\"" +
			"}," +
			"{" +
			"\"itemname\":\"Editorial\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.quote\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/editorial\"" +
			"}," +
			"{" +
			"\"itemname\":\"Columns\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.write\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/columns\"" +
			"}," +
			"{" +
			"\"itemname\":\"Career\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.document\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/career\"" +
			"}," +
			"{" +
			"\"itemname\":\"Food\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.food\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/food\"" +
			"}," +
			"{" +
			"\"itemname\":\"Teenage\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.newspaper\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/teenage\"" +
			"}," +
			"{" +
			"\"itemname\":\"Auto\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.auto\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/auto\"" +
			"}," +
			"{" +
			"\"itemname\":\"Environment\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.earth_usa\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/environment\"" +
			"}," +
			"{" +
			"\"itemname\":\"Clovis Toons\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.smiley\"," +
			"\"itemurl\":\"http://www.jamaicaobserver.com/app/cartoons\"" +
			"}," +
			"{" +
			"\"itemname\":\"AReality\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.document\"," +
			"\"itemurl\":\"\"" +
			"}" +
			"]" +
			"}," +
			"{" +
			"\"itemname\":\"Media\"," +
			"\"itemtype\":\"section\"," +
			"\"itemiconurl\":\"\"," +
			"\"itemurl\":\"\"," +
			"\"sectionitems\":[" +
			"{" +
			"\"itemname\":\"Fyah 105 Live\"," +
			"\"itemtype\":\"item\"," +
			"\"itemiconurl\":\"R.drawable.radio\"," +
			"\"itemurl\":\"http://live.str3am.com:9210/live2.m3u\"" +
			"}" +
			"]" +
			"}," +
			"{" +
			"\"itemname\":\"Device\"," +
			"\"itemtype\":\"section\"," +
			"\"itemiconurl\":\"\"," +
			"\"itemurl\":\"\"," +
			"\"sectionitems\":[]" +
			"}," +
			"{" +
			"\"itemname\":\"Favorites\"," +
			"\"itemtype\":\"extra\"," +
			"\"itemiconurl\":\"R.drawable.ic_action_favorite\"," +
			"\"itemurl\":\"\"," +
			"\"sectionitems\":[]" +
			"}," +
			"{" +
			"\"itemname\":\"Settings\"," +
			"\"itemtype\":\"extra\"," +
			"\"itemiconurl\":\"R.drawable.ic_action_settings\"," +
			"\"itemurl\":\"\"," +
			"\"sectionitems\":[]" +
			"}" +
			"]" +
			"}";

	// Intent Keys
	public static final String ACTIVITY_TO_LAUNCH = "ACTIVITY_TO_LAUNCH";
	public static final String ABOUT_TEXT_TITLE = "ABOUT_TEXT_TITLE";
	public static final String ABOUT_TEXT = "ABOUT_TEXT";
	public static final String CLASS_TO_LAUNCH = "ClassToLaunch";
	public static final String CLASS_TO_LAUNCH_PKG = "ClassToLaunchPackage";
	public static final String KEY_PRODUCT = "product";

	public static final String VUFORIA_LICENSE_KEY = "AdUJ+kf/////AAAAGcpvVssvwE+PlNG8LS7H0q9O6GoSSfdoF5jE53hxjahAXcItZFsSObQBvDqV8DDAGOgpLJsR4EeCbkrY3dfaqP/fNQrJGTHq98fS9Mh9AWeZQZubvMN98CFIAQSOAc3fqsl137o7bWYQGkJC6kwZzjai/rEMKKGa6M0BNZXRf2cK3KHepEtYfYxALVtHHatB6TcLJFcnH+TUg+RMT6Q3L3aePWGYRl+liqAb7snJrJYd+RoHV9nOb3q/tpeSvjFJ9LMmmJIxnFzjgbaNcDy/8fMm/VQ3yCfPKdZvV+/8xhd4MAbpFajLYEnZQ081rcNVLOsJXLs14UCdpAILOBcaHzSYeHjjZLN5Gr1w9dsuDsWq";
}
