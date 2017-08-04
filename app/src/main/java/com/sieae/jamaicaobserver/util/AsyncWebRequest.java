package com.sieae.jamaicaobserver.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

@SuppressWarnings("deprecation")
public class AsyncWebRequest extends AsyncTask<String, Integer, String> {
	 
	 OnAsyncRequestComplete caller;
	 Context context;
	 String method = "GET";
	 List<NameValuePair> parameters = null;
	 ProgressDialog pDialog = null;
	 JSONObject mJsonData;

	// Three Constructors
	public AsyncWebRequest(Context activity, OnAsyncRequestComplete a, String m, JSONObject jSonData) {
		caller = (OnAsyncRequestComplete) a;
		context = activity;
		method = m;

		mJsonData = jSonData;
	}

	 // Three Constructors
	 public AsyncWebRequest(Context a, String m, JSONObject jSonData) {
	  caller = (OnAsyncRequestComplete) a;
	  context = a;
	  method = m;
	 
	  mJsonData = jSonData;
	 } 
	 // Three Constructors
	 public AsyncWebRequest(Context a, String m, List<NameValuePair> p) {
	  caller = (OnAsyncRequestComplete) a;
	  context = a;
	  method = m;
	  parameters = p;
	 }
	 
	 public AsyncWebRequest(Context activity, OnAsyncRequestComplete a, String m) {
	  caller = (OnAsyncRequestComplete) a;
	  context = activity;
	  method = m;
	 }

	public AsyncWebRequest(Context a, String m) {
		caller = (OnAsyncRequestComplete) a;
		context = a;
		method = m;
	}
	 
	 public AsyncWebRequest(Context a) {
	  caller = (OnAsyncRequestComplete) a;
	  context = a;
	 }
	 
	 // Interface to be implemented by calling activity
	 public interface OnAsyncRequestComplete {
	  public void asyncResponse(String response);
	 }
	 
	 public String doInBackground(String... urls) {
	  // get url pointing to entry point of API
	  String address = urls[0].toString();
	  if (method == "POST") {
	   return post(address);
	  }
	 
	  if (method == "GET") {
	   return get(address);
	  }
	 
	  return null;
	 }
	 public void WantToCloaseBufferingAfterCompletion(){
		 
		 
	 }
	 public void onPreExecute() {
	  pDialog = new ProgressDialog(context);
	  pDialog.setMessage("Loading data.."); // typically you will define such
	            // strings in a remote file.
	  pDialog.show();
	 }
	 
	 public void onProgressUpdate(Integer... progress) {
	  // you can implement some progressBar and update it in this record
	  // setProgressPercent(progress[0]);
	 }
	 
	 public void onPostExecute(String response) {
	  if (pDialog != null && pDialog.isShowing()) {
	   pDialog.dismiss();
	  }
	  caller.asyncResponse(response);
	 }
	 
	 protected void onCancelled(String response) {
	  if (pDialog != null && pDialog.isShowing()) {
	   pDialog.dismiss();
	  }
	  caller.asyncResponse(response);
	 }
	 
	 @SuppressWarnings("deprecation")
	 private String get(String address) {
	  try {
	 
	   if (parameters != null) {
	 
	    String query = "";
	    String EQ = "="; String AMP = "&";
	    for (NameValuePair param : parameters) {
	     query += param.getName() + EQ + URLEncoder.encode(param.getValue()) + AMP;
	    }
	 
	    if (query != "") {
	     address += "?" + query;
	    }
	   }
	 
	   HttpClient client = new DefaultHttpClient();
	   HttpGet get= new HttpGet(address);
	   Log.v("","print get request"+address.toString());
	   HttpResponse response = client.execute(get);
	   return stringifyResponse(response);
	 
	  } catch (ClientProtocolException e) {
	   // TODO Auto-generated catch block
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	  }
	 
	  return null;
	 }
	 
	 private String post(String address) {
	  try {

	   HttpClient client = new DefaultHttpClient();
	   HttpPost post = new HttpPost(address);
	 //create json object 
//	   JSONObject data = new JSONObject();
//	   try {
//
//		/*	data.put((String)"name", (String)"jagdish ji");
//			data.put((String)"companyname", (String)"self");
//			data.put((String)"email", (String)"abc@gmail.com");
//			data.put((String)"mobile", (String)"9414876415");*/
//	/*	data.put((String)"user_id", (int)10);
//		data.put((String)"model_id", (int)6);
//		data.put((String)"mfg_year", (String)"2011");
//		data.put((String)"company_id", (int)3);*/
//		//   data.put((String)"is_mobile_verify", (int)1);
//		//   data.put((String)"user_id", (int)10);
//	/*	   data.put((String)"user_id", (int)10);
//		   data.put((String)"cab_id", (int)7);
//		   data.put((String)"available_from", (String)"2016-01-08 03:10:00");
//		   data.put((String)"available_to", (String)"2016-01-08 03:10:00");
//		   data.put((String)"start_city", (String)"delhi");
//		   data.put((String)"inclusive_id", (String)"1,3");
//
//		   JSONObject stopover1 = new JSONObject();
//		   stopover1.put((String)"next_city", (String)"jaipur");
//		   stopover1.put((String)"price", (String)"300");
//		   JSONObject stopover2 = new JSONObject();
//		   stopover2.put((String)"next_city", (String)"ajmer");
//		   stopover2.put((String)"price", (String)"600");
//		   JSONObject stopover3 = new JSONObject();
//		   stopover3.put((String)"next_city", (String)"ahmedabad");
//		   stopover3.put((String)"price", (String)"900");
//		   JSONArray stopover = new JSONArray();
//
//		   stopover.put(stopover1);
//		   stopover.put(stopover2);
//		   stopover.put(stopover3);
//
//		   data.put("stopover", stopover);*/
////		   data.put((String)"source", (String)"delhi");
////		   data.put((String)"destination", (String)"ahmedabad");
////		   data.put((String)"date_on", (String)"2016-01-08");
//
//	} catch (JSONException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	 //  if (parameters != null) {
	 //   post.setEntity(new UrlEncodedFormEntity(parameters));
	 //  }


	   StringEntity se = new StringEntity(mJsonData.toString());
	   post.setEntity(se);
	   post.setHeader("Accept", "application/json");
	   post.setHeader("Content-type", "application/json");
	 Log.v("","print post request"+post.toString());
	   HttpResponse response = client.execute(post);
	   return stringifyResponse(response);
	 
	  } catch (ClientProtocolException e) {
	   // TODO Auto-generated catch block
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	  }
	 
	  return null;
	 }
	 
	 private String stringifyResponse(HttpResponse response) {
	  BufferedReader in;
	  try {
	   in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	 
	   StringBuffer sb = new StringBuffer("");
	   String line = "";
	   while ((line = in.readLine()) != null) {
	    sb.append(line);
	   }
	   in.close();
	 
	   return sb.toString();
	  } catch (IllegalStateException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	 
	  return null;
	 }
	}