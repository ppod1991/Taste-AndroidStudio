package com.example.taste;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Promotion implements Serializable {

	String promotion_id;
	String store_id;
	String start_date;
	String end_date;
	String display_text;
	String user_id;
	String store_name;
	
	@Override public String toString() {
		return(display_text);
		
	}
	
	public Promotion(String myUserId, String myStoreId) {
		user_id = myUserId;
		store_id = myStoreId;
	}
}