package com.anonym;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.facebook.Session;
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
import android.preference.PreferenceManager;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
	private class RegisterOnAzureHub extends AsyncTask<Object, Object, String>{

		Activity thisActivity;
		String gcmRegID;
		List<String> tags;
		
		public RegisterOnAzureHub setGCMRegID(String regID) {
			this.gcmRegID = regID;
			return this;
		}
		
		public RegisterOnAzureHub setTags(List<String> _tags){
			this.tags = _tags;
			return this;
		}
		
		// return value from doInBackground passed here
		@Override
		protected void onPostExecute(String error){
    	  	
 			if( error.length() > 0){
 				
 				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(thisActivity);
			
 				dlgAlert.setMessage(error);
 				dlgAlert.setTitle(TAG);
 				dlgAlert.setPositiveButton("OK", null);
 				dlgAlert.setCancelable(true);
 				dlgAlert.create().show();
 			}
		}
		
		@Override
		  protected String doInBackground(Object... params) {
			  
			  thisActivity = (Activity) params[0];
  
			 try{

				 //hub.unregisterAll(gcmRegID);
				 hub.register(gcmRegID, tags.toArray(new String[tags.size()]));
				 
				 StringBuilder sb = new StringBuilder();
				 for(String tag: tags){
					 sb.append(tag);
				 }
				 
				 Log.i(TAG, "Registered on Notification Hub with following tags: " + sb.toString());
				 
			 }catch(Exception ex){
				 
				return "Failed to register with Azure Notification Hub. Exception: " + 	ex.getMessage();
			 }
		
			 return "";
		 }

	}
	
	private class RegisterOnGCM extends AsyncTask<Activity, Object, String>{

		private Activity activity;
		private GoogleCloudMessaging gcm;
		private String GCM_SENDER_ID = "304926128006";
		public static final String GCM_PROPERTY_REG_ID = "registration_id";
		
		
		@Override
        protected void onPostExecute(String error) {
			
 			if( error.length() > 0){
 				
 				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(activity);
			
 				dlgAlert.setMessage(error);
 				dlgAlert.setTitle(TAG);
 				dlgAlert.setPositiveButton("OK", null);
 				dlgAlert.setCancelable(true);
 				dlgAlert.create().show();
 			}
        }
		
		@Override
		protected String doInBackground(Activity... params) {
			
			String msg = "";
			
			this.activity = params[0];
			
			Context context = this.activity;
			
			this.gcm = GoogleCloudMessaging.getInstance(context);
			
			String gcmRegID = "";
			
			try {
				
				//gcm.unregister();
				gcmRegID = gcm.register(GCM_SENDER_ID);
				Log.i(TAG, "GCM Registration ID obtained: " + gcmRegID);
				
				storeRegistrationID(context, gcmRegID);
				Log.i(TAG, "GCM Registration ID stored");
	
			} catch (IOException ex) {
				return ex.getMessage();
			}

			return msg;
		}
		
		private void storeRegistrationID(Context context, String regId){
			
			final SharedPreferences prefs = context.getSharedPreferences("GCM_PREFS", Context.MODE_PRIVATE);
			int appVersion = getAppVersion(context);
			Log.i(TAG, "Saving regId on app version " + appVersion);
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(GCM_PROPERTY_REG_ID, regId);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}
		
	}
	
	public static final String GCM_PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */	
	public String getGCMRegistrationID(Context context) {
		final SharedPreferences prefs = this.getSharedPreferences("GCM_PREFS", Context.MODE_PRIVATE);
		String registrationId = prefs.getString(GCM_PROPERTY_REG_ID, "");
	    
		if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
		
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	//private String SENDER_ID = "304926128006";
	//private GoogleCloudMessaging gcm;
	private NotificationHub hub;
	private LocationClient locationClient;
	//private String userid = "1267167108";
	
	MyBroadcastReceiver mReceiver;
	
	private GoogleMap gMap;
	
	private static final int RESULT_SETTINGS = 1;
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	private static final String TAG = "com.anonym.youhelp.mainactivity";
	
	OnInfoWindowClickListener mapClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String prefUserID = sharedPrefs.getString("prefUsername", "");

		if(prefUserID.length() == 0 ){
			
			try{
				Intent intent = new Intent(this, RegisterWizard1.class);
				startActivityForResult(intent, 1);
			} catch(Exception ex){
				ex.printStackTrace();
			}
			
			return;
		}

		InitStuff();	

	}
	
	private void InitStuff(){
		
		this.mReceiver = new MyBroadcastReceiver(this);
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
			//String[] strings = getResources().getStringArray(R.array.action_list);
			
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
						
						if( marker.getSnippet().contains("Reported by") ){
							
							try{
								Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
								String snippet = marker.getSnippet();
								String[] tokens = snippet.split(" ");
								String userid = tokens[2];
			                    intent.putExtra("userid", userid);
			                    startActivity(intent);
							} catch(Exception ex){
								ex.printStackTrace();
							}
		                    
						}else{
							
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
							
							SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
							String prefUserID = sharedPrefs.getString("prefUsername", "");
							
							intent.putExtra("userid", prefUserID);
							startActivity(intent);
						}
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
					Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
				}
				
			}

		};
		gMap.setOnInfoWindowClickListener(mapClickListener);
		gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);	
		gMap.setBuildingsEnabled(true);	

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "UI set");
		}
		
		LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		
		// Bound to updates to at most 3 sec. and for 
		// geographical accuracies of more that 300 meters
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 
											  3 * 1000, 
											  300,
											  this);
		
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Location listener is set");
		}
		
		// Check device for Play Services APK
		if( checkPlayServices() ) {
			
			// Prepare the global (i.e. class-shared) variables to be used
			// when Google Play Services are connected - see onConnected() method
			
			try{
				//if( getGCMRegistrationID(this).isEmpty() ) {
					new RegisterOnGCM().execute(this, null, null);
					Log.i(TAG, "Registration with GCM is schedulled");
				//} else {
				//	Log.i(TAG, "GCM Registration skipped");
				//}
					String connectionString = "Endpoint=sb://variant.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=qW1ebXOAAkJxRLQ0eejGvfmr1gvgDdaQmjrD/pY9XcE=";
					hub = new NotificationHub("youhelphub", connectionString, this);
					
					// Location client depends on GooglePlay services and used to get the first location fix as fast as possible.
					// Further updates will come from previously initialized LocationManager
					locationClient = new LocationClient(this, this, this);
					locationClient.connect();						

			}
			catch(Exception ex){
				msBox(TAG, ex.getMessage());
			}
			
		}else{
			msBox(TAG, "There is no Google Play Services. Please install them before running this application");
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
		super.onDestroy();
	}
	
	@Override
	public void onConnected(Bundle dataBundle) {

		//Toast.makeText(this, "Connected to GooglePlay", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "Connected to GooglePlay");
		
		Location currentLocation = locationClient.getLastLocation();
		if( currentLocation != null){
			
			String gcmRegID = this.getGCMRegistrationID(this);
			if( !gcmRegID.isEmpty() ) {
				registerWithNotificationHubs(currentLocation, gcmRegID);
			}
			
			setMyLocation(currentLocation);
			showLocations();
		}
 
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
		        	s += "\tSpeed: " + location.getSpeed() + "\n";
		        else
		        	s += "\tNo speed reported";
		        
		        Log.i(TAG, s);
		        
				Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
				
				setMyLocation(location);
				showLocations();
				
				String gcmRegID = this.getGCMRegistrationID(this);
				if( !gcmRegID.isEmpty() ) {
					registerWithNotificationHubs(location, gcmRegID);
				}
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
	
	public void msBox(String title,String message)
	{
	    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);                      
	    dlgAlert.setMessage(message)
	    		.setTitle(title)           
	    		.setPositiveButton("OK", null)
	    		//.setCancelable(true)
	    		.create().show();

	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      
      if( requestCode == 1) { // Buddy
    	  InitStuff();
      }
      else if( requestCode == 2) { // Check FB Session
	      if( Session.getActiveSession() != null )
	    	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
      }
    }
	
	@SuppressLint("NewApi")
	private void ensureMap() {
	    
		try{
		// Do a null check to confirm that we have not already instantiated the map.
	    if (gMap == null) {
	        gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (gMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.

	        }
	    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
    private boolean isNetworkAvailable() {
    	
    	ConnectivityManager connectivityManager;
    	NetworkInfo activeNetworkInfo;
    	
    	try{
    		
    		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    		activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    		
    		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    	}
    	catch(Exception ex){
    		return false;
    	}
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
					Log.i(TAG, "This device is not supported");
					finish();
				}
				
				return false;
			}
		}
		catch(Exception ex){
			
			ex.getMessage();
		}

		return true;
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private void registerWithNotificationHubs(Location location, String gcmRegID) {
	   
		if( location == null ) return;
		
		final List<String> tags = getTags(location);

		RegisterOnAzureHub task = new RegisterOnAzureHub();
		task.setTags(tags).setGCMRegID(gcmRegID).execute(this, null, null);
	};

	private List<String> getTags(Location location){
		final List<String> tags = new ArrayList<String>();
		
		try{
			Address address = getAddress(location);
			if( address == null ) {
				Toast.makeText(this, "Geocoder Service is available, but no address was reported", Toast.LENGTH_SHORT).show();
				return tags;
			}

//			if( address.getCountryCode() != null ) {
//				tags.add("Country:" + address.getCountryCode());
//			}
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
		}catch(Exception ex){
			msBox("Unable to get address from Geocoder Service", ex.getLocalizedMessage());
		}
		
		return tags;
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

	private Address getAddress(Location loc) throws Exception{
		Context ctx = this.getBaseContext();
		
		Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
		List<Address> addresses = null;
		try {
			// There is a problem with Geocoder on several android devices. See Google's bugtrack here
			// https://code.google.com/p/android/issues/detail?id=38009
			addresses = geocoder.getFromLocation(loc.getLatitude(), 
									 loc.getLongitude(),
									 1);
		} catch(Exception ex){
			
			Log.e(TAG, ex.getLocalizedMessage());
			
			if (ex instanceof IOException){
				throw ex;
			}else if(ex instanceof IllegalArgumentException){
				String errorString = "Illegal arguments " + 
						Double.toString(loc.getLatitude()) +
						" , " +
						Double.toString(loc.getLongitude()) +
						"passed to address service";
				Log.e(TAG, errorString);
			}
		}
		
		if( addresses != null 
				&& addresses.size() > 0){
			return addresses.get(0);
		}else{
			Toast.makeText(this, "Problem with agruments", Toast.LENGTH_SHORT).show();
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

	public void showLocations(){
		
		gMap.clear();
		
		if( myLocation != null )
			showME(myLocation);
		
		if( reportedLocations != null) {
			for(ReportedPlace place: reportedLocations){
				showReportedPlace(place, place.getTitle(), place.getUserID());
		}}
	}
	
	private Location myLocation = new Location("");
	private ArrayList<ReportedPlace> reportedLocations = new ArrayList<ReportedPlace>();
	
	public void setMyLocation(Location location){
		myLocation = location;
	}
	
	public Location getMyLocation(){
		return myLocation;
	}
	
	public void addReportedLocation(Location location, String title, String userid){
		
		if( reportedLocations != null){
			ReportedPlace place = new ReportedPlace(location);
			place.setTitle(title);
			place.setUserID(userid);
			reportedLocations.add(place);
		}
	}
	
	private void showME(Location location){
		
//		Location tempLoc = new Location("");
//		tempLoc.setLatitude(32.072072072);
//		tempLoc.setLongitude(34.871628036);
		//showMarker(tempLoc, "She is there", "", BitmapDescriptorFactory.HUE_RED);

		showMarker(location, "You are here", "Say something", BitmapDescriptorFactory.HUE_AZURE);
	}
	
	private void showReportedPlace(Location location, String title, String userid){
		showMarker(location, title, "Reported by " + userid, BitmapDescriptorFactory.HUE_ROSE);
	}
	
	private void showMarker(Location location, String title, String snippet,  float color){
		if( location == null ) {
			Log.i(TAG, "Location passed to showMarker() is invalid");
			return;
		}else if( gMap == null ) {
			Log.i(TAG, "Map is not ready when calling to showMarker()");
			return;
		}
	
		final LatLng ME = new LatLng(location.getLatitude(),
									location.getLongitude());
		
		final int zoomLevel = 16;
		// Move the camera instantly to the current location with a zoom.
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, zoomLevel));
		
		// Zoom in, animating the camera.
		gMap.animateCamera(CameraUpdateFactory.zoomIn());
		// Zoom out to specified zoom level, animating with a duration of 2 seconds.
		gMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel), 2000, null);
		
		Marker meMarker = gMap.addMarker(new MarkerOptions()
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
