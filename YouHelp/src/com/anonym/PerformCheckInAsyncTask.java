package com.anonym;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;

public class PerformCheckInAsyncTask extends AsyncTask<String, String, String> {

	public Activity ParentActivity;
	public PerformCheckInAsyncTask()
	{}
	
    public PerformCheckInAsyncTask(Location currentLocation) {
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onPostExecute(String result) {
       super.onPostExecute(result);
       //Do anything with response..
    }
	
	@Override
	protected String doInBackground(String... uri) {

   	 	HttpClient httpclient = new DefaultHttpClient();
   	 	String responseString = null;
   	 	
   	 	try{
   	 		HttpPost httpGet = new HttpPost(uri[0]);
   	 		HttpContext localContext = new BasicHttpContext();
   	 		
   	 		HttpResponse response = httpclient.execute(httpGet, localContext);
   	 		StatusLine statusLine = response.getStatusLine();
   	 		if(statusLine.getStatusCode() == HttpStatus.SC_OK){
   	 			ByteArrayOutputStream out = new ByteArrayOutputStream();
   	 			response.getEntity().writeTo(out);
   	 			out.close();
   	 			responseString = out.toString();
   	 		}else{
   	 			//Closes the connection.
   	 			response.getEntity().getContent().close();
   	 			throw new IOException(statusLine.getReasonPhrase());
   	 		}

   	 	}
   	 	catch(final Exception ex){
   	 		
   	 		try{
   	 			if( ParentActivity != null) {
   	 			
   	 			ParentActivity.runOnUiThread(new Runnable() {
   	 					public void run() {
   	 						Toast.makeText(ParentActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();
   	 				}});

   	 			}
   	 		}
   	        catch(Exception e){
   	 		
   	        	e.printStackTrace();
   	        }
		}
   	 	
   	 	return responseString;

	}

}
