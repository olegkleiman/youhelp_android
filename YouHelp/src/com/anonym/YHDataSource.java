package com.anonym;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
							YHSQLiteHelper.COLUMN_CONTENT,
							YHSQLiteHelper.COLUMN_USERID,
							YHSQLiteHelper.COLUMN_DATECREATED };
	
	public YHDataSource(Context context){
		dbHelper = new YHSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public YHMessage createYHMessage(String content, 
									String userid, 
									Date dateCreated){
		
		ContentValues values = new ContentValues();
		values.put(YHSQLiteHelper.COLUMN_CONTENT, content);
		values.put(YHSQLiteHelper.COLUMN_USERID, userid);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(YHSQLiteHelper.COLUMN_DATECREATED, dateFormat.format(dateCreated));
		
		long insertID = database.insert(YHSQLiteHelper.TABLE_MESSAGES, null, values);
		
		Cursor cursor = database.query(YHSQLiteHelper.TABLE_MESSAGES, allColumns,
				YHSQLiteHelper.COLUMN_ID + " = " + insertID, null,
				null, null, null);
		cursor.moveToFirst();
		YHMessage newMessage = cursorToYHMessage(cursor);
		
		cursor.close();
		return newMessage;
	}
	
	public void deleteYHMessage(YHMessage message){
		long id = message.getId();
		database.delete(YHSQLiteHelper.TABLE_MESSAGES, 
				YHSQLiteHelper.COLUMN_ID + " = " + id, null);
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
	
	public List<YHMessage> getMessagesOfUser(String userid){
		List<YHMessage> messages = new ArrayList<YHMessage>();
		
		Cursor cursor = database.query(YHSQLiteHelper.TABLE_MESSAGES,
				allColumns,
				YHSQLiteHelper.COLUMN_USERID + " = '" + userid + "'", null, null, null, null);
		
		cursor.moveToFirst();
		while( !cursor.isAfterLast()){
			YHMessage message = cursorToYHMessage(cursor);
	
			messages.add(message);
			cursor.moveToNext();
		}
		
		cursor.close();
		return messages;
	}
	
	private YHMessage cursorToYHMessage(Cursor cursor){
		
		YHMessage message = new YHMessage();
		message.setId(cursor.getLong(0));
		message.setContent(cursor.getString(1));
		message.setUserID(cursor.getString(2));
		Date date = new Date(cursor.getLong(3)*1000);
		message.setDateCreated(date);
		
		return message;
	}
}
