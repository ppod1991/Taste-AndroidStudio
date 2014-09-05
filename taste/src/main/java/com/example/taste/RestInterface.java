package com.example.taste;



import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

public interface RestInterface {
	
	@GET("/users")
	void getUsers(@QueryMap Map<String, String> options, Callback<Users> cb);
	
	@POST("/users/android")
	void addUser(@Body User user, Callback<User> cb);

	@GET("/stores")
	void getStores(Callback<Stores> cb);
	
	
	@GET("/promotions")
	void getPromotions(@QueryMap Map<String, String> options, Callback<Promotions> cb );

	@POST("/promotions")
	void addPromotion(@Body Promotion promotion, Callback<Promotion> cb);
	
	@POST("/promotions/redeem")
	void redeemPromotion(@Body Promotion promotion, Callback<ResponseMessage> cb);
	
	@POST("/snaps")
	void addSnap(@Body Snap snap, Callback<Snap> cb);
}
