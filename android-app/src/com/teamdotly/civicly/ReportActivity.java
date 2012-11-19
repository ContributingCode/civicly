package com.teamdotly.civicly;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.teamdotly.civicly.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.facebook.AsyncFacebookRunner;
import com.parse.facebook.Facebook;
import com.parse.signpost.http.HttpResponse;
import com.parse.twitter.Twitter;

public class ReportActivity extends Activity {

	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final int ACTION_TAKE_PHOTO_S = 2;
	private static final int ACTION_TAKE_VIDEO = 3;

	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private ImageView mImageView;
	private Bitmap mImageBitmap;

	private static final String VIDEO_STORAGE_KEY = "viewvideo";
	private static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
	private VideoView mVideoView;
	private Uri mVideoUri;
	private EditText tweet;
	private CheckBox checkFB;
	private CheckBox checkTweet;
	
	
	private String mCurrentPhotoPath;

	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	private Location prev;
	private byte[] data;
	
	public String tweet_text;
	public String iurl;
	public ParseFile file;
	ParseObject prob=null;

	
	/* Photo album for this application */
	private String getAlbumName() {
		return getString(R.string.album_name);
	}
	
	private File getAlbumDir() {
		File storageDir = null;
      
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}		
		return storageDir;
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();		
		return f;
	}

	private void setPic() {
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = 200;
		int targetH = 200;

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		Log.d("height",photoH+"");
		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = 1;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mVideoUri = null;
		mImageView.setVisibility(View.VISIBLE);
		mVideoView.setVisibility(View.INVISIBLE);
	}

	private void galleryAddPic() {
		    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
			File f = new File(mCurrentPhotoPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
	}


	private void dispatchTakePictureIntent(int actionCode) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = null;
		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			try {
				f = setUpPhotoFile();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch
		Log.d("lol","before intent call");
		startActivityForResult(takePictureIntent, actionCode);
	}

	private void dispatchTakeVideoIntent() {
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(takeVideoIntent, ACTION_TAKE_VIDEO);		
	}


	private void handleSmallCameraPhoto(Intent intent) {

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = photoH/480;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		
        try{		  //Image to byte array 
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			data = bos.toByteArray();
			file = new ParseFile("problem_pic.png", data);
			file.saveInBackground();
			Log.d("sds","image taken ....");
        }catch(Exception e){}
	}

	private void handleCameraVideo(Intent intent) {
		mVideoUri = intent.getData();
		mVideoView.setVideoURI(mVideoUri);
		mImageBitmap = null;
		mVideoView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.INVISIBLE);
	}
		

	Button.OnClickListener mTakePicOnClickListener = 
		new Button.OnClickListener() {
		public void onClick(View v) {
		Location cl = showCurrentLocation();
		if(cl==null){
			Toast.makeText(getApplicationContext(), "Location could not be determied", Toast.LENGTH_LONG).show();
			return;
		}
			
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (checkTweet.isChecked()){
			if (!ParseTwitterUtils.isLinked(currentUser)){
				ParseTwitterUtils.link(currentUser, ReportActivity.this, linkAccountCallback);
				return;
			}
		}
		if (checkFB.isChecked())
		{
			if (!ParseFacebookUtils.isLinked(currentUser)){
				ParseFacebookUtils.link(currentUser,Arrays.asList("create_event", Permissions.Extended.PUBLISH_STREAM), ReportActivity.this, linkFbAccountCallback);
				return;
			}
		}
		ParseGeoPoint point = new ParseGeoPoint(cl.getLatitude(),cl.getLongitude());
		prob = new ParseObject("Problem");
		EditText tweet2 = (EditText) findViewById(R.id.editText1);
		tweet_text=tweet2.getText().toString();
		if(tweet_text.length()>120)
			return;
		prob.put("content", tweet2.getText().toString());
		prob.put("count", 1);
		if(point!=null)
			prob.put("location", point);
		prob.put("userid", currentUser.getObjectId());
		if(file != null){
			prob.put("image",file);
			iurl=file.getUrl();
		}
		else iurl="";
		Thread rt = new Thread(new ReportThread(prob)); 
		rt.start();
		
		
			if (checkTweet.isChecked())	
				{
				Log.d("checked","trueee");
				Thread t22 = new Thread(new tt(tweet2.getText().toString()));
				t22.start();
				}
			
			if (checkFB.isChecked())
			{
			Log.d("checked","trueee");
			Thread t2 = new Thread(new ft(tweet2.getText().toString()));
			t2.start();
			}
			Toast.makeText(getApplicationContext(), "posted", Toast.LENGTH_LONG).show();
			Intent i = new Intent(ReportActivity.this, NewsFeed.class);
			startActivity(i);
		}
	};

	
	Button.OnClickListener mTakePicSOnClickListener = 
		new Button.OnClickListener() {
		public void onClick(View v) {
			dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
		}
	};

	Button.OnClickListener mTakeVidOnClickListener = 
		new Button.OnClickListener() {
		public void onClick(View v) {
			dispatchTakeVideoIntent();
		}
	};
	
	
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
	 private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
		     
		 protected LocationManager locationManager;
		 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sec);
        data = null;
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mVideoView = (VideoView) findViewById(R.id.videoView1);
		checkFB = (CheckBox) findViewById(R.id.checkFB);
		checkTweet = (CheckBox) findViewById(R.id.checkTweet);
		tweet = (EditText) findViewById(R.id.editText1);
		
		mImageBitmap = null;
		mVideoUri = null;
		prev = null;
		
		
		Button picBtn = (Button) findViewById(R.id.btnIntend);
		setBtnListenerOrDisable( 
				picBtn, 
				mTakePicOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

		Button picSBtn = (Button) findViewById(R.id.btnIntendS);
		setBtnListenerOrDisable( 
				picSBtn, 
				mTakePicSOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

		Button cancelBtn = (Button) findViewById(R.id.cancelButton);
		 cancelBtn.setOnClickListener(new View.OnClickListener() {
	          public void onClick(View v) {
	        	  Intent i = new Intent(ReportActivity.this, NewsFeed.class);
	    			startActivity(i);
	          }
	      });

		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			        locationManager.requestLocationUpdates(
			                LocationManager.GPS_PROVIDER,
			                MINIMUM_TIME_BETWEEN_UPDATES,
			                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
			                new MyLocationListener()
			        		);
	}
	
	
	// To get google location reverse geocode
	public class ReportThread extends Thread{
		ParseObject p;
		AsyncHttpClient client;
		ReportThread(ParseObject p1)
		{
			p =p1;
		}
		@Override
		public void run(){
			ParseGeoPoint point = (ParseGeoPoint) p.get("location");
			if(point!=null)
				{
				client = new AsyncHttpClient();
				
				String req ="http://maps.googleapis.com/maps/api/geocode/json?latlng="+point.getLatitude()+","+point.getLongitude()+"&sensor=true";
				Log.d("req",req);
			client.get(req, new AsyncHttpResponseHandler() {
			    @Override
			    public void onSuccess(String response) {
			    	Log.d("resp",response);
			    	try {
						JSONObject data = new JSONObject(response);
						p.put("address", data.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        p.saveInBackground(cb);
			    }
			});
				}
			else 
				 p.saveInBackground(cb);
		}
	}
	
	
	
    // Post to fb 	
	public class ft extends Thread{
		String fb_text;
		ft(String t)
		{
			fb_text=t;
		}
		@Override
		public void run(){
		Log.d("inside","fb thread");
		Bundle parameters = new Bundle();
        parameters.putString("message", fb_text+" "+iurl);
        parameters.putString("description", "test");
        Facebook mfb = ParseFacebookUtils.getFacebook();
        
        try {
			mfb.request("me");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    
        try {
		String resp = mfb.request("me/feed", parameters, "POST");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
	
	
	
	public class tt extends Thread{ 
		
		String tweet_text;
		tt(String t)
		{
			tweet_text=t;
		}
		@Override
		public void run(){
			AccessToken a = new AccessToken(ParseTwitterUtils.getTwitter().getAuthToken(), ParseTwitterUtils.getTwitter().getAuthTokenSecret());
			// initialize Twitter4J
			twitter4j.Twitter twitter1 = new TwitterFactory().getInstance();
			twitter1.setOAuthConsumer(Constants.TW_CONSUMER_KEY, Constants.TW_CONSUMER_SECRET);
			twitter1.setOAuthAccessToken(a);
			try {
				twitter4j.Status status = twitter1.updateStatus(tweet_text+" #civicproblems "+iurl);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
 }
		

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	 protected Location showCurrentLocation() {
           Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
           if (location == null) {
        	   		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        	   		
           }
           if(!isBetterLocation( location, prev))
        	       location=prev;
           else
        	       prev = location;
           
	       if (location != null) {
	            String message = String.format(
	                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
	                    location.getLongitude(), location.getLatitude()
	            );
	        }
	       return location;

	    }   

	    public class MyLocationListener implements LocationListener {

	        public void onLocationChanged(Location location) {
	           
	        }

	        public void onStatusChanged(String s, int i, Bundle b) {
	          
	        }

	        public void onProviderDisabled(String s) {
	           
	        }

	        public void onProviderEnabled(String s) {
	          
	        }

	    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("lol","after intent call");
		switch (requestCode) {
		case ACTION_TAKE_PHOTO_B: {
			if (resultCode == RESULT_OK) {
				Log.d("info","after taking pic####");
				handleSmallCameraPhoto(data);
			}
			break;
		} // ACTION_TAKE_PHOTO_S

		case ACTION_TAKE_VIDEO: {
			if (resultCode == RESULT_OK) {
				handleCameraVideo(data);
			}
			break;
		} // ACTION_TAKE_VIDEO
		} // switch
	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
		mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(
				savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
		mVideoView.setVideoURI(mVideoUri);
		mVideoView.setVisibility(
				savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ? 
						ImageView.VISIBLE : ImageView.INVISIBLE
		);
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
	 *
	 * @param context The application's environment.
	 * @param action The Intent action to check for availability.
	 *
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);        	
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	// Login callback to link fb account with current user 
	private SaveCallback linkFbAccountCallback = new SaveCallback() {
		
		@Override
		public void done(com.parse.ParseException arg0) {
			ParseUser user = ParseUser.getCurrentUser();
			if (ParseFacebookUtils.isLinked(user)) {
		        Log.d("MyApp", "user logged in with Facebook and linked before report");
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
		      }
			 
		}
	};
	
		// send notification to web app about the new post 	
		private SaveCallback cb = new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				// TODO Auto-generated method stub
			   Log.d("hssi",prob.getObjectId());
			   AsyncHttpClient client = new AsyncHttpClient();
			   String urls = "http://civicly.cloudfoundry.com/push/"+prob.getObjectId();
			   
			   client.get(urls, new AsyncHttpResponseHandler() {
			       @Override
			       public void onSuccess(String response) {
			    	   Log.d("hssi","pushed id to web app");
			       }
			   });
			}
		};
		

}