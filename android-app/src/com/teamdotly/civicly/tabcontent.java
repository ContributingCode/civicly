package com.teamdotly.civicly;

import java.util.Iterator;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class tabcontent extends ListActivity {
    private Activity activity = this;
    int ch;
    public ProgressDialog dialog;
    
    @Override 
    public void onResume(){
    	super.onResume();

         ParseQuery query = new ParseQuery("Problem");
         ParseUser user = ParseUser.getCurrentUser();
         query.setLimit(10);
         switch(ch){
         case 0:
      	   		Location cl = MyLocation.getLocation(this.getApplicationContext());
      	   		if(cl!=null){
      	   		ParseGeoPoint point = new ParseGeoPoint(cl.getLatitude(),cl.getLongitude());
      	   		query.whereWithinMiles("location", point, 2);
      	   		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
      	   		query.findInBackground(fc);
      	   		}
      	   		else{
      	   		query.addDescendingOrder("createdAt");
      	   		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
      	   		query.findInBackground(fc);
      	   		}
      	   		break;
         case 1:
      	        query.addDescendingOrder("createdAt");
      	        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
      	   		query.findInBackground(fc);
      	   		break;
         case 2:
      	   		query.addDescendingOrder("createdAt");
      	   		query.whereEqualTo("userid", user.getObjectId());
  	   		   query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
  	   		   query.findInBackground(fc);
      	   		break;      
         }
      
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
       setContentView(R.layout.tabcontent);
       dialog = ProgressDialog.show(tabcontent.this, "", 
               "Loading. Please wait...", true);

        ch = Integer.parseInt(getIntent().getStringExtra("content"));
       ParseQuery query = new ParseQuery("Problem");
       ParseUser user = ParseUser.getCurrentUser();
       query.setLimit(10);
       switch(ch){
       case 0:
   	   		Location cl = MyLocation.getLocation(this.getApplicationContext());
    	   		if(cl!=null){
    	   		ParseGeoPoint point = new ParseGeoPoint(cl.getLatitude(),cl.getLongitude());
    	   		query.whereWithinMiles("location", point, 2);
    	   		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
    	   		query.findInBackground(fc);
    	   		}
    	   		else{
    	   		query.addDescendingOrder("createdAt");
    	   		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
    	   		query.findInBackground(fc);
    	   		}
    	   		break;
       case 1:
    	        query.addDescendingOrder("createdAt");
    	        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
    	   		query.findInBackground(fc);
    	   		break;
       case 2:
    	   		query.addDescendingOrder("createdAt");
    	   		query.whereEqualTo("userid", user.getObjectId());
    	   		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
    	   		query.findInBackground(fc);
    	   		break;      
       } 
    }
    
    private FindCallback fc = new FindCallback(){
        
		@Override
		public void done(final List<ParseObject> scoreList, ParseException e) {
			if (e == null) {
				dialog.dismiss();
	            Log.d("score", "Retrieved " + scoreList.size() + " scores");
	            	setListAdapter(new LiAdapter(activity,scoreList));	
	            	ListView lv = getListView();
	            	lv.setOnItemLongClickListener(new OnItemLongClickListener(){
          
						public boolean onItemLongClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub
							ParseObject obj =  scoreList.get(arg2);
							Log.d("objeeee", scoreList.get(arg2).getObjectId());
							obj.increment("count");
							obj.saveInBackground();
							return false;
						}
	            	});
	            	
	              	lv.setOnItemClickListener(new OnItemClickListener(){


						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Intent intent_item = new Intent(tabcontent.this, ItemView.class);
							ParseObject obj =  scoreList.get(arg2);
							PrettyTime p = new PrettyTime();
							String image_url =""; 
							if(obj.get("image") != null)
								image_url = ((ParseFile) obj.get("image")).getUrl();
							intent_item.putExtra("title", obj.getString("content"));
							intent_item.putExtra("loc", obj.getString("address"));
							intent_item.putExtra("vote", obj.getInt("count"));
							intent_item.putExtra("parseobjectid", obj.getObjectId());
							intent_item.putExtra("image_url",image_url);
							intent_item.putExtra("time", p.format(obj.getCreatedAt()));
							startActivity(intent_item);
						}
	            	});

	        } else {
	            Log.d("score", "Error: " + e.getMessage());
	            Log.d("score", "Retrieved " + scoreList.size() + " scores");
	        }		// TODO Auto-generated method stub
			
		}
    };
}
