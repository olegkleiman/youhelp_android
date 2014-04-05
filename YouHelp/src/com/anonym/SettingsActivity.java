package com.anonym;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }
	
   public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            
            String defaulSummary = getString(R.string.pref_emergency_numbers_summary);
            
        	displaySummaryOfPreference("prefUsername", "");
        	displaySummaryOfPreference("prefEmergencyNumber1", defaulSummary);
        	displaySummaryOfPreference("prefEmergencyNumber2", defaulSummary);
        	displaySummaryOfPreference("prefEmergencyNumber3", defaulSummary);

        }
        
        void displaySummaryOfPreference(String preferenceKey, String defaultValue){
        	
        	try{
        		Preference pref = findPreference(preferenceKey);
	        	if( pref != null 
	        			&& pref instanceof EditTextPreference){
	        		EditTextPreference editTextPref = (EditTextPreference) pref;
	        		String value = editTextPref.getText();
	        		if( value == null  )
	        			editTextPref.setSummary(defaultValue);
	        		else
	        			editTextPref.setSummary(value);
	        	}
        	} catch(Exception ex){
        		ex.printStackTrace();
        	}
        }
    }
}
