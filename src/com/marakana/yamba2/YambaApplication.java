package com.marakana.yamba2;

import android.app.Application;
import winterwell.jtwitter.Twitter;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import android.text.TextUtils;

public class YambaApplication extends Application {
	private static final String TAG = YambaApplication.class.getSimpleName();
	public Twitter twitter;
	private SharedPreferences prefs;
	private boolean serviceRunning = false;
	private StatusData statusData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				twitter = null;
			}
		});
		
		this.statusData = new StatusData(this);
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminated");
	}
	
	public synchronized Twitter getTwitter(){
		if(this.twitter == null){
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");
			String apiRoot = prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			
			if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(apiRoot)){
		        this.twitter = new Twitter(username, password);
		        this.twitter.setAPIRootUrl(apiRoot);
		    }
		}
		
		return this.twitter;
	}
	
	public boolean isServiceRunning(){
		return this.serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning){
		this.serviceRunning = serviceRunning;
	}
	
	public StatusData getStatusData(){
		return this.statusData;
	}
	
	public synchronized int fetchStatusUpdates(){
		Log.d(TAG, "Fetching status updates");
		Twitter twitter = this.getTwitter();
		
		if(twitter == null){
			Log.d(TAG, "Twitter connection info not initialized.");
			return 0;
		}
		else{
			try{
				List<Twitter.Status> statusUpdates = twitter.getFriendsTimeline();
				long latesStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
				
				Log.d(TAG, String.valueOf(latesStatusCreatedAtTime));
				
				int count = 0;
				ContentValues values = new ContentValues();
				
				for(Twitter.Status status : statusUpdates){
					long createdAt = status.createdAt.getTime();
					
					values.put(StatusData.C_ID, status.getId());
					values.put(StatusData.C_CREATED_AT, createdAt);
					values.put(StatusData.C_TEXT, status.getText());
					values.put(StatusData.C_USER, status.getUser().getName());
					values.put(StatusData.C_SOURCE, status.source); //Added by me
					
					Log.d(TAG, "Got status with id " + status.getId() + " Saving");
					
					this.getStatusData().insertOrIgnore(values);
					if(latesStatusCreatedAtTime < createdAt){
						count++;
					}
				}
				
				Log.d(TAG, count > 0 ? "Got " + count + " status updates" : "No new status updates");
				return count;
			}
			catch(RuntimeException e){
				Log.e(TAG, "Failed to fetch status updates", e);
				return 0;
			}
		}
	}
	
	public SharedPreferences getPrefs(){
		return this.prefs;
	}
}
