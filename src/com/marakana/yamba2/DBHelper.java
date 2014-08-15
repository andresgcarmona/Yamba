package com.marakana.yamba2;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	static final String TAG = "DBHelper";
	static final String DB_NAME = "timeline.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "timeline";
	static final String C_ID = BaseColumns._ID;
	static final String C_CREATED_AT = "created_at";
	static final String C_SOURCE = "source";
	static final String C_TEXT = "txt";
	static final String C_USER = "user";
	Context context;
	
	public DBHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABLE + " (" + C_ID + " int PRIMARY KEY, " +
					 C_CREATED_AT + " int, " + C_USER + " text, " + C_TEXT + " text, " + C_SOURCE + " text)";
		
		db.execSQL(sql);
		Log.d(TAG, "onCreated sql " + sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		Log.d(TAG, "onUpdated");
		onCreate(db);
	}

}
	