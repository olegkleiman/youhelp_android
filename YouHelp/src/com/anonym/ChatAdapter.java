package com.anonym;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<YHMessage> {
	   
	Context context; 
    int layoutResourceId;    
    List<YHMessage> data = new ArrayList<YHMessage>();
    String myUSerID;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public ChatAdapter(Context context, int layoutResourceId, List<YHMessage> data, String userID) {
	        super(context, layoutResourceId, data);
	        
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.myUSerID = userID;
        
	}
	
	@Override
	public void add(YHMessage message) {
		//data.add(message);
		super.add(message); // this call triggers the re-binding of adapter (including getView() invocations )
	}
	
	public int getCount() {
		return this.data.size();
	}
	
	public YHMessage getItem(int index) {
		return this.data.get(index);
	}
	
	//
	// There is absolutely no guarantee on the order in which getView() will be called 
	// nor how many times.
	// 
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
		View row = convertView;
        ChatHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ChatHolder();
            
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtView = (TextView)row.findViewById(R.id.txtTitle);
            holder.background = (LinearLayout)row.findViewById(R.id.chatItemRowBackground);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ChatHolder)row.getTag();
        }
        
        YHMessage replica = this.getItem(position);
        Date dateCreated = replica.getDateCreated();
        
		//String strDate = dateFormat.format(dateCreated);
        //String strText = replica.getContent() + "\n" + strDate;
        // !!!
        holder.txtView.setText(replica.getContent());
        holder.imgIcon.setImageResource(replica.icon);
        
        if( //!replica.getUserId().isEmpty()  &&
        		myUSerID.equals( replica.getUserId()) ) {
        	
        	holder.txtView.setGravity(Gravity.RIGHT);
        	holder.txtView.setBackgroundResource(R.drawable.bubble_green);
            holder.background.setGravity(Gravity.RIGHT);
            
        } else {
        
        	holder.txtView.setGravity(Gravity.LEFT);
        	holder.txtView.setBackgroundResource(R.drawable.bubble_yellow);
        	holder.background.setGravity(Gravity.LEFT);
        }
        
        return row;
    }
	
   static class ChatHolder
    {
        ImageView imgIcon;
        TextView txtView;
        LinearLayout background;
    }
}
