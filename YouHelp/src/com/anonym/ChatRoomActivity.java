package com.anonym;

import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity {

	private YHDataSource datasource;
	private ChatAdapter chatAdapter;
	String toUserid;
	private String myUserID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatRoom);
		
		Bundle extras = getIntent().getExtras();
		toUserid = extras.getString("userid");
		
		TextView titleView = (TextView)findViewById(R.id.txtHeader2);
		titleView.setText("Conversation with " + toUserid);
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		myUserID = sharedPrefs.getString("prefUsername", "");
		
		try{
			
			if( datasource == null )
				datasource = new YHDataSource(this);
		
			datasource.open();
			
			final List<YHMessage> messages = datasource.getMessagesOfUser(toUserid);
		
			chatAdapter = new ChatAdapter(this, 
										R.layout.chatroom_item_row,
										messages,
										myUserID);
			
//	        View header = (View)getLayoutInflater().inflate(R.layout.chatroom_header_row, null);
//	        TextView headerText = (TextView)header.findViewById(R.id.txtHeader);
//	        if( headerText != null)
//	        	headerText.setText("Conversation with " + toUserid);
	        
//	        messagesList.addHeaderView(header);
	        
			messagesList.setAdapter(chatAdapter);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void onCallMeChat(View view){
		
	}
	
	public void onDelteChat(View view){
		datasource.deleteAllMessagesOfUser(toUserid);
		chatAdapter.clear();
	}
	
	public void onSendChatMessage(View view) {
		
		EditText txtMessage = (EditText)findViewById(R.id.txtMessage);
		String strMessage = txtMessage.getText().toString();
		if( strMessage.isEmpty() )
			return;
		
		try {
			
			persistMessage(strMessage);
			txtMessage.setText("");
			
			YHMessage message = new YHMessage(0, strMessage);
			message.setUserID(myUserID);
			chatAdapter.add(message);
			
		}catch(Exception ex){
			
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	private void persistMessage(String content){

		if( datasource == null)
			datasource = new YHDataSource(this);
		
		datasource.open();
		 
		Date date = new Date();
		datasource.createYHMessage(content, this.myUserID, date, toUserid);
		datasource.close();
	
	}
}
