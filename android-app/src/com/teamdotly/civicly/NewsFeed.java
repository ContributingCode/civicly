package com.teamdotly.civicly;


import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.teamdotly.civicly.R;
import android.content.Context;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.app.TabActivity;


public class NewsFeed extends TabActivity {

    // Divide 1.0 by # of tabs needed
    // In this case: 1.0/2 => 0.5
    private static final android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 0.5f);

    private static TabHost tabHost;
    private static TabHost.TabSpec spec;
    private static Intent intent;
    private static LayoutInflater inflater;

    private View tab;
    private TextView label;
    private TextView divider;
    public Location loc;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed);
                 
        Button reportBtn = (Button)findViewById(R.id.reportBtn);
        
        reportBtn.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(NewsFeed.this, ReportActivity.class);
				startActivity(i);
			}
			
		});
        
        
        // Get inflator so we can start creating the custom view for tab
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Get tab manager
        tabHost = getTabHost();
        
        // This converts the custom tab view we created and injects it into the tab widget
        tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        // Mainly used to set the weight on the tab so each is equally wide
        tab.setLayoutParams(params);
        // Add some text to the tab
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("TRENDING");
        // Show a thick line under the selected tab (there are many ways to show
        // which tab is selected, I chose this)
        divider = (TextView) tab.findViewById(R.id.tabSelectedDivider);
        divider.setVisibility(View.VISIBLE);
        // Intent whose generated content will be added to the tab content area
        intent = new Intent(NewsFeed.this, tabcontent.class);
        // Just some data for the tab content activity to use (just for demonstrating changing content)
        intent.putExtra("content", "0");
        // Finalize the tabs specification
        spec = tabHost.newTabSpec("trending").setIndicator(tab).setContent(intent);
        // Add the tab to the tab manager
        tabHost.addTab(spec);
        
        
        // Add another tab
        tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        tab.setLayoutParams(params);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("LATEST");
        intent = new Intent(NewsFeed.this, tabcontent.class);
        intent.putExtra("content", "1");
        spec = tabHost.newTabSpec("latest").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);
        
        // Add another tab
        tab = inflater.inflate(R.layout.tab, getTabWidget(), false);
        tab.setLayoutParams(params);
        label = (TextView) tab.findViewById(R.id.tabLabel);
        label.setText("MY ISSUES");
        intent = new Intent(NewsFeed.this, tabcontent.class);
        intent.putExtra("content", "2");
        spec = tabHost.newTabSpec("myissues").setIndicator(tab).setContent(intent);
        tabHost.addTab(spec);
        
        
        // Listener to detect when a tab has changed. I added this just to show 
        // how you can change UI to emphasize the selected tab
        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tag) {
                // reset some styles
                clearTabStyles();
                View tabView = null;
                // Use the "tag" for the tab spec to determine which tab is selected
                if (tag.equals("trending")) {
                    tabView = getTabWidget().getChildAt(0);
                }
                else if (tag.equals("latest")) {
                    tabView = getTabWidget().getChildAt(1);
                }
                else if (tag.equals("myissues")) {
                    tabView = getTabWidget().getChildAt(2);
                }
                tabView.findViewById(R.id.tabSelectedDivider).setVisibility(View.VISIBLE);
            }       
        });
    }
    
    private void clearTabStyles() {
        for (int i = 0; i < getTabWidget().getChildCount(); i++) {
            tab = getTabWidget().getChildAt(i);
            tab.findViewById(R.id.tabSelectedDivider).setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onBackPressed() {

       return;
    }
}
