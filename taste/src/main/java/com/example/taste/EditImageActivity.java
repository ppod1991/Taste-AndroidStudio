package com.example.taste;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import org.json.JSONException;
import org.json.JSONObject;
import com.facebook.*;
import com.facebook.model.*;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;


public class EditImageActivity extends Activity implements AdapterView.OnItemSelectedListener, Callback<Stores>{

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private Store[] stores;
	private byte[] data;
	private Bitmap bmp;
	private Bitmap origBmp;
	private Store storeSelection;
	private ImageView imageView;
	private Spinner storeSpinner;
	private ArrayAdapter<Store> aa;
	static final int VERIFY_CODE = 1;
	RequestAsyncTask task;
	private User user;
	private Snap snap;
	private ImageButton shareButton;
	private ProgressBar progressBar;
	private Session.StatusCallback cb;
	private EditText messageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    Tracker t = ((Global) this.getApplication()).getTracker();
	    t.setScreenName("Edit Image Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
	    
		snap = ((Global)getApplicationContext()).snap;
		setContentView(R.layout.activity_edit_image);
		Intent intent = this.getIntent();
	    //data = intent.getByteArrayExtra("Image");
		data = intent.getByteArrayExtra("Image");
		user = (User) intent.getSerializableExtra("user");
		//data = CameraActivity.data;
		//CameraActivity.data = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        origBmp = BitmapFactory.decodeByteArray(data, 0, data.length,options);
        
    	imageView = (ImageView)findViewById(R.id.image_view);
    	//bmp = bmp1.copy(Bitmap.Config.ARGB_8888, true);
    	imageView.setImageBitmap(origBmp); 
    	
    	progressBar = (ProgressBar) (this.findViewById(R.id.progress_bar_edit));
    	progressBar.bringToFront();
    	
    	Style.toOpenSans(getApplicationContext(), findViewById(R.id.facebook_message), "light");
    	Style.toOpenSans(getApplicationContext(), findViewById(R.id.choose_store_instructions), "light");
    	
    	messageView = (EditText) findViewById(R.id.facebook_message);
    	messageView.setOnEditorActionListener(new OnEditorActionListener() {
    		
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//                   userValidateEntry();
                   // Must return true here to consume event
                   return true;

                }
                return false;
			}
        });
    	
    	
    	shareButton = (ImageButton) findViewById(R.id.share_button);
    	shareButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    ((ImageView)v.findViewById(R.id.share_button)).setImageResource(R.drawable.facebook_share_pushed);
                else if(event.getAction()==MotionEvent.ACTION_UP)
                    ((ImageView)v.findViewById(R.id.share_button)).setImageResource(R.drawable.facebook_share);
                return false;

			}
		});
    	
		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.getTaste.co").build();
		RestInterface ri = restAdapter.create(RestInterface.class);
		ri.getStores(this);
    	
	}

	@Override
	public void onResume() {
		super.onResume();
		progressBar.setVisibility(View.INVISIBLE);
		shareButton.setEnabled(true);
		
	}
	
	public void shareImage(View shareButton){
		
		Tracker t = ((Global) this.getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder().setCategory("Progress").setAction("Button Click").setLabel("Share Image to Facebook").build());
		
		
		progressBar.setVisibility(View.VISIBLE);
		shareButton.setEnabled(false);
		
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	//bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
    	//data = baos.toByteArray();
    	//bmp.recycle();
    	
//        try {
//			baos.close();
//			baos = null;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
		final Session session = Session.getActiveSession();
	    
        // Check for publish permissions    
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
            
            cb = new Session.StatusCallback() {
            	@Override
            	public void call(Session session, SessionState state, Exception exception) {
            		shareCallback();

            		if (exception != null) {
            			Log.e("Add new permission Exception", exception.toString());
            		}
            	}  
            };
            
            newPermissionsRequest.setCallback(cb);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
        else {
        	shareCallback();
        }
        
        //shareCallback();
	}
	
	public void shareCallback() {
		
		Session session = Session.getActiveSession();
		
		if (cb != null)
    		session.removeCallback(cb);
		
        //Bundle postParams = new Bundle();
        String facebookMessage = messageView.getText().toString();
        //postParams.putString("message", facebookMessage);
        //postParams.putByteArray("picture", data);
        snap.setSnapMessage(facebookMessage);
        
        //postParams.putString("fb:explicitly_shared", "true");
        Request.Callback callback= new Request.Callback() {
            public void onCompleted(com.facebook.Response response) {
                Log.i("Facebook picture post response:",response.toString());
                //Log.i("Facebook picture response error:",response.getError());
            	if (response.getError() == null) {
            		
	                try {
	                	JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
	                	String postId = null;
	 
	                    postId = graphResponse.getString("uri");
	                    Log.e("Facebook Staging URI: ", postId);
	                    snap.setPictureURL(postId);
	                    //Log.e("Facebook Post Id: ", postId);
	                    snap.setFacebookPostId(postId);
	                    /* make the API call */
	                    //Retrieve the FB CDN Url
//	                    new Request(Session.getActiveSession(),"/"+postId,null,HttpMethod.GET,new Request.Callback() {
//	                            public void onCompleted(com.facebook.Response URLresponse) {
//	                                JSONObject URLgraphResponse = URLresponse.getGraphObject().getInnerJSONObject();
//	                                Log.e("URL Graph Reponse",URLgraphResponse.toString());
//	                                 
//	                                try {
//	                                	String fb_url = URLgraphResponse.getJSONArray("images").getJSONObject(0).getString("source");
//										Log.e("URL:",fb_url);
//										snap.setPictureURL(fb_url);
//	//									Bundle actionParams = new Bundle();
//	//									actionParams.putString("eatery", "http://www.getTaste.co/moltobene");
//	////									actionParams.putString("image[0][url]", fb_url);
//	////									actionParams.putBoolean("image[0][user_generated]",true);
//	////									actionParams.putString("url", fb_url);
//	////									actionParams.putBoolean("user_generated",true);
//	////									actionParams.putBoolean("fb:explicitly_shared", true);
//	//									Request actionRequest = new Request(Session.getActiveSession(),"me/tasteapplication:experience",actionParams,HttpMethod.POST);
//	//									com.facebook.Response actionResponse = actionRequest.executeAndWait();
//	//									Log.e("Action Response:", actionResponse.toString());	
//	//									Log.e("Action error:", actionResponse.getError().getCategory().toString());
//									} catch (JSONException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//	                                		
//	                            }
//	                        }
//	                    ).executeAsync();
	                    
	                    
	                } catch (JSONException e) {
	                    Log.i("LOG",
	                        "JSON error "+ e.getMessage());
	                    

	                	CharSequence text = "Could not post picture! Please check your internet connection";
	            		Style.makeToast(EditImageActivity.this, text);
	            		
	                }
                catch (NullPointerException e) {
                    Log.i("LOG",
                        "JSON error "+ e.getMessage());
                    

                	CharSequence text = "Could not post picture! Please check your internet connection";
            		Style.makeToast(EditImageActivity.this, text);
            		
                }
	                FacebookRequestError error = response.getError();
	                Log.i("ERROR",error + "");
            	}
            	else {
                	CharSequence text = "Could not post picture! Please check your internet connection";
            		Style.makeToast(EditImageActivity.this, text);
            	}
            }
        };
        Request request = Request.newUploadStagingResourceWithImageRequest(session, bmp, callback);
//        Request request = new Request(session, "me/photos", postParams, 
//                              HttpMethod.POST, callback);

//        Request request = new Request(session, "me/tasteapplication:experience", postParams, 
//                HttpMethod.POST, callback);
        
        task = new RequestAsyncTask(request);
        task.execute();
        startCodeActivity(task);
	}
	
	@Override
	  public void failure(RetrofitError exception) {
	    Log.e(getClass().getSimpleName(),
	          "Exception from Retrofit request to StackOverflow", exception);
	  }
	
	  @Override
	  public void success(Stores myStores, retrofit.client.Response response) {
	    	storeSpinner = (Spinner)findViewById(R.id.storeList);
	    	storeSpinner.setOnItemSelectedListener(this);
	    	stores= myStores.Stores.toArray(new Store[myStores.Stores.size()]);
	    	aa = new ArrayAdapter<Store>(this,R.layout.spinner_item,stores);
	    	aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	storeSpinner.setAdapter(aa);
		  //welcome.setText("Hello " + users.Users.get(0) + "!");
	  }
	  
	public void startCodeActivity(RequestAsyncTask task)
	  {
		    Intent intent = new Intent(this, CodeActivity.class);

	        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/2, bmp.getHeight()/2, false);
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	bmp.compress(Bitmap.CompressFormat.JPEG,100, baos);
	    	//bmp.recycle();
	        intent.putExtra("editedImage", baos.toByteArray());
	        intent.putExtra("user", user);
	        intent.putExtra("store", storeSelection);
	        try {
				baos.close();
				baos = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    this.startActivity(intent);
	  }
	  
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
		 super.onActivityResult(requestCode, resultCode, data);
		 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	public void editImage() throws IOException{
		bmp = origBmp.copy(Bitmap.Config.ARGB_8888, true);
		imageView.setImageBitmap(bmp);
		
    	// Use 'Canvas' to draw text onto 'Bitmap'
    	Canvas cv = new Canvas(bmp);

    	// Prepare 'Paint' for text drawing
    	Paint mPaint = new Paint();
    	
    	//
//    	mPaint.setColor( Color.WHITE);
//    	//mPaint.setARGB(255, 0, 0, 0);
//    	mPaint.setTextAlign(Paint.Align.CENTER);
//    	mPaint.setTextSize((int)(0.03*bmp.getHeight()));
//    	mPaint.setTypeface(Typeface.SANS_SERIF);
//    	mPaint.setStyle(Paint.Style.STROKE);
//    	mPaint.setStrokeWidth(1);

    	
        Paint textPaint = new Paint();
        textPaint.setColor( Color.WHITE );
        //textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize((int)(0.025*bmp.getHeight()));
        textPaint.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf"));
        
        String text = "#" + storeSelection.hashtag_text + " #" + storeSelection.hashtag_location;
        //cv.drawText(text, (int) (.5*bmp.getWidth()),(int) (0.95 * bmp.getHeight()) , mPaint);
        cv.drawText(text, (int) (.5*bmp.getWidth()),(int) (0.95 * bmp.getHeight()) , textPaint);
     // Reset the stream of 'output' for output writing.
    	
    	//bmp.recycle();
	}
	

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
	    for (String string : subset) {
	        if (!superset.contains(string)) {
	            return false;
	        }
	    }
	    return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		storeSelection = stores[position];
		snap.setStore(stores[position]);
		Tracker t = ((Global) this.getApplication()).getTracker();
		t.send(new HitBuilders.EventBuilder().setCategory("List Selection").setAction("List Selection").setLabel("Store Chosen from Drop-Down").build());
		
		
		try {
			editImage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	  
}


