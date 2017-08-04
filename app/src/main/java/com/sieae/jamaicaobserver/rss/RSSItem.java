package com.sieae.jamaicaobserver.rss;

import android.text.Html;

import org.parceler.Parcel;

/**
 *  This class is used to remember rss item data
 */
@Parcel
public class RSSItem {
	
	private String title = null;
	private String description = null;
	private String rowdescription = null;
	private String link = null;
	private String pubdate = null;
	private String thumburl = null;
	private boolean isRead = false;

	RSSItem(){
	}

	public RSSItem (String title, String description, String link, String pubdate) {
		setTitle(title);
		setDescription(description);
		setLink(link);
		setPubdate(pubdate);
	}
	
	void setTitle(String value)
	{
		title = value;
	}
	void setDescription(String value)
	{
		description = value;
	    rowdescription = stripHtml(value).toString();
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
	public String getRowDescription()
	{
		return rowdescription;
	}
	public String getLink()
	{
		return link;
	}
	public String getPubdate()
	{
		return pubdate;
	}
	public String getThumburl() {return thumburl; }

	public boolean isRead() {
		return isRead;
	}
	public void setIsRead(boolean isRead) {
		this.isRead = isRead;
	}


	public CharSequence stripHtml(String s) {
	    return Html.fromHtml(s).toString().replace('\n', (char) 32)
	        .replace((char) 160, (char) 32).replace((char) 65532, (char) 32).trim();
	}

}
