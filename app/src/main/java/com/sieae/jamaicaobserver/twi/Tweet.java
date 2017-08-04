/*
 * @author: Diogo Alves <diogo.alves.ti@gmail.com>
 */

package com.sieae.jamaicaobserver.twi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 *  This class is used to save & get tweets for the listview, and apply a date format
 */

public class Tweet {
	
	private String name;
	private String username;
	private String urlProfileImage;
	private String message;
	private String tweetDate;
	private String tweetId;
	
	public Tweet() {
	}
	
	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}
	
	public String getusername() {
		return username;
	}
	
	public void setusername(String username) {
		this.username = username;
	}
	
	public String geturlProfileImage() {
		return urlProfileImage;
	}

	public void seturlProfileImage(String url) {
		this.urlProfileImage = url;
	}

	public String getmessage() {
		return message;
	}
	
	public void setmessage(String message) {
		this.message = message;
	}
	
	public String getData() {
		return tweetDate;
	}
	
	public void setTweetId(String tweetid) {
		this.tweetId = tweetid;
	}
	
	public String getTweetId() {
		return tweetId;
	}
	
	public void setData(String data) {
		String dataTimeZone = removeTimeZone(data);
		this.tweetDate = fomatData(dataTimeZone);
	}
	
	@SuppressLint("SimpleDateFormat")
	private String fomatData(String data){
		String strData = null;
		TimeZone tzUTC = TimeZone.getTimeZone("UTC");
		SimpleDateFormat formatEntry = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		formatEntry.setTimeZone(tzUTC);
		SimpleDateFormat formatFinal = new SimpleDateFormat("EEE, dd/MM/yy, 'at' HH:mm");
		
		try {
			strData = formatFinal.format(formatEntry.parse(data));
		} catch (ParseException e) {
		Log.e("Error parsing data", Log.getStackTraceString(e));
		}
		return strData;
	}
	
	private String removeTimeZone(String data){
		// remove strange entries from date
		return data.replaceFirst("(\\s[+|-]\\d{4})", "");
	}

}
