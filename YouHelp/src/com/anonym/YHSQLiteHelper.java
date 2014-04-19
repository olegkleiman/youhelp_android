package com.anonym;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YHSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MESSAGES = "yhMessages";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_DATECREATED = "date_created";
	public static final String COLUMN_TOUSERID = "touserid";
	private static final String DATABASE_NAME = "yh";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MESSAGES + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " 
			+ COLUMN_CONTENT + " unicode text not null, " 
			+ COLUMN_USERID + " unicode text not null, " 
			+ COLUMN_DATECREATED + " date not null,"
			+ COLUMN_TOUSERID + " unicode text)";
	
	public YHSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		
		try{
			database.execSQL(DATABASE_CREATE);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newversion) {
		
		db.execSQL("drop table if exists " + TABLE_MESSAGES);
		onCreate(db);
		
	}
	
	@Override
	public void onOpen(SQLiteDatabase db){
		
	}

}
