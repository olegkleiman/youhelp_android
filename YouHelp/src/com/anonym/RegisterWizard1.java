package com.anonym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class RegisterWizard1 extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register1);
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.rbCreateUser:
	            if (checked)
	                // 
	            break;
	        case R.id.rbSignFacebook:
	            if (checked)
	                // 
	            break;
	    }
	}
	
	public void onNext(View view) {
		Intent intent = new Intent(this, RegisterWizard_CreateNewUser.class);
		startActivityForResult(intent, 1);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			
		    if(resultCode == RESULT_OK){      
		         String username = data.getStringExtra("username");  
		         
		         Intent returnIntent = new Intent();
		         returnIntent.putExtra("username", username);
		         setResult(RESULT_OK, returnIntent); 
		         
		         finish();
		     }
		     if (resultCode == RESULT_CANCELED) {    
		         //Write your code if there's no result
		     }

		}
	}
}
