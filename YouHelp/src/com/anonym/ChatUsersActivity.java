package com.anonym;

import java.util.ArrayList;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatusers);
		
		ListView usersList = (ListView)findViewById(R.id.lvChatusers);
		
		usersNameList = new ArrayList<String>();
		
		getUsersNames();
		
		ArrayAdapter<String> arrayAdapter =      
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, usersNameList);
		
		usersList.setAdapter(arrayAdapter); 
		
		 // register onClickListener to handle click events on each item
		usersList.setOnItemClickListener(new OnItemClickListener()
           {
                    // argument position gives the index of item which is clicked
			@Override
                   public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
                   {
                           String selectedUser= usersNameList.get(position);
                           Toast.makeText(getApplicationContext(), "User Selected : "+ selectedUser,   
                        		   			Toast.LENGTH_LONG).show();
                        }


           });
	}
	
	void getUsersNames(){
		usersNameList.add("USER ONE");
		usersNameList.add("USER TWO");
		usersNameList.add("USER THREE");
    }
}
