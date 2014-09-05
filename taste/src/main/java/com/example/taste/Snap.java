package com.example.taste;

import java.io.Serializable;

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

@SuppressWarnings("serial")
public class Snap implements Serializable, Callback<Snap> {

	Promotion promotion;
	Store store;
	User user;
	String access_token;
	String picture_url;
	String facebook_post_id;
	String snap_message;
	
	public Snap() {
	}
	
	public Snap(Promotion myPromotion,Store myStore, User myUser, String myAccessToken, String myPictureURL, String myFacebookPostId, String mySnapMessage) {
		promotion = myPromotion;
		store = myStore;
		user = myUser;
		access_token = myAccessToken;
		picture_url = myPictureURL;
		facebook_post_id = myFacebookPostId;
		snap_message = mySnapMessage;
	}
		
	public void setPromotion(Promotion myPromotion){
		promotion = myPromotion;
	}

	public void setUser(User myUser){
		user = myUser;
	}
	
	public void setStore(Store myStore){
		store = myStore;
		
	}
	
	public void setFacebookPostId(String myFacebookPostId){
		facebook_post_id = myFacebookPostId;
	}
	
	public void setSnapMessage(String mySnapMessage){
		snap_message= mySnapMessage;
	}
	
	public void setAccessToken(String myAccessToken){
		access_token = myAccessToken;
		if (picture_url != null)
			postSnap();
	}
	
	public void setPictureURL(String myPictureURL){
		picture_url = myPictureURL;
		if (access_token != null)
			postSnap();
		
	}
	
	public void postSnap() {

		
		RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint("http://www.getTaste.co").build();
		RestInterface ri = restAdapter.create(RestInterface.class);
		Gson gson = new Gson();
		String json = gson.toJson(this);
		Log.e("JSON Output",json);
		ri.addSnap(new Snap(promotion,store,user,access_token,picture_url,facebook_post_id,snap_message),this);
		
		promotion = null;
		store = null;
		facebook_post_id = null;
		snap_message = null;
		picture_url = null;
		access_token = null;
		
	}

	@Override
	public void failure(RetrofitError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void success(Snap arg0, Response arg1) {
		// TODO Auto-generated method stub
		Log.e("Snap Posted","HELLO");
	}
	
	
}