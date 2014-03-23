package com.anonym;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatUsersActivity extends Activity {

	ArrayList<String> usersNameList;
	private YHDataSource datasource;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatusers);
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatusers);
		
		if( datasource == null )
			datasource = new YHDataSource(this);
		
		datasource.open();
		final List<YHMessage> messages = datasource.getAllMessages();
		
		ArrayAdapter<YHMessage> adapter = new ArrayAdapter<YHMessage>(this,
				android.R.layout.simple_list_item_2, 
				android.R.id.text1,
				messages){
			
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					
						View view = super.getView(position, convertView, parent);
						
						TextView text1 = (TextView) view.findViewById(android.R.id.text1);
						TextView text2 = (TextView) view.findViewById(android.R.id.text2);
				    
						YHMessage msg = messages.get(position);
				    
						text1.setText(msg.getContent());
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date dateCreated = msg.getDateCreated() ;

						String text = "" +  dateFormat.format(dateCreated) + " " + msg.getUserId();
						text2.setText(text);
					
						return view;

				}
		};
		messagesList.setAdapter(adapter);
		
	
		 // register onClickListener to handle click events on each item
		messagesList.setOnItemClickListener(new OnItemClickListener()
           {
                // argument position gives the index of item which is clicked
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id){
						
					YHMessage msg = messages.get(position);
					
                    Intent intent = new Intent(getApplicationContext(), 
                    							ChatRoomActivity.class);
                    String userid = msg.getUserId();
                    intent.putExtra("userid", userid);
                    startActivity(intent);
      
               }

           });
	}

	public void onRefreshChatUsers(View view){
		onCreate(null);
	}
}
