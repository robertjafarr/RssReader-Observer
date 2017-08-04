package com.sieae.jamaicaobserver.rss;

import org.parceler.Parcel;

import java.util.List;

/**
 *  This class is used to save information about our rss feed
 */

import java.util.Vector;

@Parcel
public class RSSFeed {
	private String title = null;
	private String description = null;
	private String link = null;
	private String pubdate = null;
	private List<RSSItem> itemList;
	private String thumburl = null;
	
	RSSFeed(){
		itemList = new Vector<RSSItem>(0);
	}

	public RSSFeed(RSSItem item){
		itemList = new Vector<RSSItem>(0);
		addItem(item);
	}
	
	void addItem(RSSItem item){
		itemList.add(item);
	}
	
	public RSSItem getItem(int location){
		return itemList.get(location);
	}
	
	public List<RSSItem> getList(){
		return itemList;
	}
	public void setList(List<RSSItem> items){
		itemList.clear();// = new Vector<RSSItem>(0);
		itemList.addAll(items);
	}
	void setTitle(String value)
	{
		title = value;
	}
	void setDescription(String value)
	{
		description = value;
	}
	void setLink(String value)
	{
		link = value;
	}
	void setPubdate(String value)
	{
		pubdate = value;
	}
	void setThumburl(String value)
	{
		thumburl = value;
	}
	
	public String getTitle()
	{
		return title;
	}
	public String getDescription()
	{
		return description;
	}
	public String getLink()
	{
		return link;
	}
	String getPubdate()
	{
		return pubdate;
		//This is not formatted, as formatting might be different for every feed
	}

	public String getThumburl() {return thumburl; }


}
