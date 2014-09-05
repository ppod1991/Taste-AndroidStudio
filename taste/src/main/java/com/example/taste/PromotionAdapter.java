package com.example.taste;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pod.taste.R;

public class PromotionAdapter extends ArrayAdapter<Promotion> {

	Promotion[] promotions;
	public PromotionAdapter(Context context, int resource, Promotion[] objects) {
		super(context, R.layout.promotion_row, R.id.display_text, objects);
		// TODO Auto-generated constructor stub
		promotions = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = super.getView(position, convertView, parent);
		TextView storeName = (TextView) row.findViewById(R.id.store_name);
		storeName.setText(promotions[position].store_name);
		
//		TextView display_text = (TextView) row.findViewById(R.id.display_text);
//		display_text.setText(promotions[position].display_text);
		TextView expiration = (TextView) row.findViewById(R.id.expiration_dates);
		TextView displayText = (TextView) row.findViewById(R.id.display_text);
		
		expiration.setText(("valid " + convertDate(promotions[position].start_date) + " to " + convertDate(promotions[position].end_date)));

	    
	    Style.toOpenSans(getContext(), displayText, "light");
	    Style.toOpenSans(getContext(), expiration, "light");
	    Style.toOpenSans(getContext(), storeName, "bold");
		return(row);
	}
	
	public static String convertDate(String rawData) {
	    TimeZone UTC = TimeZone.getTimeZone("UTC");
		//Log.e("rawDate",rawData);
		
	    SimpleDateFormat dfUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US);
	    dfUTC.setTimeZone(UTC);
	    
	    SimpleDateFormat dfLocal = new SimpleDateFormat("M/d/yy",Locale.US);
	    dfLocal.setTimeZone(TimeZone.getDefault());
	    
	    try {
			Date stringDate = dfUTC.parse(rawData);
			String nowAsCurrentTimezone = dfLocal.format(stringDate);
			Log.e("Time:", nowAsCurrentTimezone);
			return nowAsCurrentTimezone;
//			String month = nowAsCurrentTimezone.substring(5, 7);
//			if (!(month.equals("10") || month.equals("11") || month.equals("12")))
//				month = month.substring(1,2);
//			
//			String day = nowAsCurrentTimezone.substring(8,10);
//		    String year = nowAsCurrentTimezone.substring(2,4);
//		    return (month+"/" + day + "/" + year);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e("Date parsing error!",e.toString());
			e.printStackTrace();
		}
	    
//	    Calendar cal = Calendar.getInstance();
//	    TimeZone tz = cal.getTimeZone();
//	    df.setTimeZone(tz);
//	    String nowAsCurrentTimezone = df.format(rawData);
//	    Log.e("Date:",nowAsCurrentTimezone);
	    return "";

	}
	
}
