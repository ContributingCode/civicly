package com.teamdotly.civicly;

import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ocpsoft.pretty.time.PrettyTime;

import com.fedorvlasov.lazylist.ImageLoader;
import com.parse.ParseFile;
import com.parse.ParseObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class LiAdapter extends ArrayAdapter {
    private final Activity activity;
    private final List<ParseObject> issues;
    
    public ImageLoader imageLoader; 
 
    public LiAdapter(Activity activity, List<ParseObject> objects) {
        super(activity, R.layout.row_list , objects);
        this.activity = activity;
        this.issues = objects;
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        StockQuoteView sqView = null;
 
        if(rowView == null)
        {
            // Get a new instance of the row layout view
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_list, null);
 
            // Hold the view objects in an object,
            // so they don't need to be re-fetched
            sqView = new StockQuoteView();

            sqView.title = (TextView) rowView.findViewById(R.id.title);
            sqView.image = (ImageView) rowView.findViewById(R.id.imageView2);
            sqView.count = (TextView) rowView.findViewById(R.id.textView3);
            sqView.time = (TextView) rowView.findViewById(R.id.textView1);
            sqView.loc = (TextView) rowView.findViewById(R.id.textView2);
            

            // Cache the view objects in the tag,
            // so they can be re-accessed later
            rowView.setTag(sqView);
        } else {
            sqView = (StockQuoteView) rowView.getTag();
        }
 
        // Transfer the problem data from the data object
        // to the view objects

        ParseObject problem = (ParseObject) issues.get(position);
        sqView.title.setText((CharSequence) problem.get("content"));
        	sqView.count.setText((CharSequence) (""+problem.get("count")));
        	sqView.loc.setText((CharSequence) (""+problem.get("address")));
        PrettyTime p = new PrettyTime();
        sqView.time.setText(p.format(problem.getCreatedAt()));
        
        String image_url =""; 
		if(problem.get("image") != null)
			image_url = ((ParseFile) problem.get("image")).getUrl();
        
        
        	imageLoader.DisplayImage(image_url , sqView.image);
        
        return rowView;
    }
 
    protected static class StockQuoteView {

        protected TextView title;
        protected ImageView image;
        protected TextView count;
        protected TextView time;
        protected TextView loc;

    }
}
