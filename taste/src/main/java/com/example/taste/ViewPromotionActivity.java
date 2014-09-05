package com.example.taste;

import java.util.HashMap;
import java.util.Map;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;
import retrofit.http.QueryMap;

public class ViewPromotionActivity extends Activity implements ListView.OnItemClickListener, Callback<Promotions> {
	
	Promotion[] promotions;
	ListView promotionsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_promotion);
		
	    Tracker t = ((Global) this.getApplication()).getTracker();
	    t.setScreenName("View Promotion Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
		Bundle extras = this.getIntent().getExtras(); 
		User user = (User) extras.getSerializable("user");
		Store store = (Store) extras.getSerializable("store");
		
    	promotionsList = (ListView)findViewById(R.id.promotionView);
    	promotionsList.setOnItemClickListener(this);
    	
		Log.e("User_id",user.user_id);
		Log.e("Store_id",store.store_id);
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.getTaste.co").build();
		RestInterface ri = restAdapter.create(RestInterface.class);
		Map<String,String> options = new HashMap<String,String>();
		options.put("store_id", store.store_id);
		options.put("user_id", user.user_id);
		options.put("use_status", "not used");
		ri.getPromotions(options, this);
		
	}

	@Override
	public void failure(RetrofitError arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void success(Promotions myPromotions, retrofit.client.Response response) {
		// TODO Auto-generated method stub

    	promotions= myPromotions.Promotions.toArray(new Promotion[myPromotions.Promotions.size()]);
    	PromotionAdapter aa = new PromotionAdapter(this,android.R.layout.simple_list_item_1,promotions);
    	//aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	promotionsList.setAdapter(aa);
    	
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
	    Intent intent = new Intent(this, RedeemActivity.class);
	    intent.putExtra("promotion",promotions[position]);
	    this.startActivity(intent);
	    
		
	}


	



}
