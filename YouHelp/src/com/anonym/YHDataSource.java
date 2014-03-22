package com.anonym;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class YHDataSource {
	private SQLiteDatabase database;
	private YHSQLiteHelper dbHelper;
	private String[] allColumns = { YHSQLiteHelper.COLUMN_ID, 
							YHSQLiteHelper.COLUMN_CONTENT };
	
	public YHDataSource(Context context){
		dbHelper = new YHSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public YHMessage createYHMessage(String content){
		
		ContentValues values = new ContentValues();
		values.put(YHSQLiteHelper.COLUMN_CONTENT, content);
		long insertID = database.insert(YHSQLiteHelper.TABLE_MESSAGES, null, values);
		
		Cursor cursor = database.query(YHSQLiteHelper.TABLE_MESSAGES, allColumns,
				YHSQLiteHelper.COLUMN_ID + " = " + insertID, null,
				null, null, null);
		cursor.moveToFirst();
		YHMessage newMessage = cursorToYHMessage(cursor);
		
		cursor.close();
		return newMessage;
	}
	
	public List<YHMessage> getAllMessages() {
		List<YHMessage> messages = new ArrayList<YHMessage>();
		
		
		Cursor cursor = database.query(YHSQLiteHelper.TABLE_MESSAGES,
										allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while( !cursor.isAfterLast()){
			YHMessage message = cursorToYHMessage(cursor);
			messages.add(message);
			cursor.moveToNext();
		}
		
		// make sure to close the cursor
		cursor.close();
		return messages;
	}
	
	private YHMessage cursorToYHMessage(Cursor cursor){
		YHMessage message = new YHMessage();
		message.setId(cursor.getLong(0));
		message.setContent(cursor.getString(1));
		
		return message;
	}
}
