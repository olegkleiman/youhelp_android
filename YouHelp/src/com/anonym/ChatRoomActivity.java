package com.anonym;

import java.util.Date;
import java.util.List;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatRoomActivity extends Activity {

	private static final String TAG = "com.anonym.youhelp.chatroomactivity";
	
	private YHDataSource datasource;
	private ChatAdapter chatAdapter;
	String toUserid;
	private String myUserID;
	
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;
	private String GCM_SENDER_ID = "939177037001";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);
		
		ServiceConnection mConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name,
					IBinder service) {
				
//				DispatchService dipsService = (DispatchService)service;
//				if( dipsService != null ) 
					//dipsService.setChatActivity(this);
					Log.i("", "DispService connected");
				
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
			}
		
		};
		
		Intent serviceIntent = new Intent(this, DispatchService.class);
		this.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
		
//		Context appCtx = getApplicationContext();
//		
//		PackageManager pm = getApplicationContext().getPackageManager();
//		Intent intent = new Intent("com.google.android.c2dm.intent.RECEIVE");
//		List<ResolveInfo> receivers = pm.queryBroadcastReceivers(intent, 0);
//		for(final ResolveInfo rInfo : receivers){
//			Log.i(TAG, rInfo.toString() );
//		}
		
		ListView messagesList = (ListView)findViewById(R.id.lvChatRoom);
		
		Bundle extras = getIntent().getExtras();
		toUserid = extras.getString("userid");
		
		TextView titleView = (TextView)findViewById(R.id.txtHeader2);
		titleView.setText("Conversation with " + toUserid);
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		myUserID = sharedPrefs.getString("prefUsername", "");
		
		this.gcm = GoogleCloudMessaging.getInstance(this);
		String connectionString = "Endpoint=sb://variant.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=WYlEAkd3+RzDMkHd9JK+TVG5ahRcWTwccl9CKCPNZ50=";
		hub = new NotificationHub("youhelpchat", connectionString, this);
		
		registerWithNotificationHubs();
		
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
	
	private void registerWithNotificationHubs()
	{
		new AsyncTask<Object, Object, Object>() {
			
			@Override
			protected Object doInBackground(Object... params) {
				try {
					String gcmRegID = gcm.register(GCM_SENDER_ID);
					hub.register(gcmRegID, myUserID);
				} catch(Exception e) {
					return e;
				}
				
				return null;
			}
			
		}.execute(null, null, null);
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
			
			String serviceURL = getString(R.string.send_chatmessage_url);
			// Should be something like http://youhelp.cloudapp.net/YouHelpService.svc/sendchatmessage?content=;
			
			StringBuilder sb = new StringBuilder(serviceURL); 
			sb.append("?content=");
			sb.append(strMessage);
	  	 	
			sb.append("&fromuserid=");
			sb.append(myUserID);
			
			sb.append("&touserid=");
			sb.append(toUserid);
			
			String uri = sb.toString();
			
			PerformCheckInAsyncTask sendTask = new PerformCheckInAsyncTask();
			sendTask.ParentActivity = this;
			sendTask.execute(uri);
			
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
