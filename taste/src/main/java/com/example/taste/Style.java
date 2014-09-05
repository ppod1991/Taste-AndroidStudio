package com.example.taste;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.pod.taste.R;

public class Style {

	public static void makeToast(Activity a, CharSequence myText) {
		LayoutInflater inflater = a.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) a.findViewById(R.id.toast_layout_root));

		TextView text = (TextView) layout.findViewById(R.id.toast_text);
		text.setText(myText);
		
		toOpenSans(a,text,"light");
		int duration = 3500;
		Toast toast = new Toast(a.getApplicationContext());
		toast.setDuration(duration);
		toast.setView(layout);
		toast.show();
	}
	
	public static void toOpenSans(Context a, View v, String style)
	{
		Typeface typeface;
		
		if(style.equals("light"))
			typeface = Typeface.createFromAsset(a.getAssets(), "fonts/OpenSans-Light.ttf");
		else if (style.equals("bold"))
			typeface = Typeface.createFromAsset(a.getAssets(), "fonts/OpenSans-Semibold.ttf");
		else
			typeface = Typeface.createFromAsset(a.getAssets(), "fonts/OpenSans-Regular.ttf");
	    
	    ((TextView) v).setTypeface(typeface);
	}
}
