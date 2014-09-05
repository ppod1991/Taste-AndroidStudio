package com.example.taste;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.splash, 
	            container, false);
	    Tracker t = ((Global) getActivity().getApplication()).getTracker();
	    t.setScreenName("Splash Screen View");
	    t.send(new HitBuilders.AppViewBuilder().build());
	    
	    return view;
	}
	
}
