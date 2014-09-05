package com.example.taste;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.*;
import com.pod.taste.R;


public class MainActivityOld extends Activity implements ListView.OnItemClickListener, Callback<User>{

	private User user;
	private Promotion[] promotions;
	private RestInterface ui;
	ListView promotionsList;
	private ProgressBar progressBar2;
	private ImageButton toCameraButton;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main_old);  
		
		toCameraButton = (ImageButton) findViewById(R.id.toCamera_button);
		openFacebookSession(toCameraButton);
		promotionsList = (ListView)findViewById(R.id.allPromotionsList);
		promotionsList.setOnItemClickListener(this);

		Style.toOpenSans(getApplicationContext(), findViewById(R.id.welcome_message), "light");
		
    	toCameraButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    ((ImageView)v.findViewById(R.id.toCamera_button)).setImageResource(R.drawable.camera_button_main_pushed);
                else if(event.getAction()==MotionEvent.ACTION_UP)
                    ((ImageView)v.findViewById(R.id.toCamera_button)).setImageResource(R.drawable.camera_button_main);
                return false;

			}
		});
		
    	progressBar2 = (ProgressBar) findViewById(R.id.progress_bar_main_2);
    	
//
	    
	}

	public void onResume() {
		super.onResume();
		progressBar2.setVisibility(View.INVISIBLE);
		toCameraButton.setEnabled(true);
	}
	
	
	  public void openFacebookSession(View button) {
		
		  // start Facebook Login
		    Session.openActiveSession(this, true, new Session.StatusCallback() {

		      // callback when session changes state
		      @Override
		      public void call(Session session, SessionState state, Exception exception) {
		    	  
		    	  if (session.isOpened()) {
		    		  Log.e("Access Token",session.getAccessToken().toString());
		    		  Request.newMeRequest(session, new Request.GraphUserCallback() {
						
						@Override
						public void onCompleted(GraphUser user, com.facebook.Response response) {
							if (user != null) 
							{
								TextView welcome = (TextView) findViewById(R.id.welcome_message);
								welcome.setText("Gifts for " + user.getFirstName() + ":");
								RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.getTaste.co").build();
								ui = restAdapter.create(RestInterface.class);
								Map<String,String> user_options = new HashMap<String,String>();
								User curUser = new User();
								curUser.birthday = user.getBirthday();
								Object email = user.asMap().get("email");
								if (email != null) 
									curUser.email = email.toString();
								Object gender = user.asMap().get("gender");
								if (gender != null)
									curUser.gender = gender.toString();
								
								Object loc = user.asMap().get("hometown");
								if(loc != null)
									curUser.location_id = loc.toString();
								
								curUser.first_name = user.getFirstName();
								curUser.last_name = user.getLastName();
								curUser.facebook_id = user.getId();
								curUser.fb_url = user.getLink();
								
								ui.addUser(curUser,MainActivityOld.this);

								//startCameraActivity();
							}
						}


					}).executeAsync();
		    	  }
		      }
		    });
	  }
	  
	  @Override
	  public void failure(RetrofitError exception) {
	    Log.e(getClass().getSimpleName(),
	          "Exception from Retrofit request to StackOverflow", exception);
	  }

	  @Override
	  public void success(User myUser, retrofit.client.Response response) {
//		  TextView welcome = (TextView) findViewById(R.id.welcome_message);
//		  welcome.setText("Hello " + users.Users.get(0) + "!");
//		  List<User> returnedUsers = users.Users;
//		  if (returnedUsers.size() == 0) {
//			  //Add new user
//			  
//		  }
		  Log.e("retro fit add user response", response.toString());
		  Log.e("returned user:", myUser.toString());
		  
		  user = myUser;
		  ((Global)getApplicationContext()).snap.setUser(user);
		  Map<String,String> promotion_options = new HashMap<String,String>();
		  promotion_options.put("user_id", user.user_id);
		  promotion_options.put("use_status", "not used");
		  ui.getPromotions(promotion_options, new promotionCallback());
	  }
	  
	  private class promotionCallback implements Callback<Promotions> {

		@Override
		public void failure(RetrofitError arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void success(Promotions myPromotions, Response arg1) {
			// TODO Auto-generated method stub
			  
			  ProgressBar progressBar = (ProgressBar) ((MainActivityOld.this).findViewById(R.id.progress_bar_main));
			  progressBar.setVisibility(View.INVISIBLE);
			  
			  if (myPromotions.Promotions.size() == 0){
				  ImageView welcome = (ImageView) MainActivityOld.this.findViewById(R.id.welcome_to_taste);
				  welcome.setVisibility(View.VISIBLE);
				  ImageView tapToStart = (ImageView) MainActivityOld.this.findViewById(R.id.tap_to_start);
				  tapToStart.setVisibility(View.VISIBLE);
				  promotionsList.setVisibility(View.INVISIBLE);
				  TextView welcomeMessage = (TextView) findViewById(R.id.welcome_message);
				  welcomeMessage.setVisibility(View.INVISIBLE);
			  }
			  
		      promotions= myPromotions.Promotions.toArray(new Promotion[myPromotions.Promotions.size()]);
		      PromotionAdapter aa = new PromotionAdapter(MainActivityOld.this,android.R.layout.simple_list_item_1,promotions);
		      //aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		      promotionsList.setAdapter(aa);
		}
		
		
		
	   
	  }
	 
	  public void startCameraActivity(View button)
	  {
		  	toCameraButton.setEnabled(false);
		  	progressBar2.setVisibility(View.VISIBLE);
		    Intent intent = new Intent(this, CameraActivity.class);
		    intent.putExtra("user", user);
		    startActivity(intent);
	  }
 
	  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }

	
  @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
	    Log.e("Item Selected Reached",promotions[position] + "");
		Intent intent = new Intent(this, RedeemActivity.class);
	    
	    intent.putExtra("promotion",promotions[position]);
	    startActivity(intent);
	    
		
	}

		
	            	
}
