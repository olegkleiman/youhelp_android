package com.anonym;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YHSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "yhMessages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTENT = "content";
	private static final String DATABASE_NAME = "yh";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_CONTENT
			+ " text not null);";
	
	
	public YHSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		
		database.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
