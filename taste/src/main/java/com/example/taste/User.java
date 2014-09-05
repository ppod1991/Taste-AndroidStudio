package com.example.taste;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	String first_name;
	String last_name;
	String user_id;
	String facebook_id;
	String location_id;
	String email;
	String gender;
	String birthday;
	String fb_url;
	
	@Override public String toString() {
		return(" " + first_name + last_name + user_id + facebook_id + location_id + email);
		
	}
	
}


