package com.anonym;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<YHMessage> {
	   
	Context context; 
    int layoutResourceId;    
    List<YHMessage> data = null;
	
	public ChatAdapter(Context context, int layoutResourceId, List<YHMessage> data) {
	        super(context, layoutResourceId, data);
	        
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ChatHolder)row.getTag();
        }
        
        YHMessage replica = data.get(position);
        holder.txtTitle.setText(replica.getContent());
        holder.imgIcon.setImageResource(replica.icon);
        
        return row;
    }
	
   static class ChatHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
