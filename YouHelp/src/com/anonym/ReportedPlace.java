package com.anonym;

import android.location.Location;

public class ReportedPlace extends Location {

	private String Snippet;
	
	public void setSnippet(String title){
		Snippet = title;
	}
	public String getSnippet(){
		return Snippet;
	}
	
	public ReportedPlace(Location location) {
		
		super(location);
	}

}
