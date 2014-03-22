package com.anonym;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
						text2.setText(msg.getUserId());
					
						return view;

				}
		};
		messagesList.setAdapter(adapter);
		
	
		 // register onClickListener to handle click events on each item
		messagesList.setOnItemClickListener(new OnItemClickListener()
           {
                // argument position gives the index of item which is clicked
				@Override
				public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3){
						
					YHMessage msg = messages.get(position);
                    Toast.makeText(getApplicationContext(), "Message Content : "+ msg.getContent(),   
                        		   			Toast.LENGTH_SHORT).show();
                        
               }


           });
	}

	public void onRefreshChatUsers(View view){
		onCreate(null);
	}
}
