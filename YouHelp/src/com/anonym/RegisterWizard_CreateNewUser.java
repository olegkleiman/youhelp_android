package com.anonym;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
//import com.quickblox.core.
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.users.result.QBUserResult;

public class RegisterWizard_CreateNewUser extends Activity {

	private static final String TAG = "com.anonym.RegisterWizard_CreateNewUser";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createnewuser);
	}
	
	
	public void onCreateNewUser(View view) {
		
		QBSettings.getInstance().fastConfigInit("8185", "jPRHvt-amUu-yPQ", "QuXnDQNRN8xrjkx");
		
		QBAuth.createSession( new QBCallbackImpl(){
			
			@Override
			public void onComplete(Result result){
				if( result.isSuccess()) {
					
					final QBUser user = new QBUser("nuser", "nuser123456");
					user.setEmail("nuser@hotmail.com");
					
					QBUsers.signUp(user, new QBCallbackImpl(){
						
						@Override
						public void onComplete(Result res){
							
							if( res.isSuccess()) {
							
								QBUserResult qbUserResult = (QBUserResult) res;
								Log.i(TAG, "Registration was successful" + qbUserResult.getUser().toString());
								
							}
							else{
								
								Log.e(TAG, res.getErrors().toString());

							}
						}
					});
				}
			}
		});
	}
}
