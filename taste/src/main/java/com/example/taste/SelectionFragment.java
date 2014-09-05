package com.example.taste;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SelectionFragment extends Fragment implements ListView.OnItemClickListener, Callback<User> {

	private static final String TAG = "SelectionFragment";
	private User user;
	private Promotion[] promotions;
	private RestInterface ui;
	ListView promotionsList;
	private ProgressBar progressBar2;
	private ImageButton toCameraButton;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	    

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
		
	    super.onCreateView(inflater, container, savedInstanceState);
	    View view = inflater.inflate(R.layout.selection, container, false);
	    
		toCameraButton = (ImageButton) view.findViewById(R.id.toCamera_button);
		promotionsList = (ListView) view.findViewById(R.id.allPromotionsList);
		promotionsList.setOnItemClickListener(this);

		Style.toOpenSans(this.getActivity().getApplicationContext(), view.findViewById(R.id.welcome_message), "light");
		
	    Tracker t = ((Global) getActivity().getApplication()).getTracker();
	    t.setScreenName("Main Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
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
		
    	toCameraButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v){
    			Tracker t = ((Global) getActivity().getApplication()).getTracker();
    			t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Button To Camera Clicked").build());
    			startCameraActivity();
    		}
    	});
    	
    	progressBar2 = (ProgressBar) view.findViewById(R.id.progress_bar_main_2);
    	
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
        
    	
	    return view;
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
		  
		  Tracker t = ((Global) getActivity().getApplication()).getTracker();
		  t.set("&uid", user.user_id);
		  
		  ((Global)getActivity().getApplicationContext()).snap.setUser(user);
		  Map<String,String> promotion_options = new HashMap<String,String>();
		  promotion_options.put("user_id", user.user_id);
		  promotion_options.put("use_status", "not used");
		  ui.getPromotions(promotion_options, new promotionCallback());
	  }
	  
		private void makeMeRequest(final Session session) {
			 if (session.isOpened()) {
	    		  Log.e("Access Token",session.getAccessToken().toString());
	    		  Request.newMeRequest(session, new Request.GraphUserCallback() {
					
					@Override
					public void onCompleted(GraphUser user, com.facebook.Response response) {
						if (user != null) 
						{
							TextView welcome = (TextView) getView().findViewById(R.id.welcome_message);
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
							
							ui.addUser(curUser,SelectionFragment.this);
	
							//startCameraActivity();
						}
					}
	
	
				}).executeAsync();
	    	  }
		} 
		
		private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		    if (session != null && session.isOpened()) {
		        // Get the user's data.
		        makeMeRequest(session);
		    }
		}
		
	  private class promotionCallback implements Callback<Promotions> {

		@Override
		public void failure(RetrofitError arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void success(Promotions myPromotions, Response arg1) {
			// TODO Auto-generated method stub
			  
			  ProgressBar progressBar = (ProgressBar) (SelectionFragment.this.getView().findViewById(R.id.progress_bar_main));
			  progressBar.setVisibility(View.INVISIBLE);
			  
			  if (myPromotions.Promotions.size() == 0){
				  ImageView welcome = (ImageView) SelectionFragment.this.getView().findViewById(R.id.welcome_to_taste);
				  welcome.setVisibility(View.VISIBLE);
				  ImageView tapToStart = (ImageView) SelectionFragment.this.getView().findViewById(R.id.tap_to_start);
				  tapToStart.setVisibility(View.VISIBLE);
				  promotionsList.setVisibility(View.INVISIBLE);
				  TextView welcomeMessage = (TextView) SelectionFragment.this.getView().findViewById(R.id.welcome_message);
				  welcomeMessage.setVisibility(View.INVISIBLE);
			  }
			  
		      promotions= myPromotions.Promotions.toArray(new Promotion[myPromotions.Promotions.size()]);
		      PromotionAdapter aa = new PromotionAdapter(SelectionFragment.this.getActivity(),android.R.layout.simple_list_item_1,promotions);
		      //aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		      promotionsList.setAdapter(aa);
		}
		
		
		
	   
	  }
	 
	  public void startCameraActivity()
	  {
		  	toCameraButton.setEnabled(false);
		  	progressBar2.setVisibility(View.VISIBLE);
		    Intent intent = new Intent(getActivity(), CameraActivity.class);
		    intent.putExtra("user", user);
		    startActivity(intent);
	  }
	  
	  @Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
		  	Tracker t = ((Global) getActivity().getApplication()).getTracker();
			t.send(new HitBuilders.EventBuilder().setCategory("List Selection").setAction("List Selection").setLabel("Promotion Selected").build());
			
		    Log.e("Item Selected Reached",promotions[position] + "");
			Intent intent = new Intent(getActivity(), RedeemActivity.class);
		    
		    intent.putExtra("promotion",promotions[position]);
		    startActivity(intent);
		    
			
		}
	
	  @Override
	  public void onSaveInstanceState(Bundle bundle) {
	      super.onSaveInstanceState(bundle);
	      uiHelper.onSaveInstanceState(bundle);
	  }
	
	  @Override
	  public void onPause() {
	      super.onPause();
	      uiHelper.onPause();
	  }
	
	  @Override
	  public void onDestroy() {
	      super.onDestroy();
	      uiHelper.onDestroy();
	  }
	  
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		progressBar2.setVisibility(View.INVISIBLE);
		toCameraButton.setEnabled(true);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	
}
