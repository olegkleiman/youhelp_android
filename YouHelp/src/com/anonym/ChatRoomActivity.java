package com.anonym;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity {

	private YHDataSource datasource;
	String toUserid;
	private String myUserID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatRoom);
		
		Bundle extras = getIntent().getExtras();
		toUserid = extras.getString("userid");
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		myUserID = sharedPrefs.getString("prefUsername", "");
		
		try{
			
			if( datasource == null )
				datasource = new YHDataSource(this);
		
			datasource.open();
		
			//datasource.deleteAllMessagesOfUser(userid);
			
			final List<YHMessage> messages = datasource.getMessagesOfUser(toUserid);
		
			ChatAdapter chatAdapter = new ChatAdapter(this, 
					R.layout.chatroom_item_row,
					messages,
					myUserID);
			
	        View header = (View)getLayoutInflater().inflate(R.layout.chatroom_header_row, null);
	        TextView headerText = (TextView)header.findViewById(R.id.txtHeader);
	        if( headerText != null)
	        	headerText.setText("Conversation with " + toUserid);
	        
	        messagesList.addHeaderView(header);
	        
			messagesList.setAdapter(chatAdapter);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void onSendChatMessage(View view) {
		EditText txtMessage = (EditText)findViewById(R.id.txtMessage);
		String strMessage = txtMessage.getText().toString();
		if( strMessage.isEmpty() )
			return;
		
		persistMessage(this, strMessage);

	}
	
	
	private void persistMessage(Context context, String content){

		try{
				if( datasource == null)
					datasource = new YHDataSource(this);
				
				datasource.open();
				 
				Date date = new Date();
				datasource.createYHMessage(content, this.myUserID, date, toUserid);
				datasource.close();
		
		}catch(Exception ex){
			
			Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();

		}
		
	}
}
