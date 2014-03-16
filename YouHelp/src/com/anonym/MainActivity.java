package com.anonym;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.messaging.NotificationHub;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import android.app.ActionBar.OnNavigationListener;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements 
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationListener
{
	private String SENDER_ID = "304926128006";
	private GoogleCloudMessaging gcm;
	private NotificationHub hub;
	private LocationClient locationClient;
	private String userid = "1267167108";
	
	MyBroadcastReceiver mReceiver;
	
	private GoogleMap mMap;
	
	private static final int RESULT_SETTINGS = 1;
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	private static final String TAG = "com.anonym.youhelp.mainactivity";
	
	OnInfoWindowClickListener mapClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mReceiver = new MyBroadcastReceiver(this);
		this.mReceiver.setUserID(userid);
		registerReceiver(this.mReceiver, 
				         new IntentFilter("com.google.android.c2dm.intent.RECEIVE"));

		// start Facebook Login
//		try{
//			Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//			@Override
//			public void call(Session session, SessionState state, Exception exception) {
//				
//				if (session.isOpened()) {
//					Log.i(TAG, "Logged into FB...");
//					
//	            	// make request to the /me API
//	            	Request.newMeRequest(session, new Request.GraphUserCallback(){
//
//						@Override
//						public void onCompleted(GraphUser user, Response response) {
//			                  if (user != null) {
//			                	  String username = user.getName();
//			                	  userid = user.getId();
//			                	  
//			                	  Log.i(TAG, "User ID: " + userid +" Username: " + username);
//			                  }
//							
//						}
//
//	            	}).executeAsync();
//				}
//				else if( session.isClosed()){
//					Log.i(TAG, "Logged out from FB...");
//				}
//			}
//        	
//        });
//		} catch(Exception ex){
//			ex.printStackTrace();
//		}
	
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
		          android.R.layout.simple_spinner_dropdown_item);
		
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {
			
			// Get the same strings provided for the drop-down's ArrayAdapter
			String[] strings = getResources().getStringArray(R.array.action_list);
			
			  @Override
			  public boolean onNavigationItemSelected(int position, long itemId) {

			    return true;
			  }
		};
			
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);

		ensureMap();
		
		mapClickListener = new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				try{
					
					if( isNetworkAvailable() ) {
					
						ViewGroup mContainerView = (ViewGroup) findViewById(R.id.main_layout);
						LayoutTransition lt = new LayoutTransition();
						lt.disableTransitionType(LayoutTransition.CHANGING);
						mContainerView.setLayoutTransition(lt);

						View chatLayout = findViewById(R.id.chatGlanceLayoyt);
						chatLayout.setVisibility(View.VISIBLE);
						
						//Animation vanish = AnimationUtils.loadAnimation(MainActivity.this,
						//												R.anim.vanish);
						//findViewById(R.id.chatGlanceLayoyt).startAnimation(vanish);
						
						Location currentLocation = locationClient.getLastLocation();
						
						Intent intent = new Intent(MainActivity.this, SendActivity.class);
						Bundle b = new Bundle();
						b.putParcelable("currentLocation", currentLocation);

						intent.putExtras(b);
						intent.putExtra("userid", userid);
						startActivity(intent);
					}
					else {
						AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);
						
				   	 	dlgAlert.setMessage("Yau are not connected to network");
				   	 	dlgAlert.setTitle("YouHelp");
				   	 	dlgAlert.setPositiveButton("OK", null);
				   	 	dlgAlert.setCancelable(true);
				   	 	dlgAlert.create().show();
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
			}

		};
		mMap.setOnInfoWindowClickListener(mapClickListener);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);	
		mMap.setBuildingsEnabled(true);	

		Log.i(TAG, "UI set");
		
//		Intent thisIntent = getIntent();
//		Bundle bundle = thisIntent.getExtras();
//		if( bundle != null){
//			String locationURL = bundle.getString("reportedLocationUrl");
//			if( locationURL != null ) {
//				Location location = new Location("");
//				String[] tokens = locationURL.split(",");
////				location.setLatitude(Float.parseFloat(tokens[0])); 
////				location.setLongitude(Float.parseFloat(tokens[1]));
//				location.setLatitude(32.072072072072); 
//				location.setLongitude(34.871628036643);
//				this.showReportedPlace(location);
//			}
//		}
		
		LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		// Bound to updates to at most 30 min. and for 
		// geographical accuracies of more that 500 meters
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 
											  30 * 60 * 1000, 
											  500,
											  this);
		
		Log.i(TAG, "Location listener is set");
		
		// Check device for Play Services APK
		if( checkPlayServices() ) {
			
			// Prepare the global (i.e. class-shared) variables to be used
			// when Google Play Services are connected - see onConnected() method
			
			try{
					gcm = GoogleCloudMessaging.getInstance(this);
					String connectionString = "Endpoint=sb://variant.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=ZMuryU3+QPa8GYO29tnE3ji2R9gsZWzJZfcF/qbsDy8=";
					hub = new NotificationHub("geochathub", connectionString, this);
	
					// Location client depends on GooglePlay services and used to get the first location fix as fast as possible.
					// Further updates will come from previously initialized LocationManager
					locationClient = new LocationClient(this, this, this);
					locationClient.connect();						

			}
			catch(Exception ex){
				msbox(TAG, ex.getMessage());
			}
			
		}else{
			msbox(TAG, "There is no Google Play Services. Please install them before running this application");
		}
			

	}
	
	 @Override
	 protected void onStart() {
		 super.onStart();
		 
	     // Connect the client.
		 if( locationClient != null )
			 locationClient.connect();
	 }
	
	@Override
	protected void onResume(){
		
		//ensureMap();
		checkPlayServices();
		
		super.onResume();
	}

	 
	@Override
	protected void onStop() {

		// Disconnecting the client invalidates it.
		if( locationClient != null )
			locationClient.disconnect();
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){
		
		unregisterReceiver(this.mReceiver);
	}
	
	@Override
	public void onConnected(Bundle dataBundle) {

		Log.i(TAG, "Connected to GooglePlay");
		
		Location currentLocation = locationClient.getLastLocation();
		registerWithNotificationHubs(currentLocation);
		
		Log.i(TAG, "Registered with Notification Hub");

		if( currentLocation != null)
			showME(currentLocation);
 
	}

	@Override
	public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected from GooglePaly. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onLocationChanged(Location location) {

		try{

			Log.i(TAG, "Location fix received");
			
			if( location != null ){
				String provider = location.getProvider();
				
				String s = "Provider: " + provider +  "\n";
				s += "\tLatitude:  " + location.getLatitude() + "¡\n";
		        s += "\tLongitude: " + location.getLongitude() + "¡\n";
		        
		        if( location.hasSpeed() )
		        	s += "\tSpeed: " + location.getSpeed() + "¡\n";
		        else
		        	s += "\tNo speed reported";
		        
		        //Log.i(TAG, s);
		        
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
				
				showME(location);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void msbox(String str,String str2)
	{
	    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);                      
	    dlgAlert.setMessage(str2)
	    		.setTitle(str)           
	    		.setPositiveButton("OK", null)
	    		//.setCancelable(true)
	    		.create().show();

	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if( Session.getActiveSession() != null )
    	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
	
	@SuppressLint("NewApi")
	private void ensureMap() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.

	        }
	    }
	}
	
    private boolean isNetworkAvailable() {
    	
    	ConnectivityManager connectivityManager 
    	      = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    	return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
	private boolean checkPlayServices(){
		try
		{
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if( resultCode != ConnectionResult.SUCCESS ) {
				if( GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
					GooglePlayServicesUtil.getErrorDialog(resultCode, this, 
							PLAY_SERVICES_RESOLUTION_REQUEST).show();
				}
				else {
					//Log.i(TAG, "This device is not supported");
					finish();
				}
				
				return false;
			}
		}
		catch(Exception ex)
		{
			ex.getMessage();
		}

		return true;
	}
	
	private void registerWithNotificationHubs(Location location) {
	   
		if( location == null ) return;
		
		Address address = getAddress(location);
		if( address == null ) return;
		
		
		final List<String> tags = new ArrayList<String>();
		if( address.getCountryCode() != null ) {
			tags.add("Country:" + address.getCountryCode());
		}
		if( address.getPostalCode() != null ){
			tags.add("PostalCode:"+address.getPostalCode());
		}
//		if( address.getLocality() != null ) {
//			tags.add("Locality:" + address.getLocality());
//		}
		if( address.getAdminArea() != null ) {
			// State/Region
			tags.add("AdminArea:"+address.getAdminArea());
		}
		
		new AsyncTask<Object, Object, String>() {
	      
			Activity thisActivity;
			
			@Override
			protected void onPostExecute(String error){
	    	  	
	 			if( error.length() > 0){
	 				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(thisActivity);
				
	 				dlgAlert.setMessage(error);
	 				dlgAlert.setTitle("YouHelp");
	 				dlgAlert.setPositiveButton("OK", null);
	 				dlgAlert.setCancelable(true);
	 				dlgAlert.create().show();
	 			}
			}
			
		  @Override
	      protected String doInBackground(Object... params) {
			  
			  thisActivity = (Activity) params[0];
			  
	    	  String regid = "";
	    	  try {
	            regid = gcm.register(SENDER_ID);
	    	  } catch (Exception e) {
	    		  
	        	 return "Failed to register with GCM. Exception: " + e.getMessage();
	    	  }
	    	  
	         try{
	        	 hub.register(regid, tags.toArray(new String[tags.size()]));
	         }catch(Exception e1){

		 		return "Failed to register with Azure Notification Hub. Exception: " + 	e1.getMessage();
	         }
	         
	         return "";
	     }

	   }.execute(null, null, null);
	}
	

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	     
		switch (item.getItemId()) {
			case R.id.menu_about:{
			}
			
			case R.id.action_settings:{
				try{
					Intent intent = new Intent(this, SettingsActivity.class);
					startActivityForResult(intent, RESULT_SETTINGS);
				}
				catch(Exception ex){
					ex.getMessage();
				}

			}
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	private Address getAddress(Location loc){
		Context ctx = this.getBaseContext();
		
		Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(loc.getLatitude(), 
									 loc.getLongitude(),
									 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch(IllegalArgumentException e2){
			String errorString = "Illegal arguments " + 
								Double.toString(loc.getLatitude()) +
								" , " +
								Double.toString(loc.getLongitude()) +
								"passed to address service";
			Log.e("GeoChat", errorString);
			e2.printStackTrace();
			return null;
		}
		
		if( addresses != null 
				&& addresses.size() > 0){
			return addresses.get(0);
		}else{
			return null;
		}
		
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
		if (connectionResult.hasResolution()) {
			
		}
		else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
	}

	private void showME(Location location){

		Location tempLoc = new Location("");
		tempLoc.setLatitude(32.072072072);
		tempLoc.setLongitude(34.871628036);
		//showMarker(tempLoc, "She is there", "", BitmapDescriptorFactory.HUE_RED);

		showMarker(location, "You are here", "Say something", BitmapDescriptorFactory.HUE_AZURE);
	}
	
	public void showReportedPlace(Location location){
		showMarker(location, "Reportred place", "One", BitmapDescriptorFactory.HUE_ROSE);
	}
	
	private void showMarker(Location location, String title, String snippet,  float color){
		if( location == null ) {
			Log.i(TAG, "Location passed to showMarker() is invalid");
			return;
		}else if( mMap == null ) {
			Log.i(TAG, "Map is not ready when calling to showMarker()");
			return;
		}
	
		final LatLng ME = new LatLng(location.getLatitude(),
									location.getLongitude());
		
		final int zoomLevel = 16;
		// Move the camera instantly to the current location with a zoom.
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, zoomLevel));
		
		// Zoom in, animating the camera.
		mMap.animateCamera(CameraUpdateFactory.zoomIn());
		// Zoom out to specified zoom level, animating with a duration of 2 seconds.
		mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel), 2000, null);
		
		Marker meMarker = mMap.addMarker(new MarkerOptions()
        		.position(ME)
        		.title(title)
        		.snippet(snippet)
        		.icon(BitmapDescriptorFactory.defaultMarker(color)));
		meMarker.showInfoWindow();	
	}

	public void onClick_ViewMessages(View v){
		Intent intent = new Intent(MainActivity.this, ChatUsersActivity.class);
		startActivity(intent);
	}
	
}
