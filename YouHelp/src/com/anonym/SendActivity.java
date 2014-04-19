package com.anonym;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.LocationClient;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.CellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SendActivity extends Activity {

	private Location currentLocation;
	private String userid;
	
	private static final String TAG = "com.anonym.youhelp.sendactivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		userid = extras.getString("userid");
		currentLocation = extras.getParcelable("currentLocation");

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		try{
			unregisterReceiver(sendReceiver);
			unregisterReceiver(deliveryReceiver);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void onClickEmergency(View view){
		
		try{
			
//			TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//			List<CellInfo> cells = tMgr.getAllCellInfo();
//			String mPhoneNumber = tMgr.getLine1Number();
			
			String SENT = "sent";
			String DELIVERED = "delivered";
			
			/* Create Pending Intents */
			Intent sentIntent = new Intent(SENT);
			final PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, 
															sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
			Intent deliveryIntent = new Intent(DELIVERED);
			final PendingIntent deliverPI = PendingIntent.getBroadcast(getApplicationContext(), 
										0, deliveryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			// Register for SMS send action
			registerReceiver(sendReceiver, new IntentFilter(SENT));
			registerReceiver(deliveryReceiver, new IntentFilter(DELIVERED));
		
			final SmsManager smsManager = SmsManager.getDefault();
			
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			if( sharedPrefs == null ) {
				Toast.makeText(this, "Failed to obtain SharedPreferences",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			List<String> smsNumbers = new ArrayList<String>();
				
			String smsNumber1 = sharedPrefs.getString("prefEmergencyNumber1", "");
			if( !smsNumber1.isEmpty() )
				smsNumbers.add(smsNumber1);
			String smsNumber2 = sharedPrefs.getString("prefEmergencyNumber2", "");
			if( !smsNumber2.isEmpty() )
				smsNumbers.add(smsNumber2);
			String smsNumber3 = sharedPrefs.getString("prefEmergencyNumber3", "");
			if( !smsNumber3.isEmpty() )
				smsNumbers.add(smsNumber3);

			if( smsNumbers.isEmpty() ) {
				msgBox("No emergency numbers", "Please provide the emergency numbers in settings.");
				return;
			}
			
			StringBuilder sb = new StringBuilder("I'm in an emergency. Please help!\n Map link: \n"); 
			sb.append("http://maps.google.com/?ie=UTF&z=138&hq=&ll="); // here.com/");
			
	   	    double lat = currentLocation.getLatitude();
	   	    double lon = currentLocation.getLongitude();
	   	 	String strCurrentLocation = String.format(Locale.US, "%.13f;%.13f", lat, lon);
			sb.append(strCurrentLocation);
			
			String now = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			sb.append(".\n Sent at ");
			sb.append(now);
			
			final String emergencyMessage = sb.toString();
			
			SendSmsWithIntent(smsNumbers, emergencyMessage);
			
			//final Activity sendingActivity = this;
			
			//int nSkip = 0;
			for(final String smsNumber : smsNumbers){
			
				if( !smsNumber.isEmpty() ) {
					
					Log.i(TAG, "Sending to " +  smsNumber + ".\n Emergency message: " + emergencyMessage);
					smsManager.sendTextMessage(smsNumber, null, emergencyMessage, 
											   sentPI, deliverPI);
					//Toast.makeText(sendingActivity, "SMS sent", Toast.LENGTH_SHORT).show();
					
//					final Runnable runn = new Runnable() {
//	
//						@Override
//						public void run() {
//						
//							Log.i(TAG, "Sending to " +  smsNumber + ".\n Emergency message: " + emergencyMessage);
//							smsManager.sendTextMessage(smsNumber, null, emergencyMessage, 
//													   sentPI, deliverPI);
//							Toast.makeText(sendingActivity, "SMS sent", Toast.LENGTH_SHORT).show();
//						
//						}
//					};
//				
//
//					Handler handler = new Handler();
//					handler.postDelayed(runn, (nSkip++) * 3000);
				}
			}
			
		} catch(Exception ex){
			
			Toast.makeText(this,ex.getMessage().toString(),
							Toast.LENGTH_LONG).show();
		}
		
		finish();
		

	}

	private static class SmsRunnable implements Runnable{

		private final String message;
		private final String smsNumber;
		
		SmsRunnable(final String message, final String smsNumber){
			this.message = message;
			this.smsNumber = smsNumber;
		}
		
		@Override
		public void run() {
			final SmsManager smsManager = SmsManager.getDefault();
			
			
		}
		
	}
	
	public void msgBox(String title,String message)
	{
	    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);                      
	    dlgAlert.setMessage(message)
	    		.setTitle(title)           
	    		.setPositiveButton("OK", null)
	    		//.setCancelable(true)
	    		.create().show();

	}
	
	@SuppressWarnings("unused")
	private void SendSmsWithIntent(List<String> smsNumbers, String smsMesssage){
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String smsNumber = smsNumbers.get(0); //// sharedPrefs.getString("prefEmergencyNumber1", "");
		
		Uri uri = Uri.parse("smsto:" + smsNumber);
		Intent i = new Intent(Intent.ACTION_SENDTO, uri);
		i.putExtra("sms_body", smsMesssage);  
		//i.setPackage("com.whatsapp");  
		startActivity(i);		
	}
	
	private BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Toast.makeText(getApplicationContext(), "Deliverd",
			         Toast.LENGTH_LONG).show();
			
		}
		
	};
	
	private BroadcastReceiver sendReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String result = "";
			
			switch( getResultCode() ){
			case Activity.RESULT_OK:
				result = "Transmission successful";
				break;
				
			case Activity.RESULT_CANCELED:
				result = "SMS not delivered";
                break;                        
				
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				result = "Transmission failed";
				break;
				
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				result = "Radio off";
				break;
				
		    case SmsManager.RESULT_ERROR_NULL_PDU:
		           result = "No PDU defined";
		           break;
		           
		    case SmsManager.RESULT_ERROR_NO_SERVICE:
		           result = "No service";
		           break;
			};
			
			Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
		}
	};
	
	public void onClickHelp(View view) {
		
		if( isNetworkAvailable() )
		{
			int buttonID = view.getId();
			String strTilte = "";
		
			Button btn = (Button)findViewById(buttonID);
			strTilte = btn.getText().toString();
	
			String serviceURL = getString(R.string.send_toast_service_url);
			// Should be something like http://youhelp.cloudapp.net/YouHelpService.svc/sendtoast?title=;
			
			StringBuilder sb = new StringBuilder(serviceURL); 
			sb.append("?title=");
			sb.append(strTilte);
	  	 	
			sb.append("&subtitle=");
			
	   	    double lat = currentLocation.getLatitude();
	   	    double lon = currentLocation.getLongitude();
	   	 	String strCurrentLocation = String.format(Locale.US, "%.13f;%.13f", lat, lon);
			
			sb.append(strCurrentLocation);
			sb.append("&tags=null");
			
			sb.append("&userid=");
			sb.append(userid);
			
			String uri = sb.toString();
			
//			TelephonyManager tMngr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//			String phoneNuber = tMngr.getLine1Number();
			
			PerformCheckInAsyncTask sendTask = new PerformCheckInAsyncTask();
			sendTask.ParentActivity = this;
			sendTask.execute(uri);
			
			finish();
		}
		else{
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
			
	   	 	dlgAlert.setMessage("Yau are not connected to network");
	   	 	dlgAlert.setTitle("YouHelp");
	   	 	dlgAlert.setPositiveButton("OK", null);
	   	 	dlgAlert.setCancelable(true);
	   	 	dlgAlert.create().show();
		}
	}
	
    private boolean isNetworkAvailable() {
    	
    	ConnectivityManager connectivityManager 
    	      = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
