package com.example.taste;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;

public class RedeemActivity extends Activity implements Callback<ResponseMessage>{

	Promotion promotion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_redeem);

	    Tracker t = ((Global) this.getApplication()).getTracker();
	    t.setScreenName("Redeem Promotion Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
		Bundle extras = this.getIntent().getExtras(); 
		promotion = (Promotion) extras.getSerializable("promotion");
		
		TextView storeName = (TextView)findViewById(R.id.store_name_redeem);
		storeName.setText(promotion.store_name);

		TextView display_text = (TextView) findViewById(R.id.display_text_redeem);
		display_text.setText(promotion.display_text);
		//String words[] = promotion.display_text.split(" ");
		//display_text.setText(words[0] + " " + words[1]);
//		TextView promotionDescription = (TextView)findViewById(R.id.promotion_description);
//		promotionDescription.setText(promotion.display_text);
		
		TextView expiration = (TextView) findViewById(R.id.expiration_date_redeem);
		expiration.setText(("valid from " + PromotionAdapter.convertDate(promotion.start_date) + " to " + PromotionAdapter.convertDate(promotion.end_date)));
		TextView redeemInstructions = (TextView) findViewById(R.id.redeem_instructions);
		
		Style.toOpenSans(RedeemActivity.this.getApplicationContext(), display_text, "");
		Style.toOpenSans(RedeemActivity.this.getApplicationContext(), storeName, "bold");
		Style.toOpenSans(RedeemActivity.this.getApplicationContext(), expiration, "light");
		Style.toOpenSans(RedeemActivity.this.getApplicationContext(), redeemInstructions, "light");
		//Style.toOpenSans(RedeemActivity.this.getApplicationContext(), promotionDescription, "light");
		
		ImageButton redeemButton = (ImageButton) findViewById(R.id.redeem_button);
    	redeemButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    ((ImageView)v.findViewById(R.id.redeem_button)).setImageResource(R.drawable.redeem_button_pushed);
                else if(event.getAction()==MotionEvent.ACTION_UP)
                    ((ImageView)v.findViewById(R.id.redeem_button)).setImageResource(R.drawable.redeem_button);
                return false;

			}
		});
    	
	}
	
	public void redeemPromotion(View v) {
		
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.getTaste.co").build();
		RestInterface ri = restAdapter.create(RestInterface.class);
		ri.redeemPromotion(promotion, this);
		

	}

	@Override
	public void failure(RetrofitError arg0) {
		// TODO Auto-generated method stub
		Tracker t = ((Global) this.getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Promotion Failed to be Redeemed").build());
		
		
		Log.e("Redeem Failure!", arg0.getMessage().toString());

	    
	}

	@Override
	public void success(ResponseMessage redeemResponse, Response arg1) {

		Tracker t = ((Global) this.getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Promotion Successfully Redeemed").build());
		
		
    	CharSequence text = redeemResponse.getResponseMessage();
		Style.makeToast(RedeemActivity.this, text);
		
	    Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Calling Activity","Redeem");
	    this.startActivity(intent);
	    
	}
}
