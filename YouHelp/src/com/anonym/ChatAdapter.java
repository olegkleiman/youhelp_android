package com.anonym;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<YHMessage> {
	   
	Context context; 
    int layoutResourceId;    
    List<YHMessage> data = null;
    String myUSerID;
	
	public ChatAdapter(Context context, int layoutResourceId, List<YHMessage> data, String userID) {
	        super(context, layoutResourceId, data);
	        
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.myUSerID = userID;
        
	}
	
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
            holder.background = (RelativeLayout)row.findViewById(R.id.chatItemRowBackground);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ChatHolder)row.getTag();
        }
        
        YHMessage replica = data.get(position);
        holder.txtView.setText(replica.getContent());
        holder.imgIcon.setImageResource(replica.icon);
        
        if( myUSerID.equals( replica.getUserId()) ) {
        	
        	holder.txtView.setGravity(Gravity.RIGHT);
            holder.background.setGravity(Gravity.RIGHT);
            
        } else {
        
        	holder.txtView.setGravity(Gravity.LEFT);
        	holder.background.setGravity(Gravity.LEFT);
        }
        return row;
    }
	
   static class ChatHolder
    {
        ImageView imgIcon;
        TextView txtView;
        RelativeLayout background;
    }
}
