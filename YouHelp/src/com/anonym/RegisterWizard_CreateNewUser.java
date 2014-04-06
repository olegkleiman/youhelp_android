package com.anonym;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.buddy.sdk.AuthenticatedUser;
import com.buddy.sdk.BuddyClient;
import com.buddy.sdk.Callbacks;
import com.buddy.sdk.Callbacks.OnCallback;
import com.buddy.sdk.responses.Response;

public class RegisterWizard_CreateNewUser extends Activity
										implements TextWatcher,
										OnCallback<Response<AuthenticatedUser>>
{

	private static final String TAG = "com.anonym.RegisterWizard_CreateNewUser";
	private ProgressDialog progress;
	private BuddyClient buddyClient;
	String mUserName;
	String mPassword;
	String mEMail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createnewuser);
		
		progress = new ProgressDialog(this);
		
		buddyClient = new BuddyClient("youhelp", "91AE6B5A-FE1B-4E37-936E-559FBBD11186", getApplicationContext());
		buddyClient.ping(this , new OnCallback<Response<String>>(){

			@Override
			public void OnResponse(Response<String> response, Object state) {
				
				RegisterWizard_CreateNewUser activity = (RegisterWizard_CreateNewUser)state;
				
				if( response.isCompleted() ) {
					String res = response.getResult();
					if( res.equalsIgnoreCase("Pong") ) {
						Button createButton = (Button) activity.findViewById(R.id.btnCreateNewUser);
						createButton.setEnabled(true);
					}

				}
				
			}
			
		});

		//QBSettings.getInstance().fastConfigInit("8185", "jPRHvt-amUu-yPQ", "QuXnDQNRN8xrjkx");
		
		EditText txtUserName = (EditText)this.findViewById(R.id.txtUserName);
		txtUserName.addTextChangedListener(this);
		
		EditText txtPassword = (EditText)this.findViewById(R.id.txtPassword);
		txtPassword.addTextChangedListener(this);
		
		EditText txtEMail = (EditText)this.findViewById(R.id.txtEMail);
		txtEMail.addTextChangedListener(this);
	}

	@Override
	public void OnResponse(Response<AuthenticatedUser> response, Object state) {
						
		if( response.isCompleted() ){
					
				AuthenticatedUser user = response.getResult();
				Integer userid = user.getId();
				
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String strUserName = "buddy:" + userid;
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString("prefUsername", strUserName);
				//editor.putString("prefPhoneNumber", "+972-0543307026");
				editor.commit();
				
				Log.i(TAG, "Registration was successful with Buddy. User :" + user.toString() );

				Intent returnIntent = new Intent();
				returnIntent.putExtra("username",strUserName);
				setResult(RESULT_OK, returnIntent);     
				finish();
				
		} else{
						
				String errorMessage = response.getErrorMessage();
				Log.e(TAG, errorMessage);
				
				TextView lblRegistrationStatus = (TextView) this.findViewById(R.id.lblRegistrationStatus);
				lblRegistrationStatus.setText(errorMessage);
				}
		
	}
	
	public void onCreateNewUser(View view) {
		
//		final ProgressDialog ringProgressDialog = ProgressDialog.show(this, 
//				"Please wait ...", 
//				"Creating New User ...", 
//				true);
//		ringProgressDialog.setCancelable(true);
//
//		new Thread(new Runnable() {
//
//			      @Override
//			      public void run() {
//			                try {
//			                    // Here you should write your time consuming task...
//			                    // Let the progress ring for 10 seconds...
//			                    Thread.sleep(10000);
//			                } catch (Exception e) {
//			                }
//			                ringProgressDialog.dismiss();
//			            }
//			      }).start();

		progress.setMessage("Please wait...");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.show();
		
		buddyClient.checkIfUserNameExists(mUserName, this, new Callbacks.OnCallback<Response<Boolean>>(){

			@Override
			public void OnResponse(Response<Boolean> response, Object state) {

				progress.hide();
				
				if( !response.isCompleted() ) {
					//|| !response.getResult() == true ){
					String errorMessage = response.getErrorMessage();
					Log.i(TAG, errorMessage);
				
					RegisterWizard_CreateNewUser activity = (RegisterWizard_CreateNewUser)state;
					TextView lblRegistrationStatus = (TextView) activity.findViewById(R.id.lblRegistrationStatus);
					lblRegistrationStatus.setText(errorMessage);
					
					return;
				}
				else {
					RegisterWizard_CreateNewUser activity = (RegisterWizard_CreateNewUser)state;
					buddyClient.createUser(mUserName, mPassword, activity);
				}
				
			}
			
		});

	}

	@Override
	public void afterTextChanged(Editable s) {
		
		EditText txtUserName = (EditText)this.findViewById(R.id.txtUserName);
		mUserName = txtUserName.getText().toString();
		
		EditText txtPassword = (EditText)this.findViewById(R.id.txtPassword);
		mPassword = txtPassword.getText().toString();
		
		EditText txtEMail = (EditText)this.findViewById(R.id.txtEMail);
		mEMail = txtEMail.getText().toString();
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}


}
