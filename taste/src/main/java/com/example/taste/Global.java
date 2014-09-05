package com.example.taste;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.pod.taste.R;
import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.EmailIntentSender;

@ReportsCrashes( formKey = "",
			formUri = "http://127.0.0.1:5984/acra-taste/_design/acra-storage/_update/report",
			reportType = org.acra.sender.HttpSender.Type.JSON,
			httpMethod = org.acra.sender.HttpSender.Method.PUT,
			formUriBasicAuthLogin="ppod",
			formUriBasicAuthPassword="ppod",
			resToastText=R.string.crash_toast_text,
			mode = ReportingInteractionMode.TOAST,
					customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.BUILD, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT, ReportField.USER_COMMENT,ReportField.BRAND},
			mailTo = "support@getTaste.co")
public class Global extends Application {
	
	Snap snap = new Snap();
    Tracker t;
    		
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);

		//EmailIntentSender e = new EmailIntentSender
	}
	
	synchronized Tracker getTracker() {
		if (t == null) {
			t = GoogleAnalytics.getInstance(this).newTracker(R.xml.global_tracker);
			GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
		}
		return t;
	}
	
}
