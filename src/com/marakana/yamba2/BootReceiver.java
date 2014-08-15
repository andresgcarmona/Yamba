package com.marakana.yamba2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private final String TAG = "BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UpdaterService.class)); //
		Log.d(TAG, "onReceived");
	}
}
