package com.anonym;

import java.util.Date;

public class YHMessage {

	private long id;
	private String Content;
	private String UserID;
	private Date DateCreated;
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public String getContent(){
		return Content;
	}
	
	public void setContent(String content){
		this.Content = content;
	}
	
	public String getUserId(){
		return UserID;
	}
	
	public void setUserID(String userID){
		this.UserID = userID;
	}
	
	public Date getDateCreated(){
		return DateCreated;
	}
	
	public void setDateCreated(Date date){
		DateCreated = date;
	}
	
	@Override
	public String toString(){
		return UserID + Content;
	}
	
}
