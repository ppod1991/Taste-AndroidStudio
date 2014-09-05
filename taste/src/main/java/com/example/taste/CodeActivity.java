package com.example.taste;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.facebook.Session;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CodeActivity extends Activity implements Callback<Promotion> {

	private EditText editText;
	Bundle extras;
	Promotion promotionToAdd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code);
		
	    Tracker t = ((Global) this.getApplication()).getTracker();
	    t.setScreenName("Code Activity Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
	    
		Intent intent = this.getIntent();
		byte[] data = intent.getByteArrayExtra("editedImage");
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,options);
    	ImageView imageView = (ImageView)findViewById(R.id.edited_image_view);
    	//bmp = bmp1.copy(Bitmap.Config.ARGB_8888, true);
    	imageView.setImageBitmap(bmp); 
    	data = null;
    	editText = (EditText)findViewById(R.id.enter_code);
    	editText.requestFocus();
    	extras = this.getIntent().getExtras(); 
    	
    	TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
//				   Log.e("Button Pressed",actionId + "");
//				   Log.e("Event",(event == null) + "");
			   		if ((actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE)) { 
					   //Log.e("Enter detected",actionId + "");	
					   verifyCode(editText);//match this behavior to your 'Send' (or Confirm) button
		   			}
				   return true;
			}
		};
		
		editText.setOnEditorActionListener(enterListener);
		
    	ImageButton verifyButton = (ImageButton) findViewById(R.id.verify_button);
    	verifyButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    ((ImageView)v.findViewById(R.id.verify_button)).setImageResource(R.drawable.gift_button_pushed);
                else if(event.getAction()==MotionEvent.ACTION_UP)
                    ((ImageView)v.findViewById(R.id.verify_button)).setImageResource(R.drawable.gift_button);
                return false;

			}
		});
    	
	}
	
	
	public void verifyCode(View b) {
		String enteredCode = editText.getText().toString();
		editText.setText("");
		Log.e("Entered Code",enteredCode);

    	CharSequence text;
    	
		User user = (User) extras.getSerializable("user");
		Store store = (Store) extras.getSerializable("store");
		
		if (enteredCode.equals(store.store_code)) {
			RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.getTaste.co").build();
			RestInterface ri = restAdapter.create(RestInterface.class);

			promotionToAdd = new Promotion(user.user_id,store.store_id);
			ri.addPromotion(promotionToAdd, this);
			
        	text = "congrats!";
        	Tracker t = ((Global) this.getApplication()).getTracker();
			t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Correct Code Submitted").build());
			
		    
    	}
    	else {
    		text = "oh no! that's not right";
    		Tracker t = ((Global) this.getApplication()).getTracker();
			t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Incorrect Code Submitted").build());
			
    	}
		
		Style.makeToast(this, text);
	}

	@Override
	public void failure(RetrofitError arg0) {
		// TODO Auto-generated method stub
		Log.e("failure to post promotion",":(");
	}

	@Override
	public void success(Promotion myPromotion, Response arg1) {
		// TODO Auto-generated method stub
    	CharSequence text = "Friends get a gift by clicking on your post!";
		Style.makeToast(this, text);
		Snap snap = ((Global)getApplicationContext()).snap;
		snap.setPromotion(myPromotion);
		snap.setAccessToken(Session.getActiveSession().getAccessToken());
		//snap.postSnap();
	    Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Calling Activity","Code");
	    //intent.putExtras(extras);
	    this.startActivity(intent);
	}
}
