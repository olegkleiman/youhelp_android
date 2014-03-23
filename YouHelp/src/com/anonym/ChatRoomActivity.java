package com.anonym;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatRoomActivity extends Activity {

	private YHDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatRoom);
		
		Bundle extras = getIntent().getExtras();
		String userid = extras.getString("userid");
		
		try{
			
			if( datasource == null )
				datasource = new YHDataSource(this);
		
			datasource.open();
		
			final List<YHMessage> messages = datasource.getMessagesFromUser(userid);
		
			ChatReplicaAdapter chatAdapter = new ChatReplicaAdapter(this, 
					R.layout.chatroom_item_row,
					messages);
		
//			ArrayAdapter<YHMessage> adapter = new ArrayAdapter<YHMessage>(this,
//				android.R.layout.simple_list_item_1, 
//				messages);
		
			messagesList.setAdapter(chatAdapter);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
