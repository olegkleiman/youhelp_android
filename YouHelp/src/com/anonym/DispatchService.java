package com.anonym;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;



public class DispatchService extends Service {

	class DispatchBinder extends Binder {
		DispatchService dispatchservice;
		
		DispatchBinder(DispatchService service){
			this.dispatchservice = service;
		}
		
		public DispatchService getDispatchService() {
			return dispatchservice;
		}

	}
	
	public void setMainActivity(MainActivity activity){
		
	}
	
	public void setChatActivity(ChatRoomActivity activity){
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		DispatchBinder binder = new DispatchBinder(this);
		
		return binder;
	}

}
