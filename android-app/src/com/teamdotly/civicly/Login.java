package com.teamdotly.civicly;


import java.util.Arrays;

import com.teamdotly.civicly.R;
import com.parse.LogInCallback;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


//*************************************************************
// Login
//*************************************************************
public class Login extends Activity {
	
	final Activity Login = this;
	
	//**********************************************
	// onCreate
	//**********************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			Intent i = new Intent(Login.this, NewsFeed.class);
    			startActivity(i);
		}
      	setContentView(R.layout.main);
        ((Button)findViewById(R.id.LoginButton)).setOnClickListener( loginButtonListener );
        ((Button)findViewById(R.id.TwitterLogin)).setOnClickListener( loginTwitterListener );
    	}
    
 	// Login with twitter 
	private OnClickListener loginTwitterListener = new OnClickListener() {
		public void onClick( View v ) {
			Log.d("check","twitter");
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				if (!ParseTwitterUtils.isLinked(currentUser))
					  ParseTwitterUtils.link(currentUser, Login.this, linkAccountCallback); 
			}	
			else 
				ParseTwitterUtils.logIn(Login, twLoginCallback);
		}
	};

	// Login with fb
	private OnClickListener loginButtonListener = new OnClickListener() {
		
		public void onClick( View v ) {
			Log.d("check","fb");
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				if (!ParseFacebookUtils.isLinked(currentUser))
					ParseFacebookUtils.link(currentUser,Arrays.asList("create_event", Permissions.Extended.PUBLISH_STREAM), Login.this, linkFbAccountCallback);
			}	
			else
			ParseFacebookUtils.logIn(Arrays.asList("create_event", Permissions.Extended.PUBLISH_STREAM),Login, twLoginCallback);
		}
	};
				
	private LogInCallback twLoginCallback = new LogInCallback() {
		
		@Override
		public void done(ParseUser user, com.parse.ParseException arg1) {
			if (user == null) {
		      Log.d("MyApp", "Uh oh. The user cancelled the login.");
		    } else if (user.isNew()) {
		      Log.d("MyApp", "User signed up and logged in through Facebook");
		    } else {
		      Log.d("MyApp", "User logged in!");
		    }
			// If user is not null take to next screen (feed)
			if(user != null){
			 Intent i = new Intent(Login.this, NewsFeed.class);
			 startActivity(i);
			}
		}
	};
	
	// Login callback to link fb account with current user 
	private SaveCallback linkFbAccountCallback = new SaveCallback() {
		
		@Override
		public void done(com.parse.ParseException arg0) {
			ParseUser user = ParseUser.getCurrentUser();
			if (ParseFacebookUtils.isLinked(user)) {
		        Log.d("MyApp", "user logged in with Facebook and linked");
		        Intent i = new Intent(Login.this, NewsFeed.class);
				 startActivity(i);
		      }
		}
	};
	
	// Login callback to link twitter account with current user 
	private SaveCallback linkAccountCallback = new SaveCallback() {
	
		@Override
		public void done(com.parse.ParseException arg0) {
			ParseUser user = ParseUser.getCurrentUser();
			if (ParseTwitterUtils.isLinked(user)) {
		        Log.d("MyApp", "user logged in with Twitter and linked");
		        Intent i = new Intent(Login.this, NewsFeed.class);
				 startActivity(i);
		      }
			 
		}
	};
	
}