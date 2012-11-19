package com.teamdotly.civicly;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

import com.parse.ParseUser;

import android.app.Application;

public class CiviclyApplication extends Application {

	
	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, Constants.PARSE_KEY, Constants.PARSE_SECRET);
		ParseFacebookUtils.initialize(Constants.FB_KEY);
		ParseTwitterUtils.initialize(Constants.TW_CONSUMER_KEY,Constants.TW_CONSUMER_SECRET);

//		ParseUser.enableAutomaticUser();
//		ParseACL defaultACL = new ParseACL();
//		// Optionally enable public read access.
//		// defaultACL.setPublicReadAccess(true);
//		ParseACL.setDefaultACL(defaultACL, true);
	}

}
