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
	                // Pirates are the best
	            break;
	        case R.id.rbSignFacebook:
	            if (checked)
	                // Ninjas rule
	            break;
	    }
	}
	
	public void onNext(View view) {
		Intent intent = new Intent(this, RegisterWizard_CreateNewUser.class);
		startActivity(intent);
	}
}
