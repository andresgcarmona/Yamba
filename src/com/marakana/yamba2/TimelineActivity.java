package com.marakana.yamba2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity {
	DBHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	TimelineAdapter timelineAdapter;
	TimelineReceiver receiver;
	IntentFilter filter;
	
	static final String[] FROM = {DBHelper.C_CREATED_AT, DBHelper.C_USER, DBHelper.C_TEXT};
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	static final String SEND_TIMELINE_NOTIFICATIONS = "com.marakana.yamba.SEND_TIMELINE_NOTIFICATIONS";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		if(yamba.getPrefs().getString("username", null) == null){
			startActivity(new Intent(this, PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		
		receiver = new TimelineReceiver();
	    filter = new IntentFilter(UpdaterService.NEW_STATUS_INTENT);

		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		dbHelper = new DBHelper(this);
		db = dbHelper.getReadableDatabase();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		db.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.setupList();
	}
	
	static final ViewBinder VIEW_BINDER = new ViewBinder(){
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() != R.id.textCreatedAt) return false;
			
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText(relativeTime);
			
			return true;
		}
	};
	
	private void setupList(){
		cursor = db.query(DBHelper.TABLE, null, null, null, null, null, DBHelper.C_CREATED_AT + " DESC");
		startManagingCursor(cursor);
		
		//timelineAdapter = new TimelineAdapter(this, cursor);
		//listTimeline.setAdapter(timelineAdapter);
		
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
	    listTimeline.setAdapter(adapter);
	    
	    registerReceiver(receiver, filter, SEND_TIMELINE_NOTIFICATIONS, null);
	}
	
	class TimelineReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d("TimelineReceiver", "onReceived");
		}
	}
}
