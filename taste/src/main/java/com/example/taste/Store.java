package com.example.taste;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Store implements Serializable {

	String store_name;
	String store_id;
	String hashtag_text;
	String hashtag_location;
	String store_code;
	Double store_latitude;
	Double store_longitude;
	
	@Override public String toString() {
		return(store_name);
		
	}
}