package com.anonym;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChatUsersActivity extends Activity {

	ArrayList<String> usersNameList;
	private YHDataSource datasource;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatusers);
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatusers);
		
		datasource = new YHDataSource(this);
		datasource.open();
		
		List<YHMessage> messages = datasource.getAllMessages();
		ArrayAdapter<YHMessage> adapter = new ArrayAdapter<YHMessage>(this,
				android.R.layout.simple_list_item_1, messages);
		messagesList.setAdapter(adapter);
		
//		usersNameList = new ArrayList<String>();
//		
//		getUsersNames();
//		
//		ArrayAdapter<String> arrayAdapter =      
//                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, usersNameList);
//		
//		messagesList.setAdapter(arrayAdapter); 
//		
//		 // register onClickListener to handle click events on each item
//		messagesList.setOnItemClickListener(new OnItemClickListener()
//           {
//                    // argument position gives the index of item which is clicked
//			@Override
//                   public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
//                   {
//                           String selectedUser= usersNameList.get(position);
//                           Toast.makeText(getApplicationContext(), "User Selected : "+ selectedUser,   
//                        		   			Toast.LENGTH_LONG).show();
//                        }
//
//
//           });
	}
	
	void getUsersNames(){
		usersNameList.add("USER ONE");
		usersNameList.add("USER TWO");
		usersNameList.add("USER THREE");
    }
}
