package com.marakana.yamba2;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter{
	static final String[] FROM = {DBHelper.C_CREATED_AT, DBHelper.C_USER, DBHelper.C_TEXT};
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	
	public TimelineAdapter(Context context, Cursor cursor){
		super(context, R.layout.row, cursor, FROM, TO);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		
		long timestamp = cursor.getLong(cursor.getColumnIndex(DBHelper.C_CREATED_AT));
		TextView textCreatedAt = (TextView) view.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}
}
