package com.anonym;

import java.util.Date;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "MyBroadcastReceiver";
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Context ctx;	
	MainActivity mainActivity;
	String userId; 
	
	private YHDataSource datasource;
	
	public MyBroadcastReceiver(MainActivity activity){
		this.mainActivity = activity;
	}
	
	public void setUserID(String userID){
		this.userId = userID;
	}
	
	private void persistMessage(Context context, String content, String userid){

		try{
		if( datasource == null)
			datasource = new YHDataSource(mainActivity);
		
		datasource.open();
		 
		Date date = new Date();
		datasource.createYHMessage(content, userid, date, "");
		datasource.close();
		
		}catch(Exception ex){
			
			Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();

		}
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		try{
			Log.d(TAG, intent.getAction());
			
			if( !intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) 
				return;
			
			Bundle extras = intent.getExtras();
			if( extras == null ) {
				Toast.makeText(context, "Empty Notification Received", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String title = extras.getString("msg");
			String userid = extras.getString("userid");
			String coords = extras.getString("coords");
			String[] tokens = coords.split(";");
			if( tokens.length != 2 ) 
				return;
			
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			
			if( sharedPrefs == null )
				return;

			String prefUserID = sharedPrefs.getString("prefUsername", "");
			// Do not receive from yourself
			if( prefUserID.length() != 0
				&& prefUserID.equals(userid) ) { // current user == sending user
					return;
			}
			
			persistMessage(context, title, userid);

			// Assume Google Maps as default
			String strMapAppCode = sharedPrefs.getString("prefMapApps", "2");
			int mapCode = Integer.parseInt(strMapAppCode);

			switch( mapCode )
			{
				case 1: // Waze
				{
					StringBuilder sb = new StringBuilder("waze://?ll=");
					sb.append(tokens[0]);
					sb.append(",");
					sb.append(tokens[1]);
					sb.append("&z=6");
					
					// center waze map to lat / lon:
					String url = sb.toString(); // "waze://?ll=32.072072072072075,34.8716280366431456&z=6"; // "waze://?q=Jerusalem";
					
					Intent wazeIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
					wazeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(wazeIntent);
					
					Location location = new Location("");
					location.setLatitude(Float.parseFloat(tokens[0])); 
					location.setLongitude(Float.parseFloat(tokens[1]));
					showLocationOnMainActivity(mainActivity, location, title, userid);
				}
				break;
		
				case 2: // Google Maps
				{
					StringBuilder sb = new StringBuilder("geo:0,0?q=");
					sb.append(tokens[0]);
					sb.append(",");
					sb.append(tokens[1]);
					sb.append("(" + title + " from " + userid + ")");
					
					String url = sb.toString();
					
					Intent gmIntent = new Intent(android.content.Intent.ACTION_VIEW, 
						    //Uri.parse("http://maps.google.com/maps?daddr=32.072072072072075,34.8716280366431456&mode=driving"));
							//Uri.parse("geo:0,0?q=32.072072072072075,34.8716280366431456(Reported Place)")
							Uri.parse( url )
							);
					gmIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
					gmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(gmIntent);

					Location location = new Location("");
					location.setLatitude(Float.parseFloat(tokens[0])); 
					location.setLongitude(Float.parseFloat(tokens[1]));
					showLocationOnMainActivity(mainActivity, location, title, userid);

				}
				break;
				
			}

			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		    ctx = context;
		        
		    String messageType = gcm.getMessageType(intent);
		    
		    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
		            sendNotification("Send error: " + intent.getExtras().toString());
		    } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
		            sendNotification("Deleted messages on server: " + 
		                    intent.getExtras().toString());
		    } else {
		    	sendNotification(title);
		    }
		        
		    setResultCode(Activity.RESULT_OK);

		}
		catch ( ActivityNotFoundException ex  )
		{
			Intent wazeIntent =
					new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
			wazeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(wazeIntent);
		}	
		catch ( Exception ex  ) {
			Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
		

	}

	private void showLocationOnMainActivity(MainActivity mainActivity, 
											Location location,
											String title,
											String userid)
	{
		
		if( mainActivity != null ){
			
			//location.setLatitude(32.072072072072); 
			//location.setLongitude(34.871628036643);
			
			mainActivity.addReportedLocation(location, title, userid);
			mainActivity.showLocations();

		}
	}
	
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
		          new Intent(ctx, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder =
		          new NotificationCompat.Builder(ctx)
		          .setSmallIcon(R.drawable.ic_launcher)
		          .setVibrate(new long[] { 500, 500 })
		          .setContentTitle("You Help")
		          .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
		          .setStyle(new NotificationCompat.BigTextStyle()
		                     .bigText(msg))
		          .setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
