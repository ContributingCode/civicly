package com.teamdotly.civicly;

import com.fedorvlasov.lazylist.ImageLoader;
import com.parse.*;
import com.teamdotly.civicly.LiAdapter.StockQuoteView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ItemView extends Activity {

	protected TextView title;
	private Button vote_up;
	private int vote_count;

	View itemview;
	TextView count_view,count_view1;
	ImageLoader imageLoader;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);
        imageLoader = new ImageLoader(this);
        TextView tv = (TextView) findViewById(R.id.issue);
        TextView time_view = (TextView) findViewById(R.id.textView1);
        TextView loc = (TextView) findViewById(R.id.itemLoc);
        ImageView image_view = (ImageView) findViewById(R.id.imageView1);
        count_view1 = (TextView) findViewById(R.id.textView3);
        this.vote_up = (Button) findViewById(R.id.voteup);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
    		//return;
    		}
    // Get data via the key
    String value1 = extras.getString("title");
    vote_count = extras.getInt("vote");
    String image_url = extras.getString("image_url");
    String timedata = extras.getString("time");
    
    final String object_id = extras.getString("parseobjectid");
   
    tv.setText(value1);
    if(image_url.length() > 0)
    		imageLoader.DisplayImage(image_url , image_view);
    count_view1.setText(vote_count+"");
    time_view.setText(timedata);
    loc.setText(extras.getString("loc"));
    
    // back buttom 
    Button cancelBtn = (Button) findViewById(R.id.backButton);
	 cancelBtn.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
       	  Intent i = new Intent(ItemView.this, NewsFeed.class);
   			startActivity(i);
         }
     });

    //click vote up
    this.vote_up.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	vote_count++;
        	count_view = (TextView) findViewById(R.id.textView3);
        	count_view.setText(vote_count+"");
        	ParseQuery query = new ParseQuery("Problem");
        	query.getInBackground(object_id, new GetCallback() {
        	  public void done(ParseObject object, ParseException e) {
        	    if (e == null) {
        	    	object.put("count", vote_count);
        	    	object.saveInBackground();
        	    } else {
        	      // something went wrong
        	    }
        	  }
        	});

        }
      });        
    }
}
