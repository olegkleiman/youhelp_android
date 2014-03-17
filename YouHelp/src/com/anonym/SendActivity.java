package com.anonym;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SendActivity extends Activity {

	private Location currentLocation;
	private String userid;
	
	@SuppressLint("NewApi")
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
			
			new PerformCheckInAsyncTask().execute(uri);
			
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
